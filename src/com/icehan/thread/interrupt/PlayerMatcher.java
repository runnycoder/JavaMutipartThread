package com.icehan.thread.interrupt;

import java.util.concurrent.ArrayBlockingQueue;
public class PlayerMatcher {
    private ArrayBlockingQueue<String> players =  new ArrayBlockingQueue<String>(20);

    public PlayerMatcher(ArrayBlockingQueue<String> players) {
        this.players = players;
    }

    public void matchPlayers() throws InterruptedException{
        String playerOne = null,playerTwo =null;
        try {
            while (true){
                playerOne=playerTwo=null;
                //wait for two player to arrive and start a new game
                playerOne = players.take();//could throw IE
                playerTwo = players.take();//could throw IE
                startNewGame(playerOne,playerTwo);
            }
        } catch (InterruptedException e) {
            if(!"".equals(playerOne)&&playerOne!=null){
                players.put(playerOne);
            }
            throw e;
        }
    }

    public void startNewGame(String playerOne,String playerTwo){

    }
}
