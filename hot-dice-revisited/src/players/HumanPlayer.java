package players;

import com.DiceHand;

public class HumanPlayer extends BasicPlayer{
    @Override
    public void playTurn() {

    }

    @Override
    public boolean isEndTurnConditionMet(DiceHand rolledDice) {
        return false;
    }

    @Override
    public DiceHand chooseDiceToKeep(DiceHand rolledDice, boolean initialRoll) {
        return rolledDice;
    }
}
