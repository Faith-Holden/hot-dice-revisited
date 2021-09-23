package main.players;

import main.com.DiceHand;

public class ConservativePlayer extends BasicBotPlayer{
    @Override
    public boolean isEndTurnConditionMet(DiceHand diceToCheck) {
        if(diceToCheck.getHandSize()==6){
            return false;
        }
        int newDiceNum = diceToCheck.getHandSize();
        return (newDiceNum)>2;
    }
    @Override
    public DiceHand chooseDiceToKeep(DiceHand rolledDice) {
        return new DiceHand(rolledDice.getScoringDice());
    }
    @Override
    public String getPlayerString(){
        return "Conservative";
    }

}
