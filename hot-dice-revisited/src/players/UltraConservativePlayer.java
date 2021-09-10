package players;

import com.DiceHand;
import com.Die;

public class UltraConservativePlayer extends BasicBotPlayer{
    @Override
    public boolean isEndTurnConditionMet(DiceHand rolledDice) {
        return true;
    }

    @Override
    public DiceHand chooseDiceToKeep(DiceHand rolledDice) {
        rolledDice.scoreInitialRoll();
        return new DiceHand(rolledDice.getScoringDice());
    }
}
