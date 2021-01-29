package com.icehan.thread.cas;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 利用CAS实现非阻塞栈
 * @param <E>
 */
public class ConcurrentStack<E> {

    private static class Node<E>{
        public final E item;
        public Node<E> next;

        public Node(E item) {
            this.item = item;
        }
    }
    //使用原子类维护栈顶结点 在入栈出栈需要更新栈顶结点的时候使用原子操作 更新过程中如果旧值被改动需要重新获取
    AtomicReference<Node<E>> top = new AtomicReference<Node<E>>();

    public void push(E item){
        Node<E> newHead = new Node<>(item);
        Node<E> oldHead;
        do {
            oldHead = top.get();
            newHead.next = oldHead;
        }while (!top.compareAndSet(oldHead, newHead));
    }

    public E pop(){
        Node<E> oldHead;
        Node<E> newHead;
        do {
            oldHead = top.get();
            if(oldHead==null){
                return null;
            }
            newHead = oldHead.next;
        }while (!top.compareAndSet(oldHead, newHead));
        return oldHead.item;
    }
}
