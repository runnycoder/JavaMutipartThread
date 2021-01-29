package com.icehan.thread.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class EventsResolve {
    final JButton button  = new JButton("change color");
    final Random random = new Random();
    final ExecutorService backgroundExec = Executors.newCachedThreadPool();

    private boolean moreWork(){
        return true;
    }

    private void cleanUpPartialWork(){
        System.out.println("task resource is cleaning!");
    }

    private void doSomeWork(){
        System.out.println("task is running!");
    }

     //简单事件处理
    public void randomColor(){
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button.setBackground(new Color(random.nextInt()));
            }
        });
    }

    /**
     * 复杂任务处理 将耗时的任务绑定到可视化组件
     * 不支持取消操作 不显示进度 同时也不更新GUI组件
     */
    public void complexTaskResolve(){
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backgroundExec.execute(new Runnable() {
                    @Override
                    public void run() {
                        //doBigComputation
                    }
                });
            }
        });
    }

    /**
     * 将耗时的任务绑定到组件上 并提供用户反馈
     */
    public void complexTaskResolve1(){
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button.setEnabled(false);
                button.setLabel("running...");
                backgroundExec.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            System.out.println(" doBigComputation .....");
                        } finally {
                            GuiExecutor.instance().execute(new Runnable() {
                                @Override
                                public void run() {
                                    button.setEnabled(true);
                                    button.setLabel("its ok!");
                                }
                            });
                        }
                    }
                });
            }
        });
    }


    Future<?> runningTask = null;//线程限制的
    /**
     * 事件线程使用此代码 保证runningTask被限制在事件线程中
     * 并且startButton的监听器可以确保同时只有一个后台任务在运行
     */
    {

        JButton startButton = new JButton("start button");
        JButton cancelButton = new JButton("cancel button");

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(runningTask ==null){
                    runningTask = backgroundExec.submit(new Runnable() {
                        @Override
                        public void run() {
                            while (moreWork()) {
                                if (Thread.interrupted()) {
                                    cleanUpPartialWork();
                                    break;
                                }
                                doSomeWork();
                            }
                        }
                    });
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(runningTask!=null){
                    runningTask.cancel(true);
                }
            }
        });

    }

    //启动一个耗时的可取消的任务
    {
        JButton startButton = new JButton("start button");
        JButton cancelButton = new JButton("cancel button");

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                class CancelListener implements ActionListener{
                    BackgroundTask<?> task;
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(task!=null){
                            task.cancel(true);
                        }
                    }
                }

                final CancelListener listener = new CancelListener();
                listener.task = new BackgroundTask<Object>() {
                    @Override
                    protected Object compute() throws Exception {
                        while (moreWork()&&!isCancelled()){
                            doSomeWork();
                        }
                        return null;
                    }
                    public void onCompletion(boolean cancelled,String s,Throwable exception){
                        cancelButton.removeActionListener(listener);
                        cancelButton.setLabel("done");
                    }
                };

                cancelButton.addActionListener(listener);
                backgroundExec.execute(listener.task);

            }
        });
    }


}
