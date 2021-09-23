package main.players;

import main.com.DiceHand;

public abstract class BasicPlayer {
    private final String PLAYER_TYPE;

    private DiceHand playerHand;
    private int playerScore;


    public BasicPlayer(){
        playerScore = 0;
        playerHand = new DiceHand(6);
        PLAYER_TYPE = getPlayerString();
    }

    public void updateGui() throws InterruptedException {}
    public void updateGui(int rolledDiceNum, DiceHand keptDice, boolean isFarkle) throws InterruptedException {}
    public void runAnimation1(DiceHand diceHand) throws InterruptedException {}
    public void runAnimation2(DiceHand diceHand){}
    public void showHotDice(){}
    public void checkPaused(){}

    //-------------Abstract methods----------------
    public abstract String getPlayerString();
    public abstract void playTurn() throws InterruptedException;
    public abstract boolean isEndTurnConditionMet(DiceHand rolledDice);
    public abstract DiceHand chooseDiceToKeep(DiceHand rolledDice) throws InterruptedException;
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
    public String getPlayerType() {
        return PLAYER_TYPE;
    }
    //----------------------------------------------
}
