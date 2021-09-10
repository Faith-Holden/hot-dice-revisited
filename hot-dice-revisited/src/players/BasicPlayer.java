package players;

import com.DiceHand;

public abstract class BasicPlayer {
    private DiceHand playerHand;
    private int playerScore;

    public BasicPlayer(){
        playerScore = 0;
        playerHand = rollDice();
    }

    public void updateGui(){}

    public DiceHand rollDice(){
        return new DiceHand();
    }
    public DiceHand rollDice(int diceToRoll){
        return new DiceHand(diceToRoll);
    }
    //-------------Abstract classes----------------
    public abstract void playTurn();
    public abstract boolean isEndTurnConditionMet(DiceHand rolledDice);
    public abstract DiceHand chooseDiceToKeep(DiceHand rolledDice);
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
