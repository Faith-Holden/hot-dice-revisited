package players;

import com.DiceHand;

public abstract class BasicBotPlayer extends BasicPlayer {
    @Override
    public void playTurn() {
        boolean isTurnDone = false;
        updateGui();
        setPlayerHand(rollDice());
        updateGui();


        while(!isTurnDone){
            DiceHand diceToKeep = chooseDiceToKeep(getPlayerHand());
            isTurnDone = isEndTurnConditionMet(diceToKeep);

            if(!isTurnDone && diceToKeep.getHandSize()==6){
                //true if hot dice
                setPlayerHand(rollDice());
                setPlayerScore(getPlayerScore()+getPlayerHand().getHandScore());
            }else{
                //true in any other case
                setPlayerScore(getPlayerScore()+diceToKeep.getHandScore());
                setPlayerHand(diceToKeep);
            }
            updateGui();
        }
    }
}
