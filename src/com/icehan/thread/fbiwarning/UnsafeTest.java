package com.icehan.thread.fbiwarning;

import org.junit.Test;
import sun.misc.Unsafe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * 由于线程同步类的锁和线程阻塞等许多操作都用到了这个Unsafe类
 * 所以看看都有那些方法
 */
public class UnsafeTest{

    private static Unsafe unsafe = null;
    private static Field getUnsafe = null;

    static {
        try {
            getUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            getUnsafe.setAccessible(true);
            unsafe = (Unsafe) getUnsafe.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test1() throws NoSuchFieldException, IllegalAccessException {
        //基于反射获取Unsafe的实例
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        Unsafe unsafe = (Unsafe) f.get(null);
        System.out.println(unsafe);
    }

    /**
     * java中每个对象都有自己的对象头 这个要占一定的内存
     * arrayBaseOffset返回的是数组对象中首个元素的内存偏移位置
     * arrayIndexScale返回的是元素与元素之间的相对偏移量
     */
    @Test
    public void arrayOperation(){
        String[] strings = new String[]{"1","2","3","4"};
        int[] ints = new int[]{1,2,3,4};
        int i = unsafe.arrayBaseOffset(String[].class);
        System.out.println("string[] base offset is "+ i);

        int j = unsafe.arrayBaseOffset(int[].class);
        System.out.println("int[] base offset is "+ j);

        //every index scale
        long scale = unsafe.arrayIndexScale(String[].class);
        System.out.println("string[] index scale is " + scale);

        long intscale = unsafe.arrayIndexScale(int[].class);
        System.out.println("int[] index scale is " + intscale);

        //print first string in strings[]
        System.out.println("String[] first element is :" + unsafe.getObject(strings, i));
        System.out.println("int[] first element is :" + unsafe.getObject(strings, j));

        //print first string in strings[]
        System.out.println("sec element is :" + unsafe.getObject(strings, i+scale*1));

        //set 100 to first string
        unsafe.putObject(strings, i + scale * 0, "100");

        //print first string in strings[] again
        System.out.println("after set ,first element is :" + unsafe.getObject(strings, i + scale * 0));

    }

    @Test
    public void objectOperation() throws InstantiationException, NoSuchFieldException {
        /**
         * 通过class类创建类对象(allocateInstance),获取对象属性的偏移量(objectFieldOffset)
         * 通过偏移量设置属性值(putObject)
         */
        Data data = (Data)unsafe.allocateInstance(Data.class);
        data.setId(111);
        data.setName("unsafe");
        System.out.println(data);

        //返回成员变量相对于对象的内存地址偏移量
        Field idField = Data.class.getDeclaredField("id");
        Field nameField = Data.class.getDeclaredField("name");
        Field stateField = Data.class.getDeclaredField("state");

        long idFieldOffset = unsafe.objectFieldOffset(idField);
        long nameFieldOffset = unsafe.objectFieldOffset(nameField);
        long stateFieldOffset = unsafe.objectFieldOffset(stateField);

        System.out.println("id offset is "+idFieldOffset);
        System.out.println("name offset is "+nameFieldOffset);
        System.out.println("state offset is "+stateFieldOffset);

        //使用putLong,putInt,putDouble,putChar,putObject等方法可以直接修改内存数据(越过访问权限)
        unsafe.putObject(data, nameFieldOffset, "safe?");
        System.out.println(data.getName());

    }

    /**
     * 同样我们也可以通过Unsafe类 将一个class类的字节码文件读取到内存中 创建类对象
     *
     */
    @Test
    public void classOperation() throws IOException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
       File classFile = new File("/Users/runny/Documents/idea-workspace/JavaMutipartThread/out/production/JavaMutipartThread/com/icehan/thread/fbiwarning/Data.class");
       FileInputStream in;
       try( FileInputStream input = new FileInputStream(classFile);){//带资源块的try当try块退出的时候会自动调用res.close()
           in = input;
           byte[] classContent = new byte[(int) classFile.length()];
           input.read(classContent);

           Class<?> dataClass = unsafe.defineClass(null, classContent, 0, classContent.length, null, null);
           Object getId = dataClass.getMethod("getId").invoke(dataClass.newInstance(), null);
           System.out.println(getId);
       }
       in.read();//此处再调用read()会抛出 Stream Closed说明上面的流已经被关闭了
       System.out.println(in);
    }

    @Test
    public void getOSInfo(){
        /**
         *
         *   os内存信息获取
         *  可以获取地址大小（addressSize），页大小（pageSize），基本类型数组的偏移量
         *  （Unsafe.ARRAY_INT_BASE_OFFSET\Unsafe.ARRAY_BOOLEAN_BASE_OFFSET等）、
         *  基本类型数组内元素的间隔（Unsafe.ARRAY_INT_INDEX_SCALE\Unsafe.ARRAY_BOOLEAN_INDEX_SCALE等）
         */


        //get os address size
        System.out.println("address size is :" + unsafe.addressSize());
        //get os page size
        System.out.println("page size is :" + unsafe.pageSize());
        //int array base offset
        System.out.println("unsafe array int base offset:" + Unsafe.ARRAY_INT_BASE_OFFSET);

    }

    /**
     * 内存直接操作 类似于C++的allocate()函数
     */
    @Test
    public void memoryOperation() throws InterruptedException {

        //分配一个8byte字节的内存
        long address = unsafe.allocateMemory(8L);
        System.out.println(Long.toHexString(address));
//        System.out.println("address byte to memory: "+ Long.toBinaryString( unsafe.getLong(address)) );
        //初始化内存 设定每个字节值为15
        unsafe.setMemory(address, 8L,(byte)15);
        System.out.println( unsafe.getInt(address));
        System.out.println("address int toBinary to memory: "+ Integer.toBinaryString( unsafe.getInt(address)));
        System.out.println("address long toBinary to memory: "+ Long.toBinaryString( unsafe.getLong(address)) );
        System.out.println("address long toHex to memory: "+ Long.toHexString( unsafe.getLong(address)) );

        unsafe.freeMemory(address);//调用freeMemory释放内存是需要一点时间的 所以要等待一下
        Thread.sleep(3000);
        System.out.println("address memory free after");
        /**
         * 注意：内存释放之后此处的内存将不在受控制 可能有其它内容也可能为0 所以直接修改内存是很危险的行为
         */
        System.out.println("address byte to memory: "+ Long.toBinaryString( unsafe.getLong(address)) );
        System.out.println("address int toBinary to memory: "+ Integer.toBinaryString( unsafe.getInt(address)));
        System.out.println("address long toBinary to memory: "+ Long.toBinaryString( unsafe.getLong(address)) );
        System.out.println("address long toHex to memory: "+ Long.toHexString( unsafe.getLong(address)) );

    }

    @Test
    public void CASOperation() throws NoSuchFieldException {
        /**
         * CAS操作
         * Compare And Swap（比较并交换），当需要改变的值为期望的值时，那么就替换它为新的值，是原子
         * （不可在分割）的操作。很多并发框架底层都用到了CAS操作，CAS操作优势是无锁，可以减少线程切换耗费
         * 的时间，但CAS经常失败运行容易引起性能问题，也存在ABA问题。在Unsafe中包含compareAndSwapObject、
         * compareAndSwapInt、compareAndSwapLong三个方法，compareAndSwapInt的简单示例如下。
         */
        Data data = new Data();
        data.setId(1L);
        Field id = data.getClass().getDeclaredField("id");
        long l = unsafe.objectFieldOffset(id);
        System.out.println("id offset is "+ l);
        id.setAccessible(true);
        //比较并交换，比如id的值如果是所期望的值1，那么就替换为2，否则不做处理
        //l是字段在对象中的相对偏移位置 如果这个位置是确定的我们也可以直接设置这个值(本机测试这个位置是16)

        System.out.println(unsafe.compareAndSwapLong(data,l,1L,2L)+ "=== " +data.getId());

        System.out.println(unsafe.compareAndSwapLong(data,16,1L,3L)+ "=== " +data.getId());

        System.out.println(unsafe.compareAndSwapLong(data,16,2L,3L)+ "=== " +data.getId());

    }

    /**
     * 线程操作
     * 可以通过park阻塞线程
     * 通过unpark释放阻塞的线程
     */
    @Test
    public void threadOperation() throws InterruptedException {
        Thread thread = new Thread(() -> {
            long start = System.nanoTime();
            System.out.println(Thread.currentThread().getName()+"in running~");
            unsafe.park(false,0);//不加时间限制就一直等下去
            //unsafe.park(false,3000000000L);//false是相对时间 单位纳秒
            //true是绝对时间单位毫秒
//            unsafe.park(true, System.currentTimeMillis()+3000);
            System.out.println("main thread end cost times="+(System.nanoTime()-start)+"/ns");
        });

        thread.start();
        TimeUnit.SECONDS.sleep(4);//主线程等待 时间大于3s则 线程自动唤醒 小于3s 则会被unpark方法唤醒
        unsafe.unpark(thread);
    }



}
