package com;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import players.BasicPlayer;
import players.UltraConservativePlayer;

import java.util.ArrayList;

public class HotDiceGui extends Application {
    ArrayList<BasicPlayer> players;
    int currentPlayer = 1;
    Canvas canvas;

    public void start(Stage primaryStage){
         canvas = new Canvas(920, 700);

        BorderPane root = new BorderPane(canvas);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        HotDiceGame game = new HotDiceGame();
        players = game.getPlayers();


//        Label label = new Label("test");
//        Label currentPlayerLabel = new Label ("It is player+ currentPlayer + 's turn");
//        root.getChildren().add(currentPlayerLabel);
//        currentPlayerLabel.localToParent(30,50);


        players.add(new UltraConservativeGuiPlayer());
        players.get(0).playTurn();

        drawTable();

        primaryStage.show();
    }

    public void drawTable(){
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.setFill(Color.rgb(35,95,50));
        graphicsContext.fillRect(0,0, canvas.getWidth(),canvas.getHeight());
        drawRollArea();
        for(int i = 1; i<players.size()+1; i++){
            drawPlayerArea(players.get(i-1),i);
        }


    }

    public <T extends BasicPlayer>void drawPlayerArea(T player, int playerLocation){
        int xStartingCoord;
        int yStartingCoord;

        switch (playerLocation){
            case 1:
                xStartingCoord=10;
                yStartingCoord=(int)canvas.getHeight()-155;
                break;
            case 2:
                xStartingCoord=235;
                yStartingCoord=(int)canvas.getHeight()-155;
                break;
            case 3:
                xStartingCoord=460;
                yStartingCoord=(int)canvas.getHeight()-155;
                break;
            case 4:
                xStartingCoord=685;
                yStartingCoord=(int)canvas.getHeight()-155;
                break;
            case 5:
                xStartingCoord=10;
                yStartingCoord=10;
                break;
            case 6:
                xStartingCoord=235;
                yStartingCoord=10;
                break;
            case 7:
                xStartingCoord=460;
                yStartingCoord=10;
                break;
            case 8:
                xStartingCoord=685;
                yStartingCoord=10;
                break;

            default:
                return;
        }
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.setFill(Color.rgb(35,95,50));
        graphicsContext.fillRect(xStartingCoord,yStartingCoord,215,145);

        DiceHand playerDiceHand = player.getPlayerHand();
        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.strokeRect(xStartingCoord,yStartingCoord,215,145);

        Image diceImage = new Image("resources\\images\\dice.png");

        for(int i = 0; i<playerDiceHand.getHandSize();i++){
            Die die = playerDiceHand.getDiceInHand().get(i);
            int dieX;
            int dieY;

            switch (i){
                case 0:
                    dieX = xStartingCoord+5;
                    dieY = yStartingCoord+5;
                    break;
                case 1:
                    dieX = xStartingCoord+5;
                    dieY = yStartingCoord+75;
                    break;
                case 2:
                    dieX = xStartingCoord+75;
                    dieY = yStartingCoord+5;
                    break;
                case 3:
                    dieX = xStartingCoord+75;
                    dieY = yStartingCoord+75;
                    break;
                case 4:
                    dieX = xStartingCoord+145;
                    dieY = yStartingCoord+5;
                    break;
                case 5:
                    dieX = xStartingCoord+145;
                    dieY = yStartingCoord+75;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + i);
            }

            graphicsContext.drawImage(diceImage,(die.getDieNum()-1)*65,0, 65, 65, dieX,dieY,65,65);
        }
    }

    public void drawRollArea (){
        int xEdge = (int)canvas.getWidth()/2-120;
        int yEdge = (int)canvas.getHeight()/2-120;
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.setFill(Color.rgb(35,95,50));
        graphicsContext.fillRect(xEdge,yEdge,240,240);
        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.strokeRect(xEdge,yEdge,240,240);
    }



    private class UltraConservativeGuiPlayer extends UltraConservativePlayer {
        @Override
        public void updateGui(){
            drawPlayerArea(this,players.indexOf(this));
        }
    }


    public static void main(String[]args){
        launch(args);
    }

}
