package main.com;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DiceHandTest {
    @Test
    void testHotDiceHandScore(){
        int die1 = 1;
        int die2 = 2;
        int die3 = 3;
        int die4 = 4;
        int die5 = 5;
        int die6 = 6;

        DiceHand straight = new DiceHand(die1, die2, die3, die4, die5, die6);
        DiceHand pairs = new DiceHand(die1,die1,die2,die2,die3,die3);
        DiceHand pairsV2 = new DiceHand(die1, die1, die1, die1, die2, die2);
        DiceHand triples = new DiceHand(die1, die1,die1, die2, die2, die2);
        DiceHand sixOfAKind = new DiceHand(die1, die1, die1, die1, die1, die1);

        DiceHand[] handsToCheck = new DiceHand[]{straight, pairs, pairsV2, triples, sixOfAKind};

        for(DiceHand handToCheck : handsToCheck){
            handToCheck.scoreInitialRoll();
        }
        for(DiceHand handToCheck : handsToCheck){
            assertEquals(handToCheck.getHandScore(), 2600);
        }

        die1 = 6;
        die2 = 5;
        die3 = 4;
        die4 = 3;
        die5 = 2;
        die6 = 1;


        straight = new DiceHand(die1, die2, die3, die4, die5, die6);
        pairs = new DiceHand(die1,die1,die2,die2,die3,die3);
        pairsV2 = new DiceHand(die1, die1, die1, die1, die2, die2);
        triples = new DiceHand(die1, die1,die1, die2, die2, die2);
        sixOfAKind = new DiceHand(die1, die1, die1, die1, die1, die1);

        handsToCheck = new DiceHand[]{straight, pairs, pairsV2, triples, sixOfAKind};

        for(DiceHand handToCheck : handsToCheck){
            assertEquals(handToCheck.getHandScore(), 2600);
        }
    }
    @Test
    void testFarkleHandScore(){
        int farkleNum1 = 2;
        int farkleNum2 = 3;
        int farkleNum3 = 4;
        int farkleNum4 = 6;

        DiceHand farkleHand1 = new DiceHand(farkleNum1,farkleNum1,farkleNum2,farkleNum2,farkleNum3,farkleNum4);
        DiceHand farkleHand2 = new DiceHand(farkleNum1,farkleNum1,farkleNum2,farkleNum2,farkleNum3);
        DiceHand farkleHand3 = new DiceHand(farkleNum1, farkleNum2,farkleNum3,farkleNum4, farkleNum4);
        DiceHand farkleHand4 = new DiceHand(farkleNum1, farkleNum2,farkleNum2,farkleNum3, farkleNum3);
        DiceHand farkleHand5 = new DiceHand(farkleNum1,farkleNum2,farkleNum3,farkleNum4);
        DiceHand farkleHand6 = new DiceHand(farkleNum1,farkleNum2,farkleNum3,farkleNum3);
        DiceHand farkleHand7 = new DiceHand(farkleNum1,farkleNum1,farkleNum2,farkleNum2);
        DiceHand farkleHand8 = new DiceHand(farkleNum1,farkleNum2,farkleNum3);
        DiceHand farkleHand9 = new DiceHand(farkleNum1,farkleNum2,farkleNum2);
        DiceHand farkleHand10 = new DiceHand(farkleNum1,farkleNum2);
        DiceHand farkleHand11 = new DiceHand(farkleNum1,farkleNum1);
        DiceHand farkleHand12 = new DiceHand(new int[]{farkleNum1});


        DiceHand[] handsToCheck = new DiceHand[]{farkleHand1, farkleHand2, farkleHand3,farkleHand4,farkleHand5,
                farkleHand6,farkleHand7, farkleHand8, farkleHand9,farkleHand10, farkleHand11, farkleHand12};

        for(DiceHand handToCheck : handsToCheck){
            assertEquals(handToCheck.getHandScore(), 0);
        }
    }
    @Test
    void testScore3ofKind(){
        int nonScoredNum1 = 2;
        int nonScoredNum2 = 3;
        int nonScoredNum3 = 4;

        assertEquals(new DiceHand(1,1,1,5,5,nonScoredNum1).getHandScore(),1100);
        assertEquals(new DiceHand(1,1,1,5,nonScoredNum1,nonScoredNum1).getHandScore(),1050);
        assertEquals(new DiceHand(1,1,1,nonScoredNum2,nonScoredNum1,nonScoredNum1).getHandScore(),1000);

        assertEquals(new DiceHand(5,5,5,1,1,nonScoredNum1).getHandScore(),700);
        assertEquals(new DiceHand(5,5,5,1,nonScoredNum1,nonScoredNum1).getHandScore(),600);
        assertEquals(new DiceHand(5,5,5,nonScoredNum1,nonScoredNum1,nonScoredNum2).getHandScore(),500);

        assertEquals(new DiceHand(nonScoredNum1,nonScoredNum1,nonScoredNum1,1,1,5).getHandScore(),450);
        assertEquals(new DiceHand(nonScoredNum1,nonScoredNum1,nonScoredNum1,1,5,5).getHandScore(),400);

        assertEquals(new DiceHand(nonScoredNum1,nonScoredNum1,nonScoredNum1,1,5,nonScoredNum2).getHandScore(),350);
        assertEquals(new DiceHand(nonScoredNum1,nonScoredNum1,nonScoredNum1,5,5,nonScoredNum2).getHandScore(),300);
        assertEquals(new DiceHand(nonScoredNum1,nonScoredNum1,nonScoredNum1,1,1,nonScoredNum2).getHandScore(),400);

        assertEquals(new DiceHand(nonScoredNum1,nonScoredNum1,nonScoredNum1,1,nonScoredNum2,nonScoredNum2).getHandScore(),300);
        assertEquals(new DiceHand(nonScoredNum1,nonScoredNum1,nonScoredNum1,5,nonScoredNum2,nonScoredNum2).getHandScore(),250);

        assertEquals(new DiceHand(nonScoredNum1,nonScoredNum1,nonScoredNum1,nonScoredNum2,nonScoredNum2, nonScoredNum3).getHandScore(),200);
    }
    @Test
    void testScore4ofKind(){
        int nonScoredNum1 = 2;
        int nonScoredNum2 = 3;
        int nonScoredNum3 = 4;

        assertEquals(new DiceHand(1,1,1,1,5,nonScoredNum1).getHandScore(),2050);
        assertEquals(new DiceHand(1,1,1,1,nonScoredNum1,nonScoredNum2).getHandScore(),2000);

        assertEquals(new DiceHand(5,5,5,5,1,nonScoredNum1).getHandScore(),1100);
        assertEquals(new DiceHand(5,5,5,5,nonScoredNum1,nonScoredNum2).getHandScore(),1000);

        assertEquals(new DiceHand(nonScoredNum1,nonScoredNum1,nonScoredNum1,nonScoredNum1,1,5).getHandScore(), 550);

        assertEquals(new DiceHand(nonScoredNum1,nonScoredNum1,nonScoredNum1,nonScoredNum1,1,nonScoredNum2).getHandScore(), 500);
        assertEquals(new DiceHand(nonScoredNum1,nonScoredNum1,nonScoredNum1,nonScoredNum1,5,nonScoredNum2).getHandScore(), 450);

        assertEquals(new DiceHand(nonScoredNum1,nonScoredNum1,nonScoredNum1,nonScoredNum1,nonScoredNum2,nonScoredNum3).getHandScore(), 400);
    }
    @Test
    void testScore5ofKind(){
        int nonScoredNum1 = 2;
        int nonScoredNum2 = 3;

        assertEquals(new DiceHand(1,1,1,1,1,5).getHandScore(),3050);
        assertEquals(new DiceHand(1,1,1,1,1,nonScoredNum1).getHandScore(),3000);

        assertEquals(new DiceHand(5,5,5,5,5,1).getHandScore(),1600);
        assertEquals(new DiceHand(5,5,5,5,5,nonScoredNum1).getHandScore(),1500);

        assertEquals(new DiceHand(nonScoredNum1,nonScoredNum1,nonScoredNum1,nonScoredNum1,nonScoredNum1,1).getHandScore(), 700);
        assertEquals(new DiceHand(nonScoredNum1,nonScoredNum1,nonScoredNum1,nonScoredNum1,nonScoredNum1,5).getHandScore(), 650);

        assertEquals(new DiceHand(nonScoredNum1,nonScoredNum1,nonScoredNum1,nonScoredNum1,nonScoredNum1,nonScoredNum2).getHandScore(), 600);
    }
    @Test
    void testScoreUngrouped(){
        int nonScoredNum1 = 2;
        int nonScoredNum2 = 3;
        int nonScoredNum3 = 4;
        assertEquals(new DiceHand(1,1,5,5,nonScoredNum1,nonScoredNum2).getHandScore(),300);
        assertEquals(new DiceHand(1,1,5,nonScoredNum1,nonScoredNum1,nonScoredNum2).getHandScore(),250);
        assertEquals(new DiceHand(1,1,nonScoredNum1,nonScoredNum1,nonScoredNum2,nonScoredNum3).getHandScore(),200);
        assertEquals(new DiceHand(1,5,nonScoredNum1,nonScoredNum1,nonScoredNum2,nonScoredNum2).getHandScore(),150);
        assertEquals(new DiceHand(1,nonScoredNum1,nonScoredNum1,nonScoredNum2,nonScoredNum2,nonScoredNum3).getHandScore(),100);
        assertEquals(new DiceHand(5,nonScoredNum1,nonScoredNum1,nonScoredNum2,nonScoredNum2,nonScoredNum3).getHandScore(),50);
        assertEquals(new DiceHand(5,5,nonScoredNum1,nonScoredNum1,nonScoredNum2,nonScoredNum3).getHandScore(),100);
        assertEquals(new DiceHand(5,5,1,nonScoredNum1,nonScoredNum1,nonScoredNum2).getHandScore(),200);
    }




}