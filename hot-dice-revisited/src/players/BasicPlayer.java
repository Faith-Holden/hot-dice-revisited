package players;

import com.DiceHand;

public abstract class BasicPlayer {
    private final String PLAYER_TYPE;

    private DiceHand playerHand;
    private int playerScore;


    public BasicPlayer(){
        playerScore = 0;
        playerHand = rollDice();
        PLAYER_TYPE = getPlayerString();

    }

    public void updateGui() throws InterruptedException {}
    public void updateGui(int rolledDiceNum, DiceHand keptDice, boolean isFarkle) throws InterruptedException {}
    public void runAnimation1(DiceHand diceHand) throws InterruptedException {}
    public void runAnimation2(DiceHand diceHand){}
    public void showSelected(DiceHand diceHand){}
    public void checkPaused(){}





    public DiceHand rollDice(){
        return new DiceHand(6);
    }
    public DiceHand rollDice(int diceToRoll){
        return new DiceHand(diceToRoll);
    }

    //-------------Abstract methods----------------
    public abstract String getPlayerString();
    public abstract void playTurn() throws InterruptedException;
    public abstract boolean isEndTurnConditionMet(DiceHand rolledDice);
    public abstract DiceHand chooseDiceToKeep(DiceHand rolledDice, boolean initialRoll) throws InterruptedException;
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
