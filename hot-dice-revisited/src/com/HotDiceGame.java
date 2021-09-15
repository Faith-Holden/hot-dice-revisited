package com;

import players.BasicPlayer;

import java.io.File;
import java.util.ArrayList;

public class HotDiceGame {
    private ArrayList<BasicPlayer> players = new ArrayList<>();


    public HotDiceGame(){

    }

    public void updateGameBoard(boolean isLastCall){}

    public BasicPlayer playGame(){
        boolean inProgress = true;
        int lastCallPlayer = -1;
        BasicPlayer winningPlayer = null;
        int winningScore = 0;
        boolean isLastCall = false;
        while (inProgress){
            for(BasicPlayer player : players){
                if(players.indexOf(player)==lastCallPlayer){
                    inProgress = false;
                    break;

                }else{
                    updateGameBoard(isLastCall);
                    player.playTurn();
                    if(player.getPlayerScore()>1000){
                        if(lastCallPlayer==-1){
                            lastCallPlayer = players.indexOf(player);
                            isLastCall = true;
                        }
                        if(player.getPlayerScore()>winningScore){
                            winningScore = player.getPlayerScore();
                            winningPlayer = player;
                        }
                    }
                }
            }
        }
        return winningPlayer;
    }

    public void loadGame(File saveFile){
        //get a game from a save-game file
        //parse the xml
        //add players to the players arraylist with the details specified by save
        //throw errors if not able to load
    }

    public boolean saveGame(File saveFile){
        //for each player, print score and current hand to savefile, as xml
        //print current turn to savefile as xml
        //return true if save was successful. Else throw relevant exception.
        return true;

    }

    public void addPlayers(ArrayList<? extends BasicPlayer> playersToAdd) throws IndexOutOfBoundsException{
        if(players.size()<=10){
            players.addAll(playersToAdd);
        }
        else{
            throw new IndexOutOfBoundsException("Can't add " + playersToAdd.size()+ " players. There are already "
                    + players.size() + "players in the game!");
        }
    }


    //--------------Getters and setters---------------------
    public ArrayList<BasicPlayer> getPlayers() {
        return players;
    }
    public void setPlayers(ArrayList<BasicPlayer> players) {
        this.players = players;
    }
    //------------------------------------------------------
}
