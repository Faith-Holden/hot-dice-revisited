package main.players;

import main.com.DiceHand;

public class ConservativePlayer extends BasicBotPlayer{
    @Override
    public boolean isEndTurnConditionMet(DiceHand diceToCheck) {
        if(diceToCheck.getHandSize()==6){
            return false;
        }
        int newDiceNum = diceToCheck.getHandSize();
        boolean isDone = (newDiceNum)>2;
        return isDone;
    }
    @Override
    public DiceHand chooseDiceToKeep(DiceHand rolledDice, boolean initialRoll) {
//        rolledDice.scoreInitialRoll();
        return new DiceHand(rolledDice.getScoringDice());
    }
    @Override
    public String getPlayerString(){
        return "Conservative";
    }

}
