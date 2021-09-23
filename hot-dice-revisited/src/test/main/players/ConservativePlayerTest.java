package main.players;

import main.com.DiceHand;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConservativePlayerTest {
    static ConservativePlayer player1;
    static ConservativePlayer player2;

    @BeforeAll
    static void setUp(){
        player1 = new ConservativePlayer();
        player1.setPlayerHand(new DiceHand(1,1,2,3,4,5));


        player2 = new ConservativePlayer();
        player2.setPlayerHand(new DiceHand(1,1,2,3,4,4));
    }

    @Test
    void isEndTurnConditionMet() {
        DiceHand scoringDice1 = new DiceHand(player1.getPlayerHand().getScoringDice());
        DiceHand scoringDice2 = new DiceHand(player2.getPlayerHand().getScoringDice());
        assertTrue(player1.isEndTurnConditionMet(scoringDice1));
        assertFalse(player2.isEndTurnConditionMet(scoringDice2));
    }

    @Test
    void chooseDiceToKeep() {
        int[] diceNums = new int[3];
        DiceHand keptDice = player1.chooseDiceToKeep(player1.getPlayerHand());
        for(int i = 0; i<3; i++){
            diceNums[i]= keptDice.getDiceInHand().get(i).getDieNum();
        }
        assertArrayEquals(new int[]{1,1,5}, diceNums);

        int[] diceNums1 = new int[2];
        DiceHand keptDice1 = player2.chooseDiceToKeep(player2.getPlayerHand());
        for(int i = 0; i<2; i++){
            diceNums1[i]= keptDice1.getDiceInHand().get(i).getDieNum();
        }
        assertArrayEquals(new int[]{1,1}, diceNums1);
    }

    @Test
    void getPlayerString() {
        assertEquals("Conservative", player1.getPlayerString());
    }
}