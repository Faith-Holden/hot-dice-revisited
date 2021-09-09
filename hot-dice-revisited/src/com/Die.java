package com;

public class Die {
    private int dieVal;

    public Die(int dieVal){
        this.dieVal = dieVal;
    }

    public Die(){
        this.dieVal = (int)(Math.random()*6)+1;
    }

    public void setDieVal(int dieVal) {
        this.dieVal = dieVal;
    }

    public int getDieVal() {
        return dieVal;
    }

    @Override
    public String toString(){
        return String.valueOf(dieVal);
    }
}
