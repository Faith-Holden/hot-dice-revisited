package com;

import java.util.ArrayList;

public class HotDiceGame {
    ArrayList<BasicPlayer> players = new ArrayList<>();

    public HotDiceGame(){

    }

    //-------------Nested Player Classes -------------------
    public class HumanPlayer extends BasicPlayer{

        @Override
        public void playTurn() {

        }

        @Override
        public DiceHand chooseDiceToKeep(DiceHand rolledDice) {
            return null;
        }
    }

    //------------------------------------------------------


    
}
