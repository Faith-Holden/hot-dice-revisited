package com;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import players.BasicPlayer;
import players.HumanPlayer;
import players.UltraConservativePlayer;

import java.util.ArrayList;

public class HotDiceGui extends Application {
    static final Object lock = new Object();
    HotDiceGuiGame game = new HotDiceGuiGame();
    ArrayList<BasicPlayer> players;
    int currentPlayerNum = 0;
    BasicPlayer currentPlayer;
    Canvas baseCanvas;
    Label[] playerLabels;
    Label[] scoreLabels;
    StackPane root;
    Label turnInfoLabel;
    Pane elementPane;
    Canvas animationCanvas;
    Stage gameStage;
    Button endTurn;

    public void start(Stage primaryStage){
        gameStage = primaryStage;
         baseCanvas = new Canvas(920, 700);
         animationCanvas = new Canvas(baseCanvas.getWidth(),baseCanvas.getHeight());
         elementPane = new Pane();

        root = new StackPane();
        root.getChildren().add(baseCanvas);
        root.getChildren().add(animationCanvas);
        root.getChildren().add(elementPane);
        root.setStyle("-fx-background-color: rgb(35, 95, 50)");
        root.setPrefWidth(baseCanvas.getWidth());
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        players = game.getPlayers();

        gameStage.setResizable(false);

        makeStartGamePopUp();
    }

    public void makeStartGamePopUp(){

        Pane startRoot = new Pane();
        startRoot.setStyle("-fx-background-color: rgb(140, 100, 80)");
        Scene scene = new Scene(startRoot, 350, 310);
        Stage stage = new Stage();
        stage.setScene(scene);

        Label playerNumLabel = new Label("Choose how many players to Include.");
        playerNumLabel.setWrapText(true);
        playerNumLabel.setPrefWidth(150);
        playerNumLabel.setFont(Font.font(null, FontWeight.BOLD,15));
        playerNumLabel.setTextFill(Color.BLACK);
        playerNumLabel.relocate(startRoot.getWidth()-160,10);
        startRoot.getChildren().add(playerNumLabel);

        ToggleGroup playerNumToggle = new ToggleGroup();
        RadioMenuItem onePlayer = new RadioMenuItem("One Player");
        onePlayer.setToggleGroup(playerNumToggle);
        RadioMenuItem twoPlayers = new RadioMenuItem("Two Players");
        twoPlayers.setToggleGroup(playerNumToggle);
        RadioMenuItem threePlayers = new RadioMenuItem("Three Players");
        threePlayers.setToggleGroup(playerNumToggle);
        RadioMenuItem fourPlayers = new RadioMenuItem("Four Players");
        fourPlayers.setToggleGroup(playerNumToggle);
        RadioMenuItem fivePlayers = new RadioMenuItem("Five Players");
        fivePlayers.setToggleGroup(playerNumToggle);
        RadioMenuItem sixPlayers = new RadioMenuItem("Six Players");
        sixPlayers.setToggleGroup(playerNumToggle);
        RadioMenuItem sevenPlayers = new RadioMenuItem("Seven Players");
        sevenPlayers.setToggleGroup(playerNumToggle);
        RadioMenuItem eightPlayers = new RadioMenuItem("Eight Players");
        eightPlayers.setToggleGroup(playerNumToggle);
        MenuButton playerNumButton = new MenuButton("Number of Players...");
        playerNumButton.getItems().addAll(onePlayer,twoPlayers,threePlayers,fourPlayers,fivePlayers,sixPlayers,sevenPlayers,eightPlayers);
        playerNumButton.setPrefWidth(150);
        playerNumButton.relocate(startRoot.getWidth()-160,60);
        playerNumButton.setFont(Font.font(null, FontWeight.BOLD,12));
        startRoot.getChildren().add(playerNumButton);

        Label playerChoiceLabel = new Label("Choose players to include in the game.");
        playerChoiceLabel.setWrapText(true);
        playerChoiceLabel.setPrefWidth(150);
        playerChoiceLabel.setFont(Font.font(null, FontWeight.BOLD,15));
        playerChoiceLabel.setTextFill(Color.BLACK);
        playerChoiceLabel.relocate(10,10);
        startRoot.getChildren().add(playerChoiceLabel);

        String[]choiceStrings = new String[]{"Human", "Ultra Conservative Bot"};

        MenuButton[] playerChoiceButtons = new MenuButton[8];
        for (int i = 0; i<8; i++){
            playerChoiceButtons[i]=new MenuButton("None");
            ToggleGroup choiceItemToggle = new ToggleGroup();
            RadioMenuItem[] choiceItems = new RadioMenuItem[choiceStrings.length];
            for(int j=0; j<choiceItems.length;j++){
                choiceItems[j]=new RadioMenuItem(choiceStrings[j]);
                choiceItems[j].setToggleGroup(choiceItemToggle);
            }
            for (MenuItem choiceItem : choiceItems) {
                playerChoiceButtons[i].getItems().add(choiceItem);
            }
            playerChoiceButtons[i].setFont(Font.font(null, FontWeight.BOLD,12));
            playerChoiceButtons[i].setPrefWidth(150);
            startRoot.getChildren().add(playerChoiceButtons[i]);
            playerChoiceButtons[i].relocate(10, i*30+60);
            playerChoiceButtons[i].setDisable(true);
        }

        onePlayer.setOnAction(e -> enableButtons(playerChoiceButtons, 1));
        twoPlayers.setOnAction(e -> enableButtons(playerChoiceButtons, 2));
        threePlayers.setOnAction(e -> enableButtons(playerChoiceButtons, 3));
        fourPlayers.setOnAction(e -> enableButtons(playerChoiceButtons, 4));
        fivePlayers.setOnAction(e -> enableButtons(playerChoiceButtons, 5));
        sixPlayers.setOnAction(e -> enableButtons(playerChoiceButtons, 6));
        sevenPlayers.setOnAction(e -> enableButtons(playerChoiceButtons, 7));
        eightPlayers.setOnAction(e -> enableButtons(playerChoiceButtons, 8));

        Button startGameButton = new Button("Start Game");
        startGameButton.setPrefWidth(150);
        startGameButton.relocate(startRoot.getWidth()-160,startRoot.getHeight()-40);
        startGameButton.setFont(Font.font(null, FontWeight.BOLD,12));
        startGameButton.setDisable(true);
        startGameButton.setOnAction(e->
        {
            stage.close();
            onStartButtonClicked(playerChoiceButtons);
        });
        startRoot.getChildren().add(startGameButton);

        for(MenuButton menuButton: playerChoiceButtons){
            for(MenuItem playerType : menuButton.getItems()){
                playerType.setOnAction(e -> {
                    startGameButton.setDisable(false);
                    menuButton.setText(playerType.getText());
                });
            }
        }
        stage.setResizable(false);
        stage.show();
    }

    private void enableButtons(MenuButton[] playerTypes, int numOfPlayers){
        for (int i = 0; i<8; i++){
            playerTypes[i].setDisable(!(i < numOfPlayers));
        }
    }

    private void onStartButtonClicked(MenuButton[] playerTypes){
        ArrayList<BasicPlayer> newPlayerList = new ArrayList<>();
        String buttonText;
        BasicPlayer playerToAdd;
        for(MenuButton menuButton : playerTypes){
            if(!menuButton.isDisabled()){
                buttonText = menuButton.getText();
            }
            else{
                continue;
            }
            switch (buttonText){
                case "Human":
                    playerToAdd = new HumanPlayer();
                    break;
                case "Ultra Conservative Bot":
                    playerToAdd = new UltraConservativeGuiPlayer();
                    break;
                default:
                    throw new IllegalArgumentException(buttonText + " was not able to be added." +
                            "this is due to " + buttonText + " not being defined in the onStartButtonClicked method." );
            }
            newPlayerList.add(playerToAdd);
        }
        players.addAll(newPlayerList);

        drawInitialBoard(players.size());
        gameStage.show();

        GamePlayThread gamePlayThread = new GamePlayThread();
        gamePlayThread.setDaemon(true);
        gamePlayThread.start();
    }

    public void makeMenuBar(){
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        MenuItem saveGame = new MenuItem("Save Game");
        MenuItem loadGame = new MenuItem("Load Game");
        fileMenu.getItems().addAll(saveGame, loadGame);

        Menu helpMenu = new Menu("Help");
        MenuItem howToPlay = new MenuItem("How to play");
        MenuItem aboutGame = new MenuItem("About Hot Dice");
        MenuItem aboutPlayerType1 = new MenuItem("About the Ultra Conservative Player Bot");
        helpMenu.getItems().addAll(howToPlay,aboutGame,aboutPlayerType1);


        menuBar.getMenus().addAll(fileMenu,helpMenu);
        menuBar.setPrefWidth(root.getPrefWidth());
        elementPane.getChildren().add(menuBar);
    }

    public void drawInitialBoard(int playerNums){
        makeMenuBar();
        drawLabels();
        drawRollArea();
        drawScoreBoard();
        GraphicsContext graphicsContext = baseCanvas.getGraphicsContext2D();
        graphicsContext.setStroke(Color.BLACK);
        for(int i = 0; i<playerNums && i<8; i++){
            if(i<4){
                graphicsContext.strokeRect(10+(225*i),baseCanvas.getHeight()-155, 215, 145);
            }else{
                graphicsContext.strokeRect(10+(225*(i-4)),35, 215, 145);
            }
        }
    }

    public void drawLabels(){

//        turnInfoLabel = new Label("Current Player is Player " + (currentPlayerNum+1));
//        turnInfoLabel.setAlignment(Pos.BASELINE_CENTER);
//        turnInfoLabel.setPrefWidth(215);
//        turnInfoLabel.setWrapText(true);
//        turnInfoLabel.setFont(Font.font(null,FontWeight.BOLD, 20));
//        turnInfoLabel.setTextFill(Color.BLACK);
//        turnInfoLabel.relocate(650,baseCanvas.getHeight()/2-50);
//        elementPane.getChildren().add(turnInfoLabel);

        playerLabels = new Label[players.size()];
        for(int i = 0; i<players.size(); i++){
            playerLabels[i]=new Label("Player " + (i+1));
            playerLabels[i].setPrefWidth(215);
            playerLabels[i].setAlignment(Pos.BASELINE_CENTER);
            playerLabels[i].setFont(Font.font(null,FontWeight.BOLD, 14));
            playerLabels[i].setTextFill(Color.BLACK);
            switch (i+1){
                case 1:
                    playerLabels[i].relocate(10, baseCanvas.getHeight()-185);
                    break;
                case 2:
                    playerLabels[i].relocate(235, baseCanvas.getHeight()-185);
                    break;
                case 3:
                    playerLabels[i].relocate(460, baseCanvas.getHeight()-185);
                    break;
                case 4:
                    playerLabels[i].relocate(685, baseCanvas.getHeight()-185);
                    break;
                case 5:
                    playerLabels[i].relocate(10,185);
                    break;
                case 6:
                    playerLabels[i].relocate(235, 185);
                    break;
                case 7:
                    playerLabels[i].relocate(460, 185);
                    break;
                case 8:
                    playerLabels[i].relocate(685, 185);
                    break;
            }
            elementPane.getChildren().add(playerLabels[i]);
        }

        int scoreBoardTop = (int)baseCanvas.getHeight()/2-112;
        int scoreBoardLeft = 160;

        Label scoreBoard = new Label("Score Board");
        scoreBoard.setTextFill(Color.BLACK);
        scoreBoard.setFont(Font.font(null,FontWeight.BOLD, 14));
        scoreBoard.relocate(scoreBoardLeft+17, scoreBoardTop+2);
        elementPane.getChildren().add(scoreBoard);
        Label[] playerScoreLabel;
        playerScoreLabel = new Label[players.size()];
        for(int i = 0; i<players.size(); i++){
            playerScoreLabel[i]=new Label("Player " + (i+1) + ":");
            playerScoreLabel[i].setPrefWidth(100);
            playerScoreLabel[i].setAlignment(Pos.CENTER_LEFT);
            playerScoreLabel[i].setFont(Font.font(null,FontWeight.BOLD, 14));
            playerScoreLabel[i].setTextFill(Color.BLACK);
            playerScoreLabel[i].relocate(scoreBoardLeft+2, (scoreBoardTop+28)+i*25);
            elementPane.getChildren().add(playerScoreLabel[i]);
        }

        scoreLabels = new Label[players.size()];
        for(int i = 0; i<players.size(); i++){
            scoreLabels[i]=new Label(String.valueOf(players.get(i).getPlayerScore()));
            scoreLabels[i].setPrefWidth(100);
            scoreLabels[i].setAlignment(Pos.CENTER_LEFT);
            scoreLabels[i].setFont(Font.font(null,FontWeight.BOLD, 14));
            scoreLabels[i].setTextFill(Color.BLACK);
            scoreLabels[i].relocate(scoreBoardLeft+65, (scoreBoardTop+28)+(i*25));
            elementPane.getChildren().add(scoreLabels[i]);
        }


    }

    public void updateTurnInfo(){
        GraphicsContext graphicsContext = animationCanvas.getGraphicsContext2D();
        int scoreBoardTop = (int)baseCanvas.getHeight()/2-112;
        int scoreBoardLeft = 160;

        drawScoreBoard();
        graphicsContext.setLineWidth(3);
        graphicsContext.setStroke(Color.SKYBLUE);
        graphicsContext.strokeRect(scoreBoardLeft,(scoreBoardTop)+(currentPlayerNum)*25,125,25);
    }

    public void drawKeptDice(DiceHand playerDiceHand, int playerLocation){
        GraphicsContext graphicsContext = animationCanvas.getGraphicsContext2D();
        int bowlXEdge = (int) baseCanvas.getWidth()/2-167;
        int bowlYEdge = (int) baseCanvas.getHeight()/2-125;
        graphicsContext.clearRect(bowlXEdge, bowlYEdge, 335,250);
        int xStartingCoord;
        int yStartingCoord;
        switch (playerLocation){
            case 0:
                xStartingCoord=10;
                yStartingCoord=(int) baseCanvas.getHeight()-155;
                break;
            case 1:
                xStartingCoord=235;
                yStartingCoord=(int) baseCanvas.getHeight()-155;
                break;
            case 2:
                xStartingCoord=460;
                yStartingCoord=(int) baseCanvas.getHeight()-155;
                break;
            case 3:
                xStartingCoord=685;
                yStartingCoord=(int) baseCanvas.getHeight()-155;
                break;
            case 4:
                xStartingCoord=10;
                yStartingCoord=35;
                break;
            case 5:
                xStartingCoord=235;
                yStartingCoord=35;
                break;
            case 6:
                xStartingCoord=460;
                yStartingCoord=35;
                break;
            case 7:
                xStartingCoord=685;
                yStartingCoord=35;
                break;

            default:
                return;
        }

        graphicsContext.setFill(Color.rgb(35,95,50));
        graphicsContext.fillRect(xStartingCoord,yStartingCoord,215,145);


        graphicsContext.setStroke(Color.SKYBLUE);
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

        synchronized (lock){
            lock.notify();
        }
    }

    public void drawRollArea (){
        int xEdge = (int) baseCanvas.getWidth()/2-167;
        int yEdge = (int) baseCanvas.getHeight()/2-125;
        GraphicsContext graphicsContext = baseCanvas.getGraphicsContext2D();
        graphicsContext.setFill(Color.rgb(35,95,50));
        Image bowlImage = new Image("resources\\images\\rectangle bowl.png");
        graphicsContext.drawImage(bowlImage,xEdge,yEdge);
        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.strokeRect(xEdge,yEdge,335,250);
    }

    public void drawScoreBoard (){
        int scoreBoardTop = (int)baseCanvas.getHeight()/2-112;
        int scoreBoardLeft = 160;

        GraphicsContext graphicsContext = baseCanvas.getGraphicsContext2D();
        graphicsContext.setFill(Color.BLANCHEDALMOND);
        graphicsContext.fillRect(scoreBoardLeft,(scoreBoardTop),125,25*(players.size()+1));
        graphicsContext.strokeRect(scoreBoardLeft,(scoreBoardTop),125,25*(players.size()+1));
        for(int i=0; i<players.size(); i++){
            graphicsContext.strokeRect(scoreBoardLeft,(scoreBoardTop)+i*25,125,25);
            scoreLabels[i].setText(String.valueOf(players.get(i).getPlayerScore()));
        }
    }

    public void drawRolledDice(DiceHand rolledDice){
        GraphicsContext graphicsContext = animationCanvas.getGraphicsContext2D();
        Image diceImage = new Image("resources\\images\\dice.png");
        int bowlXEdge = (int) baseCanvas.getWidth()/2-167;
        int bowlYEdge = (int) baseCanvas.getHeight()/2-125;
        graphicsContext.clearRect(bowlXEdge, bowlYEdge, 335,250);

        for(int i = 0; i<rolledDice.getHandSize();i++){
            Die die = rolledDice.getDiceInHand().get(i);
            int dieX;
            int dieY;
            switch (i){
                case 0:
                    dieX = bowlXEdge+50;
                    dieY = bowlYEdge+50;
                    break;
                case 1:
                    dieX = bowlXEdge+50;
                    dieY = bowlYEdge+135;
                    break;
                case 2:
                    dieX = bowlXEdge+135;
                    dieY = bowlYEdge+50;
                    break;
                case 3:
                    dieX = bowlXEdge+135;
                    dieY = bowlYEdge+135;
                    break;
                case 4:
                    dieX = bowlXEdge+220;
                    dieY = bowlYEdge+50;
                    break;
                case 5:
                    dieX = bowlXEdge+220;
                    dieY = bowlYEdge+135;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + i);
            }
            graphicsContext.drawImage(diceImage,(die.getDieNum()-1)*65,0, 65, 65, dieX,dieY,65,65);
        }
    }

    public void doRollAnimation(DiceHand rolledDice) {

        GraphicsContext graphicsContext = animationCanvas.getGraphicsContext2D();
        Image diceImage = new Image("resources\\images\\dice.png");
        int bowlXEdge = (int) baseCanvas.getWidth()/2-167;
        int bowlYEdge = (int) baseCanvas.getHeight()/2-125;
        graphicsContext.clearRect(bowlXEdge, bowlYEdge, 335,250);

        final long startTime = System.nanoTime();
        AnimationTimer rollTimer = new AnimationTimer() {
            long previousTime;
            @Override
            public void handle(long currentTime) {
                if(currentTime-previousTime > 10){
                    for(int i = 0; i<rolledDice.getHandSize();i++){
                        int dieNum = (int)(Math.random()*6);
                        int dieX;
                        int dieY;
                        switch (i){
                            case 0:
                                dieX = bowlXEdge+50;
                                dieY = bowlYEdge+50;
                                break;
                            case 1:
                                dieX = bowlXEdge+50;
                                dieY = bowlYEdge+135;
                                break;
                            case 2:
                                dieX = bowlXEdge+135;
                                dieY = bowlYEdge+50;
                                break;
                            case 3:
                                dieX = bowlXEdge+135;
                                dieY = bowlYEdge+135;
                                break;
                            case 4:
                                dieX = bowlXEdge+220;
                                dieY = bowlYEdge+50;
                                break;
                            case 5:
                                dieX = bowlXEdge+220;
                                dieY = bowlYEdge+135;
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + i);
                        }
                         graphicsContext.drawImage(diceImage,dieNum*65,0, 65, 65, dieX,dieY,65,65);
                    }
                }
                if(currentTime-startTime>0.5e9){
                    this.stop();
                    drawRolledDice(rolledDice);
                    synchronized (lock){
                        lock.notify();
                    }
                }
                previousTime = currentTime;
            }
        };


        rollTimer.start();



    }

    private void doWait(){
        synchronized (lock){
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void doEndGuiUpdate(BasicPlayer winningPlayer){
        GraphicsContext graphicsContext = animationCanvas.getGraphicsContext2D();
        graphicsContext.clearRect(0,0,baseCanvas.getWidth(),baseCanvas.getHeight());
        graphicsContext.setFill(Color.SKYBLUE);
        graphicsContext.setFont(Font.font(null, FontWeight.BOLD, 30));
        graphicsContext.fillText("Congratulations, Player " + (players.indexOf(winningPlayer)+1) + "!",
                baseCanvas.getWidth()/2-120,baseCanvas.getHeight()/2);
    }

    private void startGuiForHuman(){
        endTurn = new Button("End Turn");
        endTurn.setVisible(false);
        endTurn.setPrefWidth(100);
        endTurn.relocate(baseCanvas.getWidth()-110, baseCanvas.getHeight()/2);
        elementPane.getChildren().add(endTurn);


    }

    private void updateGuiForHuman(){
        endTurn.setVisible(true);
    }

    //-------------------nested player classes--------------------
    private class UltraConservativeGuiPlayer extends UltraConservativePlayer {
        @Override
        public void updateGui(){
            Platform.runLater(() -> drawKeptDice(this.getPlayerHand(), players.indexOf(this)));
            Platform.runLater(HotDiceGui.this::drawScoreBoard);
            doWait();
        }

        @Override
        public void runAnimation1(DiceHand rolledDice){
            Platform.runLater(()->doRollAnimation(rolledDice));
            doWait();
            synchronized (lock){
                try {
                    lock.wait(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //------------------------------------------------------------


    //------------------other nested classes----------------------

    private class GamePlayThread extends Thread{
        @Override
        public void run() {
            BasicPlayer winningPlayer = game.playGame();
            doEndGuiUpdate(winningPlayer);
        }
    }

    public class HotDiceGuiGame extends HotDiceGame{
        @Override
        public void updateGameBoard(){


            synchronized (lock){
                try {
                    lock.wait(2500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if(currentPlayerNum<players.size()){
                currentPlayerNum++;
            }else{
                currentPlayerNum=1;
            }
            animationCanvas.getGraphicsContext2D().clearRect(0,0,animationCanvas.getWidth(),animationCanvas.getHeight());
            updateTurnInfo();
        }
    }
    //------------------------------------------------------------

    public static void main(String[]args){
        launch(args);
    }

}
