package com;

public abstract class BasicPlayer {
    private DiceHand playerHand;
    private int playerScore;

    public BasicPlayer(){
        playerHand = new DiceHand(5);
        playerScore = 0;
    }



    public DiceHand rollDice(){
        return new DiceHand();
    }
    //-------------Abstract classes----------------
    public abstract void playTurn();
    public abstract DiceHand chooseDiceToKeep (DiceHand rolledDice);
    //---------------------------------------------

    //-----------Getters and setters---------------
    public void setPlayerHand(DiceHand playerHand) {
        this.playerHand = playerHand;
    }
    public void setPlayerScore(int playerScore) {
        this.playerScore = playerScore;
    }
    public DiceHand getPlayerHand() {
        return playerHand;
    }
    public int getPlayerScore() {
        return playerScore;
    }
    //----------------------------------------------
}
