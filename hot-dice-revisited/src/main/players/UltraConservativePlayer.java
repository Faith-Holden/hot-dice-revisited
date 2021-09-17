package main.players;

import main.com.DiceHand;

public class UltraConservativePlayer extends BasicBotPlayer{
    @Override
    public boolean isEndTurnConditionMet(DiceHand rolledDice) {
        return true;
    }
    @Override
    public DiceHand chooseDiceToKeep(DiceHand rolledDice, boolean initialRoll) {
//        rolledDice.scoreInitialRoll();
        return new DiceHand(rolledDice.getScoringDice());
    }
    @Override
    public String getPlayerString(){
        return "Ultra-Conservative";
    }
}
