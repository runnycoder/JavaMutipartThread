package com.icehan.thread.future;

import com.sun.xml.internal.ws.util.CompletedFuture;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

public class TestCompleteFuture {

    /**
     * CompletableFuture applyToEither方法测试
     * 接受两个CompletableFuture任务那个执行的快就返回那个的结果
     */
    @Test
    public void test(){
        //两个CompletableFuture那个运行的快就用那个返回的结果
        String result = CompletableFuture.supplyAsync(()-> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Hi boy";
        }).applyToEither(CompletableFuture.supplyAsync(()->{
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Hi Girl";
        }), (s)->{return s;}).join();
        System.out.println(result);
    }

    /**
     * public <U> CompletionStage<U> thenApply(Function<? super T,? extends U> fn);
     * public <U> CompletionStage<U> thenApplyAsync(Function<? super T,? extends U> fn);
     * public <U> CompletionStage<U> thenApplyAsync(Function<? super T,? extends U> fn,Executor executor);
     * 这些方法接受上一个的结果 返回本次的计算结果
     */
    @Test
    public void test1(){
        String result = CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("sub1 waiting");
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Hello";
        }).thenApply(v -> {
            try {
                System.out.println("sub2 waiting");
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return v + "word!";
        }).join();
        System.out.println(result);
        System.out.println("Im main Thread");

    }

    /**
     *  public CompletionStage<Void> thenAccept(Consumer<? super T> action);
     * public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action);
     * public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action,Executor executor);
     * 针对上一步的结果进行消费 无返回值
     */
    @Test
    public void test2(){
        CompletableFuture.supplyAsync(()->{return "Hello World!";}).thenAccept(v->{
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(v);
        });
        System.out.println("Im main thread");
    }

    /**
     * public <U,V> CompletionStage<V> thenCombine(CompletionStage<? extends U> other,BiFunction<? super T,? super U,? extends V> fn);
     * public <U,V> CompletionStage<V> thenCombineAsync(CompletionStage<? extends U> other,BiFunction<? super T,? super U,? extends V> fn);
     * public <U,V> CompletionStage<V> thenCombineAsync(CompletionStage<? extends U> other,BiFunction<? super T,? super U,? extends V> fn,Executor executor);
     * 结合两个CompletionStage的结果，进行转化后返回
     * 需要上一阶段的返回值，并且other代表的CompletionStage返回之后 把这两个返回值进行转换
     */
    @Test
    public void test3(){
        String result = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Hello";
        }).thenCombine(CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "World";
        }), (s1, s2) -> {
            return s1 + " " + s2;
        }).join();
        System.out.println(result);
        System.out.println("Im main Thread");
    }

    /**
     * public CompletionStage<T> exceptionally(Function<Throwable, ? extends T> fn);
     * 运行时出了异常可以用exceptionally进行补偿
     */
    @Test
    public void test4(){
        String result = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (true) {
                throw new RuntimeException("exceptionally test!");
            }
            return "Hi boy";
        }).exceptionally(e -> {
            System.out.println(e.getMessage());
            return "Hello World";
        }).join();

        System.out.println(result);
    }

    public static void main(String[] args) {
    }
}
