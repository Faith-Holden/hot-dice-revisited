package main.players;

import main.com.DiceHand;

public class ModeratePlayer extends BasicBotPlayer{
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
        DiceHand scoredDice = new DiceHand(rolledDice.getScoringDice());

        //true if user has at least 3 dice in previously kept hand(would be too risky to reroll)
        if(rolledDice.getHandSize()<4){
            return scoredDice;
        }

        //true if anything score but 1s or 5s, meaning automatically best to keep
        if(scoredDice.getNum2s()!=0 || scoredDice.getNum3s()!=0 || scoredDice.getNum4s()!=0 || scoredDice.getNum6s()!=0){
            return scoredDice;
        }

        //true if at least three 1s
        if(scoredDice.getNum1s()>2){
            return scoredDice;
        }

        //true if at least three 5s.
        if (scoredDice.getNum5s()>2){
            return scoredDice;
        }

        //would be 300 points, worth saving
        if(rolledDice.getNum1s() == 2 && rolledDice.getNum5s() == 2){
            return rolledDice;
        }

        //keep minimum number of 1s if possible
        if(rolledDice.getNum1s()!=0){
            return new DiceHand(new int[]{1});
        }

        //keep minimum number of 5s
        if(rolledDice.getNum5s()!=0){
            return new DiceHand(new int[]{5});
        }

        return new DiceHand();
    }

    @Override
    public String getPlayerString(){
        return "Moderate";
    }
}
