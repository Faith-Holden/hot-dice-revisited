package com;

import javafx.application.Application;
import javafx.stage.Stage;

import java.util.ArrayList;

public class HotDiceGui extends Application {
    ArrayList<BasicPlayer> players;

    public void start(Stage primaryStage){
        HotDiceGame game = new HotDiceGame();
        players = game.getPlayers();




        primaryStage.show();
    }


    public static void main(String[]args){
        launch(args);
    }

}
