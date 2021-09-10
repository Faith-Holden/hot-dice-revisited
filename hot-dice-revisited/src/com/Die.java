package com;

public class Die {
    private int dieNum;
    private int scoreType;
    private int timesRolled = 0;

    public Die(int dieNum){
        this.dieNum = dieNum;
    }
    public Die(){
        this.dieNum = (int)(Math.random()*6)+1;
    }

    public void setDieNum(int dieNum) {
        this.dieNum = dieNum;
    }

    public int getDieNum() {
        return dieNum;
    }

    public void setScoreType(int scoreType) {
        this.scoreType = scoreType;
    }

    public int getScoreType() {
        return scoreType;
    }

    public int getTimesRolled() {
        return timesRolled;
    }

    public void setTimesRolled(int timesRolled) {
        this.timesRolled = timesRolled;
    }

    @Override
    public String toString(){
        return String.valueOf(dieNum);
    }
}
