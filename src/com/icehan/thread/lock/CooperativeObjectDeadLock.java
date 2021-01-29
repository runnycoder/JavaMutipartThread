package com.icehan.thread.lock;


import java.util.HashSet;
import java.util.Set;

/**
 * 有时获取锁的顺序不像在同一个方法中那么容易观察
 * 可能多个协作的对象之间也会有互持锁的情况
 * 假设一个车辆到站调用setLocation 此时占用了 Taxi锁需要dispatcher锁
 * 同时又有一个线程调用getImage 此时占用dispatcher锁 要获取Taxi锁 就可能会出现死锁的情况
 */
public class CooperativeObjectDeadLock {
    class Taxi{
        private String location,destination;
        private final Dispatcher dispatcher;

        public Taxi(Dispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }

        public synchronized String getLocation(){
            return location;
        }

        public synchronized void setLocation(String location){
            this.location = location;
            if(location.equals(destination)){
                dispatcher.notifyAvailable(this);
            }
        }

    }

    class Dispatcher{
        private final Set<Taxi> taxis;
        private final Set<Taxi> availableTaxis;

        public Dispatcher() {
            this.taxis = new HashSet<Taxi>();
            this.availableTaxis = new HashSet<Taxi>();
        }

        public synchronized void notifyAvailable(Taxi taxi){
            availableTaxis.add(taxi);
        }

        public synchronized Image getImage(){
            Image image = new Image();
            for (Taxi t:
                 taxis) {
                image.drawMarker(t.getLocation());
            }
            return image;
        }
    }

    class Image{
        //根据位置返回图像
        public Image drawMarker(String location){
            return new Image();
        }
    }
}
