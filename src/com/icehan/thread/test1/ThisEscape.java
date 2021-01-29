package com.icehan.thread.test1;

import java.util.ArrayList;
import java.util.List;

public class ThisEscape {
    private final int id;
    private final String name;
    public ThisEscape(EventSource<EventListener> source) {
        id = 1;
        source.registerListener(new EventListener() {
            public void onEvent(Event e) {
                System.out.println("id: "+ThisEscape.this.id);
                System.out.println("name: "+ThisEscape.this.name);
            }
        });
        //此处sleep一秒模拟线程切换
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        name = "ok";
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
     * 构造函数中使用内部类
     * 暴露内部类的同时也暴露出内部类隐含的外部类this对象
     * @param args
     */
    public static void main(String[] args) {
        EventSource<EventListener> eventListenerEventSource = new EventSource<EventListener>();
        ListenerRunnable listenerRunnable = new ListenerRunnable(eventListenerEventSource);
        Thread thread = new Thread(listenerRunnable);
        thread.start();
        ThisEscape thisEscape = new ThisEscape(eventListenerEventSource);
    }

}
