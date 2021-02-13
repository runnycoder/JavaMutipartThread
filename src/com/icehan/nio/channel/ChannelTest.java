package com.icehan.nio.channel;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;

public class ChannelTest {

    @Test
    public void test() throws IOException {
        RandomAccessFile rw = new RandomAccessFile("/Users/runny/Documents/daily_work/2021-02-10/channel_test.txt", "rw");
        FileChannel channel = rw.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(48);

        int read = channel.read(buffer);//read into buffer
        while (read!=-1){
            System.out.println("Read:"+read);
            buffer.flip();//make buffer ready to read
            while (buffer.hasRemaining()){
                System.out.print((char)buffer.get());
            }
            System.out.println();
            buffer.clear();
            read = channel.read(buffer);
            System.out.println("Read:"+read);
        }
        rw.close();
    }

    /**
     * Selector测试
     * 服务端模版代码
     * @throws IOException
     */
    @Test
    public void test1() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress("localhost", 8080));
        serverSocketChannel.configureBlocking(false);//非阻塞式channel

        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);//注册通道到选择器

        while(true){
            int readyNum = selector.select();
            if(readyNum==0){
                continue;
            }
            Set<SelectionKey> readySets = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readySets.iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                if(key.isAcceptable()){
                    System.out.println("接受链接");
                }else if(key.isReadable()){
                    System.out.println("通道可读");
                }else if(key.isWritable()){
                    System.out.println("通道可写");
                }
                iterator.remove();
            }
        }
    }

    /**
     * selector实现服务端监听
     */
    @Test
    public void webServer(){
        try{
            //server channel init
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", 8000));
            serverSocketChannel.configureBlocking(false);

            Selector selector = Selector.open();
            //channel register
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            //reader writer allocate

            ByteBuffer reader = ByteBuffer.allocate(1024);
            ByteBuffer writer = ByteBuffer.allocate(1024);
            writer.put("received".getBytes());
            writer.flip();

            while (true){
                int acceptNums = selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeys.iterator();
                while (it.hasNext()){
                    SelectionKey key = it.next();
                    it.remove();
                    if(key.isAcceptable()){
                        //如果有链接请求 建立一个只读渠道注册到selector上
                        SocketChannel acceptChannel = serverSocketChannel.accept();
                        acceptChannel.configureBlocking(false);
                        acceptChannel.register(selector, SelectionKey.OP_READ);
                    }else if(key.isReadable()){
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        reader.clear();
                        socketChannel.read(reader);//读取渠道信息到缓存区域
                        reader.flip();
                        System.out.println("webServer received: "+new String(reader.array()));
                        key.interestOps(SelectionKey.OP_WRITE);//设置为只写模式
                    }else if(key.isWritable()){
                        writer.rewind();//响应客户端
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        socketChannel.write(writer);
                        key.interestOps(SelectionKey.OP_READ);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * selector实现客户端发送
     */
    @Test
    public void webClient(){
        try{
            SocketChannel clientChannel = SocketChannel.open();
            clientChannel.connect(new InetSocketAddress("127.0.0.1",8000));

            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            ByteBuffer writeBuffer = ByteBuffer.allocate(1024);

            writeBuffer.put("hello Im client".getBytes());
            writeBuffer.flip();
            while (true){
                writeBuffer.rewind();
                clientChannel.write(writeBuffer);
                readBuffer.clear();
                int read = clientChannel.read(readBuffer);
//                while (read!=-1){
//                    System.out.println("Client Read:"+read);
//                    readBuffer.flip();//make buffer ready to read
//                    while (readBuffer.hasRemaining()){
//                        System.out.print((char)readBuffer.get());
//                    }
//                    System.out.println();
//                    readBuffer.clear();
//                    read = clientChannel.read(readBuffer);
//                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
