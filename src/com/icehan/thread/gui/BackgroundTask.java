package com.icehan.thread.gui;

import java.util.concurrent.*;

abstract class BackgroundTask<V> implements Runnable, Future<V> {

    private final FutureTask<V> computation = new Computation();

    private class Computation extends FutureTask<V>{
        public Computation() {
            super(new Callable<V>() {
                @Override
                public V call() throws Exception {
                    return BackgroundTask.this.compute();
                }
            });
        }
        //FutureTask任务执行完成之后的回调
        protected final void done(){
            GuiExecutor.instance().execute(new Runnable() {
                @Override
                public void run() {
                    V value = null;
                    Throwable thrown = null;
                    boolean cancelled = false;
                    try {
                        value = get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        cancelled=true;
                    } catch (ExecutionException e) {
                        thrown = e;
                    } finally {
                        onCompletion(value, thrown, cancelled);
                    }
                }
            });
        }
    }

    protected void setProgress(final int current,final int max){
        GuiExecutor.instance().execute(new Runnable() {
            @Override
            public void run() {
                onProgress(current, max);
            }
        });
    }

    //在后台线程中调用
    protected abstract V compute() throws Exception;

    //在事件线程中调用
    protected void onCompletion(V result,Throwable exception,boolean cancelled){

    }

    protected void onProgress(int current,int max){
        //其它用于计算的方法
    };




    @Override
    public void run() {

    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return null;
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }


}
