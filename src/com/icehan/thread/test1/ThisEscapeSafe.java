package com.icehan.thread.test1;

import java.util.ArrayList;
import java.util.List;

public class ThisEscapeSafe {
    private final int id;
    private final String name;
    private final EventListener listener;
    private ThisEscapeSafe() {
        id = 1;
        listener = new EventListener() {
            public void onEvent(Event e) {
                System.out.println("id: "+ ThisEscapeSafe.this.id);
                System.out.println("name: "+ ThisEscapeSafe.this.name);
            }
        };
        //此处sleep一秒模拟线程切换
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        name = "ok";
    }

    public static ThisEscapeSafe getInstance(EventSource<EventListener> source){
        ThisEscapeSafe thisEscapeSafe = new ThisEscapeSafe();
        source.registerListener(thisEscapeSafe.listener);
        return thisEscapeSafe;
    }



    static class EventSource<T> {
        private final List<T> eventListeners;
        public EventSource(){
            eventListeners = new ArrayList<T>();
        }

        public  synchronized void registerListener(T eventListener){
            this.eventListeners.add(eventListener);
            this.notifyAll();
        }

        public synchronized List<T> retriveveListeners() throws InterruptedException {
            List<T> dest = null;
            if(eventListeners.size()<=0){
                this.wait();
            }
            dest = new ArrayList<T>(eventListeners.size());
            dest.addAll(eventListeners);
            return dest;
        }
    }

    interface EventListener {
        void onEvent(Event e);
    }

    static class Event {
    }

    static class ListenerRunnable implements Runnable{

        private EventSource<EventListener> source;
        public ListenerRunnable(EventSource<EventListener> source){
            this.source = source;
        }

        @Override
        public void run() {
            List<EventListener> listeners = null;
            try {
                listeners = this.source.retriveveListeners();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (EventListener listener:
                 listeners) {
                listener.onEvent(new Event());
            }
        }
    }

    /**
     * this逃逸的原因是因为 在对象尚未初始化完毕的时候就暴露给了外部去使用
     * 无论是在构造函数中创建内部类还是新开一个线程 都要保证 创建与激活相分离
     * 私有化构造函数 提供公共的创建对象方式 在构造函数中注册 构造函数执行完毕后启动
     * @param args
     */
    public static void main(String[] args) {
        EventSource<EventListener> eventListenerEventSource = new EventSource<EventListener>();
        ListenerRunnable listenerRunnable = new ListenerRunnable(eventListenerEventSource);
        Thread thread = new Thread(listenerRunnable);
        thread.start();
        ThisEscapeSafe instance = ThisEscapeSafe.getInstance(eventListenerEventSource);
    }

}
