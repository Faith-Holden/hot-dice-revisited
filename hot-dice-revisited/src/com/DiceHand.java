package com;

import java.util.ArrayList;
import java.util.Arrays;

public class DiceHand {
    private ArrayList<Die> diceInHand;

    //------------Constructors---------------
    /**
     * Constructs new DiceHand with 5 random dice.
     */
    public DiceHand(){
        diceInHand = new ArrayList<>();
        for (int i = 0; i<5; i++){
            diceInHand.add(new Die());
        }
    }
    /**
     * Constructs new DiceHand with random dice.
     * @param handSize integer specifying how many dice to be in the new hand.
     */
    public DiceHand(int handSize){
        diceInHand = new ArrayList<>();
        for (int i = 0; i<handSize; i++){
            diceInHand.add(new Die());
        }
    }
    /**
     * Constructs new DiceHand from previously created dice provided as a parameter.
     * @param startingDice dice array of dice to include in new hand.
     */
    public DiceHand(Die[] startingDice){
        diceInHand = new ArrayList<>();
        diceInHand.addAll(Arrays.asList(startingDice));
    }
    /**
     * Constructs new DiceHand from new dice provided as a parameter.
     * @param startingDice int array for creating new dice to include in the hand.
     */
    public DiceHand(int[] startingDice){
       diceInHand = new ArrayList<>();
        for(int dieNum : startingDice){
            diceInHand.add(new Die(dieNum));
        }
    }
    //--------------------------------------

    @Override
    public String toString(){
        String handString = "[";
        for(int i = 0; i<diceInHand.size(); i++){
            if(i!=0){
                handString = handString.concat(",");
            }
            handString = handString.concat(diceInHand.get(i).toString());
        }
        handString = handString.concat("]");
        return handString;
    }

    //-----------Standard getters and setters----------
    public void setDiceInHand(ArrayList<Die> diceInHand) {
        this.diceInHand = diceInHand;
    }
    public ArrayList<Die> getDiceInHand() {
        return diceInHand;
    }
    //-------------------------------------------------
}
