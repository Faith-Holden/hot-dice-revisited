package players;

import com.DiceHand;
import com.Die;

import java.util.ArrayList;

public abstract class BasicBotPlayer extends BasicPlayer {
    Object lock = new Object();

    @Override
    public void playTurn() {
        boolean turnIsDone = false;
        boolean initialRoll = true;
        int currentHandSize = 0;
        setPlayerHand(new DiceHand());
        updateGui();

        int counter = 0;
        while (!turnIsDone){
            currentHandSize = getPlayerHand().getHandSize();
            if(currentHandSize==6){
                setPlayerHand(new DiceHand());
                currentHandSize = 0;
                initialRoll=true;
            }
            DiceHand rolledDice = rollDice(6-currentHandSize);
            runAnimation1(rolledDice);//animation to show "rolling" the dice
            updateGui();
            DiceHand keptDice = chooseDiceToKeep(rolledDice, initialRoll);
            updateGui();
            if(keptDice.isFarkle()){
                turnIsDone=true;
                continue;
            }
            if(initialRoll){
                keptDice.scoreInitialRoll();
                initialRoll=false;
            }else{
                keptDice.scoreSubsequentRolls();
            }
            setPlayerScore(getPlayerScore()+keptDice.getHandScore());
            setPlayerHand(new DiceHand(getPlayerHand(), keptDice));
            runAnimation2(getPlayerHand());
            updateGui();
            turnIsDone = isEndTurnConditionMet(getPlayerHand());

            counter++;
        }
    }
}
