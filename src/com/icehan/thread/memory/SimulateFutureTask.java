package com.icehan.thread.memory;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class SimulateFutureTask<V> {

    private final class Sync extends AbstractQueuedSynchronizer{
        private static final int RUNNING = 1,RAN = 2,CANCELED = 4;
        private V result;
        private Exception exception;
        private volatile boolean isCanceled;

        void innerSet(V v){
            while (true){
                int s = getState();
                if(isCanceled){
                    return;
                }
                if(compareAndSetState(s, RAN)){
                    break;
                }
            }
            result = v;
            releaseShared(0);
        }

        V innerGet() throws InterruptedException, ExecutionException {
            acquireInterruptibly(0);
            if(getState()==CANCELED){
                throw new CancellationException();
            }
            if(exception!=null){
                throw new ExecutionException(exception);
            }
            return result;
        }
    }
}
