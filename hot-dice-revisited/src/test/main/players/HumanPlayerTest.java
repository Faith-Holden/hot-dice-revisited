package main.players;

import main.com.DiceHand;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HumanPlayerTest {
    static int guiUpdates = 0;
    static int animationRuns = 0;
    static int pauseChecks = 0;
    static int countWaits = 0;
    static int countTurnConditionChecks = 0;
    static boolean wereDiceChecked = false;
    static HumanPlayer player;
    @BeforeAll
    static void setUp(){
        player = new HumanPlayer() {
            @Override
            public void waitForUser() throws InterruptedException {
                countWaits++;
            }
            @Override
            public String getPlayerString() {
                return "Human";
            }
            @Override
            public boolean isEndTurnConditionMet(DiceHand rolledDice) {
                countTurnConditionChecks++;
                return countTurnConditionChecks > 3;
            }
            @Override
            public DiceHand chooseDiceToKeep(DiceHand rolledDice) throws InterruptedException {
                wereDiceChecked = true;
                return rolledDice;
            }
            @Override
            public void updateGui(int rolledDiceNum, DiceHand keptDice, boolean isFarkle) throws InterruptedException {
                guiUpdates++;
            }
            @Override
            public void runAnimation1(DiceHand diceHand) throws InterruptedException {
                animationRuns++;
            }
            @Override
            public void checkPaused(){
                pauseChecks++;
            }
        };
    }

    @Test
    void playTurn() {
        try{
            player.playTurn();
        }catch (InterruptedException ignored){
        }
        assertEquals( 4,countTurnConditionChecks);
        assertTrue(wereDiceChecked);
        assertEquals(5, countWaits);
        assertEquals(28, pauseChecks);
        assertEquals(4, animationRuns);
        assertTrue(guiUpdates>3 && guiUpdates<9);
    }
}