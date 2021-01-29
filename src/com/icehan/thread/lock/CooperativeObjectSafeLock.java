package com.icehan.thread.lock;

import java.util.HashSet;
import java.util.Set;

/**
 * 当持有锁的时候调用外部方法是一件很危险的事
 * 因为你不能确定外部方法是否会需要持有本对象的锁 造成死锁
 * 所以最好的方法就是在持有锁的时候不要调用外部方法
 */
public class CooperativeObjectSafeLock {
    class Taxi{
        private String location,destination;
        private final Dispatcher dispatcher;

        public Taxi(Dispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }

        public synchronized String getLocation(){
            return location;
        }

        /**
         * 此处存在疑问 锁内置之后为何还要在方法上添加synchronized关键字呢
         * 是不是因为下面dispatcher类的getImage方法 避免了直接调用Taxi对象的方法从而避免了死锁？
         */
        public synchronized void setLocation(String location){
            boolean reachedDestination;
            synchronized (this){
                this.location = location;
                reachedDestination = location.equals(destination);
            }

            if(reachedDestination){
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
            Set<Taxi> copy;
            synchronized (this){
                copy = new HashSet<Taxi>(taxis);
            }
            Image image = new Image();
            for (Taxi t:
                    copy) {
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
