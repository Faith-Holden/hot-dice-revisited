package com;

import java.util.ArrayList;
import java.util.Arrays;

public class DiceHand {
    private ArrayList<Die> diceInHand;
    private ArrayList<Die> scoringDice;
    private int handScore = 0;

    //------------Constructors---------------
    /**
     * Constructs new DiceHand with 5 random dice.
     */
    public DiceHand(){
        diceInHand = new ArrayList<>();
        for (int i = 0; i<7; i++){
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

    public void scoreSubsequentRolls(DiceHand newHand){
        for(Die die : newHand.diceInHand){
            if (die.getDieNum()==1){
                scoringDice.add(die);
                handScore+=100;
            }else if (die.getDieNum()==5){
                scoringDice.add(die);
                handScore+=50;
            }
        }
    }

    public void scoreInitialRoll(){
        int num1s = 0;
        int num2s = 0;
        int num3s = 0;
        int num4s = 0;
        int num5s = 0;
        int num6s = 0;

        boolean fullStraight = true;//1-6 straight. Becomes false if any die num tally is more than 1
        int pairs = 0;//number of pairs
        int[] triplesTypes = new int[]{0,0};//e.g. three 4s and three 5s.
        int foursType = 0;//e.g. four 5s, four 3s, ect.
        int fivesType = 0;//e.g. five 5s, five 3s, ect.
        boolean sixOfKind = false;

        //tallies the dice and adds scoring dice to the scoringDice array
        for(Die die : diceInHand){
            switch (die.getDieNum()){
                case 1:
                    num1s++;
                    scoringDice.add(die);
                    if(num1s==2){
                        pairs++;
                        fullStraight=false;
                    } else if(num1s==3) {
                        if(triplesTypes[0]!=0){
                            triplesTypes[1]=1;
                        }else{
                            triplesTypes[0]=1;
                        }
                    } else if (num1s==4){
                        foursType = 1;
                        triplesTypes[0]=0;
                    } else if (num1s==5){
                        fivesType = 1;
                        foursType = 0;
                    } else if(num1s==6){
                        sixOfKind=true;
                        fivesType = 0;
                    }
                    break;
                case 2:
                    num2s++;
                    if(num2s==2){
                        pairs++;
                        fullStraight=false;
                    }else if(num2s==3){
                        if(triplesTypes[0]!=0){
                            triplesTypes[1]=2;
                        }else{
                            triplesTypes[0]=2;
                        }
                        scoringDice.add(die);
                    } else if (num2s==4){
                        foursType = 2;
                        triplesTypes[0]=0;
                        scoringDice.add(die);
                    } else if (num2s==5){
                        fivesType = 2;
                        foursType = 0;
                        scoringDice.add(die);
                    }else if(num2s==6){
                        sixOfKind=true;
                        fivesType = 0;
                        scoringDice.add(die);
                    }
                    break;
                case 3:
                    num3s++;
                    if(num3s==2){
                        pairs++;
                        fullStraight=false;
                    }else if(num3s==3){
                        if(triplesTypes[0]!=0){
                            triplesTypes[1]=3;
                        }else{
                            triplesTypes[0]=3;
                        }
                        scoringDice.add(die);
                    } else if (num3s==4){
                        foursType = 3;
                        triplesTypes[0]=0;
                        scoringDice.add(die);
                    } else if (num3s==5){
                        fivesType = 3;
                        foursType = 0;
                        scoringDice.add(die);
                    }else if(num3s==6){
                        sixOfKind=true;
                        fivesType = 0;
                        scoringDice.add(die);
                    }
                    break;
                case 4:
                    num4s++;
                    if(num4s==2){
                        pairs++;
                        fullStraight=false;
                    }else if(num4s==3){
                        if(triplesTypes[0]!=0){
                            triplesTypes[1]=4;
                        }else{
                            triplesTypes[0]=4;
                        }
                        scoringDice.add(die);
                    } else if (num4s==4){
                        foursType = 4;
                        triplesTypes[0]=0;
                        scoringDice.add(die);
                    } else if (num4s==5){
                        fivesType = 4;
                        foursType = 0;
                        scoringDice.add(die);
                    } else if(num4s==6){
                        sixOfKind=true;
                        fivesType = 0;
                        scoringDice.add(die);
                    }
                    break;
                case 5:
                    num5s++;
                    scoringDice.add(die);
                    if(num5s==2){
                        pairs++;
                        fullStraight=false;
                    } else if(num5s==3){
                        if(triplesTypes[0]!=0){
                            triplesTypes[1]=5;
                        }else{
                            triplesTypes[0]=5;
                        }
                        scoringDice.add(die);
                    } else if (num5s==4){
                        foursType = 5;
                        triplesTypes[0]=0;
                    } else if (num5s==5){
                        fivesType = 5;
                        foursType = 0;
                        scoringDice.add(die);
                    }else if(num5s==6){
                        sixOfKind=true;
                        fivesType = 0;
                        scoringDice.add(die);
                    }
                    break;
                case 6:
                    num6s++;
                    if(num6s==2){
                        pairs++;
                        fullStraight=false;
                    }else if(num6s==3){
                        if(triplesTypes[0]!=0){
                            triplesTypes[1]=6;
                        }else{
                            triplesTypes[0]=6;
                        }
                        scoringDice.add(die);
                    } else if (num6s==4){
                        foursType = 6;
                        triplesTypes[0]=0;
                        scoringDice.add(die);
                    } else if (num6s==5){
                        fivesType = 6;
                        foursType = 0;
                        scoringDice.add(die);
                    }else if(num6s==6){
                        sixOfKind=true;
                        fivesType = 0;
                        scoringDice.add(die);
                    }
                    break;
            }
        }

        //----------Scores the dice based on type and tally------------------
        if(!fullStraight || pairs==3 || triplesTypes[1]!=0 || sixOfKind) {
            handScore = 2600;
            return;
        }
        if(triplesTypes[0]!=0){
            if(triplesTypes[0]==1){
                handScore+=(1000+num5s*50);
            } else if(triplesTypes[0]==2){
                handScore+=(200+num5s*50+num1s*100);
            } else if(triplesTypes[0]==3){
                handScore+=(300+num5s*50+num1s*100);
            } else if(triplesTypes[0]==4){
                handScore+=(400+num5s*50+num1s*100);
            } else if(triplesTypes[0]==5){
                handScore+=(500+num1s*100);
            } else {
                handScore+=(600+num5s*50+num1s*100);
            }
        }else if (foursType!=0){
            if(foursType==1){
                handScore+=(2000+num5s*50);
            }else if (foursType==2){
                handScore+=(400+num5s*50+num1s*100);
            } else if (foursType==3){
                handScore+=(600+num5s*50+num1s*100);
            }else if (foursType==4){
                handScore+=(800+num5s*50+num1s*100);
            }else if (foursType==5){
                handScore+=(1000+num1s*100);
            }else {
                handScore+=(1200+num5s*50+num1s*100);
            }
        }else if (fivesType!=0){
            if(fivesType==1){
                handScore+=(3000+num5s*50);
            }else if (fivesType==2){
                handScore+=(600+num5s*50+num1s*100);
            } else if (fivesType==3){
                handScore+=(900+num5s*50+num1s*100);
            }else if (fivesType==4){
                handScore+=(1200+num5s*50+num1s*100);
            }else if (fivesType==5){
                handScore+=(1500+num1s*100);
            }else {
                handScore+=(1800+num5s*50+num1s*100);
            }
        }else{
            handScore+=(num5s*50+num1s*100);
        }
    }

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
