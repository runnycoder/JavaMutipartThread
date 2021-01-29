package com.icehan.thread.cas;

import java.util.concurrent.atomic.AtomicReference;

public class LinkedQueue<E> {
    private static class Node<E>{
        final E item;
        final AtomicReference<Node<E>> next;

        public Node(E item, Node<E> next) {
            this.item = item;
            this.next = new AtomicReference<Node<E>>(next);
        }
    }

    private final Node<E> dummy = new Node<E>(null,null);
    private final AtomicReference<Node<E>> head = new AtomicReference<Node<E>>(dummy);
    private final AtomicReference<Node<E>> tail = new AtomicReference<Node<E>>(dummy);

    public boolean put(E item){
        Node<E> newNode = new Node<>(item, null);
        while (true){
            Node<E> curTail = tail.get();//尾结点
            Node<E> tailNext = curTail.next.get();//尾结点的下一个结点(正常应该为空 多线程操作情况下可能会出现 取出来的尾结点已经不是最新的了)
            if (curTail==tail.get()){//如果尾结点已改变 重新获取
                if(tailNext != null){//尾结点的下一个结点不为空
                    tail.compareAndSet(curTail, tailNext);//CAS修改尾结点为下一个结点
                }else{
                    if(curTail.next.compareAndSet(null, newNode)){//如果不为空 CAS操作将新结点插入到尾接点之后
                        tail.compareAndSet(curTail,newNode);//CAS操作更新尾结点为当前结点
                        return true;
                    }
                }
            }
        }
    }
}
