package players;

import com.DiceHand;

public abstract class HumanPlayer extends BasicPlayer{
    @Override
    public void playTurn() throws InterruptedException {
        boolean turnIsDone = false;
        boolean initialRoll = true;
        DiceHand chosenDice;
        setPlayerHand(new DiceHand());
        updateGui(0, getPlayerHand(), false);
        prepGuiForUserTurn();
        int handScore = 0;

        waitForUser();
        while(!turnIsDone){
            checkPaused();
            if(getPlayerHand().getHandSize()==6){
                getPlayerHand().getDiceInHand().clear();
                getPlayerHand().getScoringDice().clear();
                initialRoll=true;
                updateGui(0, getPlayerHand(), false);
            }
            DiceHand rolledDice = new DiceHand(6-getPlayerHand().getHandSize());
            prepGuiForUserDiceRoll(rolledDice);
            checkPaused();
            runAnimation1(rolledDice);
            checkPaused();
            chosenDice = chooseDiceToKeep(rolledDice, initialRoll);
            if(chosenDice.getHandSize()==0){
                handScore = 0;
                updateGui(rolledDice.getHandSize(), chosenDice, true);
                turnIsDone = true;
                continue;
            }
            checkPaused();
            chosenDice.scoreInitialRoll();

            handScore = handScore+chosenDice.getHandScore();
            checkPaused();
            setPlayerHand(new DiceHand(getPlayerHand(), chosenDice));
            updateGui(rolledDice.getHandSize(), getPlayerHand(), false);
            checkPaused();
            waitForUser();
            turnIsDone = isEndTurnConditionMet(getPlayerHand());
            initialRoll = false;
            checkPaused();
        }

        setPlayerScore(getPlayerScore() + handScore);
    }

    public abstract void waitForUser() throws InterruptedException;
    public void prepGuiForUserTurn() throws InterruptedException {}
    public void prepGuiForUserDiceRoll(DiceHand rolledDice) throws InterruptedException {}
}

