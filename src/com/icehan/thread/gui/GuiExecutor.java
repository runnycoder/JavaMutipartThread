package com.icehan.thread.gui;

/**
 * 基于SwingUtilities
 */
public class GuiExecutor {
    private static final GuiExecutor instance = new GuiExecutor();
    private GuiExecutor(){};

    public static GuiExecutor instance(){
        return instance;
    }

    public void execute(Runnable r){
        if(SwingUtilities.isEventDispatchThread()){
            r.run();
        }else{
            SwingUtilities.invokerLater(r);
        }
    }
}
