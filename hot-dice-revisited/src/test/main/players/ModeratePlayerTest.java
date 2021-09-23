package main.players;

import main.com.DiceHand;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModeratePlayerTest {
    static ModeratePlayer player1;
    static ModeratePlayer player2;
    static ModeratePlayer player3;


    @BeforeAll
    static void setUp(){
        player1 = new ModeratePlayer();
        player1.setPlayerHand(new DiceHand(1,1,2,3,4,5));


        player2 = new ModeratePlayer();
        player2.setPlayerHand(new DiceHand(1,5,2,3,4,4));

        player3 = new ModeratePlayer();
        player3.setPlayerHand(new DiceHand(1,1,1,5,2,2));

    }

    @Test
    void isEndTurnConditionMet() {
        DiceHand scoringDice1 = new DiceHand(player1.getPlayerHand().getScoringDice());
        DiceHand scoringDice2 = new DiceHand(player2.getPlayerHand().getScoringDice());
        DiceHand scoringDice3 = new DiceHand(player3.getPlayerHand().getScoringDice());
        assertTrue(player1.isEndTurnConditionMet(scoringDice1));
        assertFalse(player2.isEndTurnConditionMet(scoringDice2));
        assertTrue(player3.isEndTurnConditionMet(scoringDice3));
    }

    @Test
    void chooseDiceToKeep() {
        int[] diceNums = new int[1];
        DiceHand keptDice = player1.chooseDiceToKeep(player1.getPlayerHand());
        diceNums[0]= keptDice.getDiceInHand().get(0).getDieNum();
        assertArrayEquals(new int[]{1}, diceNums);

        int[] diceNums2 = new int[1];
        DiceHand keptDice2 = player2.chooseDiceToKeep(player2.getPlayerHand());
        diceNums2[0]= keptDice2.getDiceInHand().get(0).getDieNum();
        assertArrayEquals(new int[]{1}, diceNums2);

        int[] diceNums3 = new int[4];
        DiceHand keptDice3 = player3.chooseDiceToKeep(player3.getPlayerHand());
        for(int i = 0; i<4; i++){
            diceNums3[i]= keptDice3.getDiceInHand().get(i).getDieNum();
        }
        assertArrayEquals(new int[]{1,1,1,5}, diceNums3);
    }

    @Test
    void getPlayerString() {
        assertEquals("Moderate", player1.getPlayerString());
    }

}