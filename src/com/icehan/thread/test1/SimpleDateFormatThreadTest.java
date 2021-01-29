package com.icehan.thread.test1;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SimpleDateFormatThreadTest {
//    private final SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    ThreadPoolExecutor poolExecutor =  new ThreadPoolExecutor(10,100,1,TimeUnit.MINUTES,new LinkedBlockingQueue<>(1000));
//
//    public void test() {
//        while (true) {
//            poolExecutor.execute(() -> {
//                String dateString = simpleDateFormat.format(new Date());
//                try {
//                    Date parseDate = simpleDateFormat.parse(dateString);
//                    String dateString2 = simpleDateFormat.format(parseDate);
//                    System.out.println(dateString.equals(dateString2));
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//            });
//        }
//    }


    private static final ThreadLocal<SimpleDateFormat> THREAD_LOCAL = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };
    //    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(10, 100, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<>(1000));

    public void test() {
        while (true) {
            poolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    SimpleDateFormat simpleDateFormat = THREAD_LOCAL.get();
                    if (simpleDateFormat == null) {
                        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    }
                    String dateString = simpleDateFormat.format(new Date());
                    try {
                        Date parseDate = simpleDateFormat.parse(dateString);
                        String dateString2 = simpleDateFormat.format(parseDate);
                        System.out.println(dateString.equals(dateString2));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    } finally {
                        THREAD_LOCAL.remove();
                    }
                }
            });
        }
    }


    public static void main(String[] args) {
        SimpleDateFormatThreadTest simpleDateFormatThreadTest = new SimpleDateFormatThreadTest();
        simpleDateFormatThreadTest.test();
    }

}
