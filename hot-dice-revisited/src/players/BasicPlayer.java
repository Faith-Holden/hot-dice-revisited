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
    public void updateGui(int rolledDiceNum, DiceHand keptDice, boolean isFarkle){}
    public void runAnimation1(DiceHand diceHand){}
    public void runAnimation2(DiceHand diceHand){}
    public void showSelected(DiceHand diceHand){}
    public void checkPaused(){}


    public abstract void playTurn();


    public DiceHand rollDice(){
        return new DiceHand(6);
    }
    public DiceHand rollDice(int diceToRoll){
        return new DiceHand(diceToRoll);
    }

    //-------------Abstract methods----------------
    public abstract boolean isEndTurnConditionMet(DiceHand rolledDice);
    public abstract DiceHand chooseDiceToKeep(DiceHand rolledDice, boolean initialRoll);
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
