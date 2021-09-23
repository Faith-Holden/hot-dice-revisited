package main.players;

import main.com.DiceHand;
import main.com.Die;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class UltraConservativePlayerTest {
    static UltraConservativePlayer player;

    @BeforeAll
    static void setUp(){
        player = new UltraConservativePlayer();
        player.setPlayerHand(new DiceHand(1,1,2,3,4,5));
    }

    @Test
    void isEndTurnConditionMet() {
        assertTrue(player.isEndTurnConditionMet(player.getPlayerHand()));
    }

    @Test
    void chooseDiceToKeep() {
       int[] diceNums = new int[3];
       DiceHand keptDice = player.chooseDiceToKeep(player.getPlayerHand());
       for(int i = 0; i<3; i++){
           diceNums[i]= keptDice.getDiceInHand().get(i).getDieNum();
       }
        assertArrayEquals(new int[]{1,1,5}, diceNums);
    }

    @Test
    void getPlayerString() {
        assertEquals("Ultra-Conservative", player.getPlayerString());
    }

}