package main.players;

import main.com.DiceHand;

public abstract class BasicBotPlayer extends BasicPlayer {
    @Override
    public void playTurn() throws InterruptedException {
        boolean turnIsDone = false;
        DiceHand chosenDice;
        setPlayerHand(new DiceHand());
        updateGui(0,getPlayerHand(),false);
        int handScore = 0;

        while (!turnIsDone){
            checkPaused();
            if(getPlayerHand().getHandSize()==6){
                showHotDice();
                getPlayerHand().getScoringDice().clear();
                getPlayerHand().getDiceInHand().clear();
                updateGui(0, getPlayerHand(), false);
            }
            checkPaused();
            DiceHand rolledDice = new DiceHand(6-getPlayerHand().getHandSize());
            runAnimation1(rolledDice);
            checkPaused();
            chosenDice = chooseDiceToKeep(rolledDice);
            if(chosenDice.getHandSize()==0){
                handScore = 0;
                setPlayerHand(new DiceHand());
                updateGui(rolledDice.getHandSize(), getPlayerHand(), true);
                turnIsDone = true;
                continue;
            }
            checkPaused();
            handScore = handScore+chosenDice.getHandScore();
            checkPaused();
            setPlayerHand(new DiceHand(getPlayerHand(), chosenDice));
            updateGui(rolledDice.getHandSize(), getPlayerHand(), false);
            checkPaused();
            turnIsDone = isEndTurnConditionMet(getPlayerHand());
            checkPaused();
        }
        setPlayerScore(getPlayerScore() + handScore);
    }
}
