package com.icehan.thread.gui;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.*;

public class SwingUtilities {

    private static final ExecutorService exec = Executors.newSingleThreadExecutor(new SwingThreadFactory());
    private static volatile Thread swingThread;

    private static class SwingThreadFactory implements ThreadFactory{
        @Override
        public Thread newThread(Runnable r) {
            swingThread = new Thread(r);
            return swingThread;
        }
    }

    public static boolean isEventDispatchThread(){
        return Thread.currentThread() == swingThread;
    }

    public static void invokerLater(Runnable task){
        exec.execute(task);
    }

    public static void invokeAndWait(Runnable task) throws InvocationTargetException,InterruptedException {
        Future<?> result = exec.submit(task);
        try {
            result.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new InvocationTargetException(e);
        }
    }

}
