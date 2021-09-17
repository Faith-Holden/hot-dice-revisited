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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import players.BasicPlayer;
import players.ConservativePlayer;
import players.HumanPlayer;
import players.UltraConservativePlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

public class HotDiceGui extends Application {
    volatile boolean shouldTerminateGamePlayThread = false;
    volatile boolean clearToRestart = false;
    private boolean paused = false;
    boolean notified = false;
    static final Object lock = new Object();
    HotDiceGuiGame game = new HotDiceGuiGame();
    ArrayList<BasicPlayer> players;
    Canvas baseCanvas;
    Label[] playerLabels;
    Label[] scoreLabels;
    Label[] playerTypeLabels;
    StackPane root;
    Pane elementPane;
    Canvas animationCanvas;
    Stage gameStage;
    Button [] diceButtons;
    Button pauseButton;
    Button endTurnButton;
    Button submitDiceButton;
    Button rollDiceButton;
    Label turnInfoLabel;

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
        doStartUpAlert();
    }

    private void onNewGameClicked(){
        togglePausedOrResumed();
        Alert confirmNewGame = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to start a new game? This will end your current one.", ButtonType.YES, ButtonType.NO);
        confirmNewGame.showAndWait();
        if(confirmNewGame.getResult()==ButtonType.YES){
            togglePausedOrResumed();
            shouldTerminateGamePlayThread = true;
            doNotify();
            while (!clearToRestart) {
                Thread.onSpinWait();
            }
            gameStage.close();
            clearToRestart = false;
            setGameStartingVariables();
            StartMenuPopUp startMenuPopUp = new StartMenuPopUp();
            startMenuPopUp.setUp();
        }else{
            togglePausedOrResumed();
        }
    }

    //-------------Methods to set up the gui-------------------
    private void doStartUpAlert(){
        ButtonType loadGame = new ButtonType("Load Game", ButtonBar.ButtonData.YES);
        ButtonType newGame = new ButtonType("New Game", ButtonBar.ButtonData.NO);
        Alert loadOrNewAlert = new Alert(Alert.AlertType.CONFIRMATION,
                "Click 'Load Game' to load a game from a file, or click 'New Game' to create a new game.", loadGame, newGame);
        loadOrNewAlert.setHeaderText("The game \"Hot Dice\", also commonly known as \"Farkle\", is a folk dice game played in a series of rounds." +
                                    "\nIn each round, players roll a hand of dice repeatedly until they decide to end their turn, or until they" +
                                    "\n\"Farkle\" by rolling a hand with no scoring dice." +
                                    "\nScores are based on the number of 1s or 5s in a hand, and by straights, 3 pairs, or sets of 3 or more dice." +
                                    "\nFor more information on the game play, please see \"Help\" section of the menu.");
        loadOrNewAlert.showAndWait();
        if (loadOrNewAlert.getResult() == loadGame) {
            game.loadGameFromFile();
        }else{
            StartMenuPopUp startMenuPopUp = new StartMenuPopUp();
            startMenuPopUp.setUp();
        }
    }

    private void setGameStartingVariables(){
        elementPane.getChildren().clear();
        shouldTerminateGamePlayThread = false;
        clearToRestart = false;
        paused = false;
        notified = false;
        game.currentPlayerNum = 0;
        scoreLabels = new Label[players.size()];
    }

    private void makeMenuBar(){
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        MenuItem saveGame = new MenuItem("Save Game");
        MenuItem loadGame = new MenuItem("Load Game");
        MenuItem newGame = new MenuItem("New Game");
        fileMenu.getItems().addAll(saveGame, loadGame, newGame);
        saveGame.setOnAction(e->game.selectSaveFileAndSave());
        newGame.setOnAction(e -> onNewGameClicked());
        loadGame.setOnAction(e -> {
            game.loadGameFromFile();
        });
        fileMenu.setOnShowing(e->togglePausedOrResumed());
        fileMenu.setOnHiding(e->togglePausedOrResumed());

        Menu helpMenu = new Menu("Help");
        MenuItem howToPlay = new MenuItem("How to Play");
//        MenuItem aboutGame = new MenuItem("About Hot Dice");
        MenuItem aboutUltraConservativePlayerBot = new MenuItem("About the Ultra Conservative Player Bot");
        helpMenu.getItems().addAll(howToPlay,aboutUltraConservativePlayerBot);
        howToPlay.setOnAction(e -> doHowToPlayPopUp());
        aboutUltraConservativePlayerBot.setOnAction(e -> doUltraConservativePlayerInfoPopup());

        helpMenu.setOnShowing(e->togglePausedOrResumed());
        helpMenu.setOnHiding(e->togglePausedOrResumed());

        menuBar.getMenus().addAll(fileMenu,helpMenu);
        menuBar.setPrefWidth(root.getPrefWidth());
        elementPane.getChildren().add(menuBar);
    }

    private void makeInitialBoard(int playerNums){
        GraphicsContext baseCanvasGContext = baseCanvas.getGraphicsContext2D();
        baseCanvasGContext.clearRect(0,0,baseCanvas.getWidth(),baseCanvas.getHeight());

        GraphicsContext animationCanvasGContext = animationCanvas.getGraphicsContext2D();
        animationCanvasGContext.clearRect(0,0,baseCanvas.getWidth(),baseCanvas.getHeight());

        baseCanvasGContext.setStroke(Color.BLACK);
        makeMenuBar();
        makeLabels();
        makeRollArea();
        makeScoreBoard();
        makeButtons();

        for(int i = 0; i<playerNums && i<8; i++){
            if(i<4){
                baseCanvasGContext.strokeRect(10+(225*i),baseCanvas.getHeight()-155, 215, 145);
            }else{
                baseCanvasGContext.strokeRect(10+(225*(i-4)),35, 215, 145);
            }
        }
    }

    private void makeLabels(){
        int bowlXInsideEdge = (int) baseCanvas.getWidth()/2-117;
        int bowlYInsideEdge = (int) baseCanvas.getHeight()/2-75;

        turnInfoLabel = new Label("Player " + (game.currentPlayerNum+1) + "'s turn.");
        turnInfoLabel.setVisible(false);
        turnInfoLabel.setTextFill(Color.SKYBLUE);
        turnInfoLabel.setAlignment(Pos.BASELINE_CENTER);
        turnInfoLabel.setPrefWidth(235);
        turnInfoLabel.setPrefHeight(150);
        turnInfoLabel.setWrapText(true);
        turnInfoLabel.setFont(Font.font(null,FontWeight.BOLD, 20));
        turnInfoLabel.relocate(bowlXInsideEdge,bowlYInsideEdge);
        elementPane.getChildren().add(turnInfoLabel);

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

        Label playerTypeLabel = new Label("Player Types");
        playerTypeLabel.setTextFill(Color.BLACK);
        playerTypeLabel.setFont(Font.font(null,FontWeight.BOLD, 14));
        playerTypeLabel.relocate(scoreBoardLeft-110, scoreBoardTop+2);
        elementPane.getChildren().add(playerTypeLabel);

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
        playerTypeLabels = new Label[players.size()];
        for(int i = 0; i<players.size(); i++){
            playerTypeLabels[i]=new Label(players.get(i).getPlayerString());
            playerTypeLabels[i].setPrefWidth(150);
            playerTypeLabels[i].setAlignment(Pos.BASELINE_CENTER);
            playerTypeLabels[i].setFont(Font.font(null,FontWeight.BOLD, 14));
            playerTypeLabels[i].setTextFill(Color.BLACK);
            playerTypeLabels[i].relocate(scoreBoardLeft+-150, (scoreBoardTop+28)+(i*25));
            elementPane.getChildren().add(playerTypeLabels[i]);
        }
    }

    private void makeButtons(){
        int drawAreaXEdge = (int) baseCanvas.getWidth()/2-117;
        int drawAreaYEdge = (int) baseCanvas.getHeight()/2-75;
        int distanceMultiplier = 85;

        diceButtons = new Button[6];

        Button die1Button = new Button();
        die1Button.setVisible(false);
        die1Button.setStyle("-fx-border-color: transparent; -fx-border-width: 0; -fx-background-radius: 0; -fx-background-color: transparent;");
        die1Button.setPrefWidth(65);
        die1Button.setPrefHeight(65);
        die1Button.relocate(drawAreaXEdge, drawAreaYEdge);
        diceButtons[0]=die1Button;
        Button die2Button = new Button();
        die2Button.setVisible(false);
        die2Button.setStyle("-fx-border-color: transparent; -fx-border-width: 0; -fx-background-radius: 0; -fx-background-color: transparent;");
        die2Button.setPrefWidth(65);
        die2Button.setPrefHeight(65);
        die2Button.relocate(drawAreaXEdge, drawAreaYEdge+distanceMultiplier);
        diceButtons[1]=die2Button;
        Button die3Button = new Button();
        die3Button.setVisible(false);
        die3Button.setStyle("-fx-border-color: transparent; -fx-border-width: 0; -fx-background-radius: 0; -fx-background-color: transparent;");
        die3Button.setPrefWidth(65);
        die3Button.setPrefHeight(65);
        die3Button.relocate(drawAreaXEdge+distanceMultiplier, drawAreaYEdge);
        diceButtons[2]=die3Button;
        Button die4Button = new Button();
        die4Button.setVisible(false);
        die4Button.setStyle("-fx-border-color: transparent; -fx-border-width: 0; -fx-background-radius: 0; -fx-background-color: transparent;");
        die4Button.setPrefWidth(65);
        die4Button.setPrefHeight(65);
        die4Button.relocate(drawAreaXEdge+distanceMultiplier, drawAreaYEdge+distanceMultiplier);
        diceButtons[3]=die4Button;
        Button die5Button = new Button();
        die5Button.setVisible(false);
        die5Button.setStyle("-fx-border-color: transparent; -fx-border-width: 0; -fx-background-radius: 0; -fx-background-color: transparent;");
        die5Button.setPrefWidth(65);
        die5Button.setPrefHeight(65);
        die5Button.relocate(drawAreaXEdge+distanceMultiplier*2, drawAreaYEdge);
        diceButtons[4]=die5Button;
        Button die6Button = new Button();
        die6Button.setVisible(false);
        die6Button.setStyle("-fx-border-color: transparent; -fx-border-width: 0; -fx-background-radius: 0; -fx-background-color: transparent;");
        die6Button.setPrefWidth(65);
        die6Button.setPrefHeight(65);
        die6Button.relocate(drawAreaXEdge+distanceMultiplier*2, drawAreaYEdge+distanceMultiplier);
        diceButtons[5]=die6Button;

        endTurnButton = new Button("End Turn");
        endTurnButton.setDisable(true);
        endTurnButton.setPrefWidth(100);
        endTurnButton.relocate(baseCanvas.getWidth()-110, baseCanvas.getHeight()/2+50);

        pauseButton = new Button("Pause");
        pauseButton.setDisable(false);
        pauseButton.setPrefWidth(100);
        pauseButton.relocate(baseCanvas.getWidth()-110, baseCanvas.getHeight()/2-100);
        pauseButton.setOnAction(e -> togglePausedOrResumed());

        rollDiceButton = new Button("Roll Dice");
        rollDiceButton.setDisable(true);
        rollDiceButton.setPrefWidth(100);
        rollDiceButton.relocate(baseCanvas.getWidth()-110, baseCanvas.getHeight()/2);

        submitDiceButton = new Button("Submit Dice");
        submitDiceButton.setDisable(true);
        submitDiceButton.setPrefWidth(100);
        submitDiceButton.relocate(baseCanvas.getWidth()-110, baseCanvas.getHeight()/2-50);

        elementPane.getChildren().addAll(die1Button,die2Button,die3Button,die4Button,die5Button,die6Button, pauseButton,
                endTurnButton, rollDiceButton , submitDiceButton);
    }

    private void makeRollArea(){
        int xEdge = (int) baseCanvas.getWidth()/2-167;
        int yEdge = (int) baseCanvas.getHeight()/2-125;
        GraphicsContext graphicsContext = baseCanvas.getGraphicsContext2D();
        graphicsContext.setFill(Color.rgb(35,95,50));
        Image bowlImage = new Image("resources\\images\\rectangle bowl.png");
        graphicsContext.drawImage(bowlImage,xEdge,yEdge);
        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.strokeRect(xEdge,yEdge,335,250);
    }

    private void makeScoreBoard(){
        int scoreBoardTop = (int)baseCanvas.getHeight()/2-112;
        int scoreBoardLeft = 10;

        GraphicsContext graphicsContext = baseCanvas.getGraphicsContext2D();
        graphicsContext.setFill(Color.BLANCHEDALMOND);
        graphicsContext.fillRect(scoreBoardLeft,(scoreBoardTop),275,25*(players.size()+1));
        graphicsContext.strokeRect(scoreBoardLeft,(scoreBoardTop),275,25*(players.size()+1));
        graphicsContext.strokeLine(scoreBoardLeft+150, scoreBoardTop, scoreBoardLeft+150, scoreBoardTop+25*(players.size()+1));
        for(int i=0; i<players.size(); i++){
            graphicsContext.strokeRect(scoreBoardLeft,(scoreBoardTop)+i*25,275,25);
            scoreLabels[i].setText(String.valueOf(players.get(i).getPlayerScore()));
        }
    }
    //-------------end of methods to set up the gui----------------

    //-------------Methods to make information popups---------------
    private void doUltraConservativePlayerInfoPopup(){
        Alert infoPopup = new Alert(Alert.AlertType.INFORMATION);
        infoPopup.setWidth(800);
        togglePausedOrResumed();
        infoPopup.setHeaderText
                ("The Ultra-Conservative Player Bot always keeps every scoreable die. " +
                        "\nIt also never rerolls hands, even if it rolls hotdice.");
        infoPopup.showAndWait();
        togglePausedOrResumed();
    }

    private void doHowToPlayPopUp(){
        Alert howToPlayAlert = new Alert(Alert.AlertType.INFORMATION);
        howToPlayAlert.setWidth(800);
        togglePausedOrResumed();
        howToPlayAlert.setHeaderText("Farkle is played by two or more players, with each player in succession having a turn at throwing the dice." +
                "\n--Each player's turn results in a score, and the scores for each player accumulate to 10,000." +
                "\n--At the beginning of each turn, the player throws all the dice at once." +
                "\n--After each throw, one or more scoring dice must be set aside." +
                "\n--The player may then either end their turn and bank the score accumulated so far, or continue to throw the remaining dice." +
                "\n--If the player has scored all six dice, they have \"hot dice\" and may continue their turn with a new throw of all six dice," +
                "\nadding to the score they have already accumulated. There is no limit to the number of \"hot dice\" a player may roll in one turn." +
                "\n--If none of the dice score in any given throw, the player has \"farkled\" and all points for that turn are lost." +
                "\n--At the end of the player's turn, the dice are handed to the next player in succession, and they have their turn." +
                "\n--Once a player has achieved a winning point total, each other player has one last turn to score enough points to surpass that high-score." +
                "\n" +
                "\nUnless otherwise stated, dice are only counted toward a score if that roll of the dice had that result." +
                "\nScoring:" +
                "\n--3 Pairs, 6 of a kind, full straight, and two triples all give 2600 points." +
                "\n--1 triple -> 100 * the number shown on the dice. (e.g. 3 twos = 200)" +
                "\n---exception: 3 1s score 1000 points." +
                "\n--4 of a kind -> 2 * the result of 1 triple (e.g. 4 twos = 400)" +
                "\n--5 of a kind -> 3 * the result of 1 triple (e.g. 5 twos = 600)" +
                "\n--1s that are not part of another score ALWAYS score as 100" +
                "\n--5s that are not part of another score ALWAYS score as 50");
        howToPlayAlert.showAndWait();
        togglePausedOrResumed();
    }
    //-------------End of methods to make information popups---------------


    //--------------Methods to update the gui------------------------
    private void drawDie(Die die, int playerOrBowlNum, int dieNumOfTotal){
        GraphicsContext graphicsContext = animationCanvas.getGraphicsContext2D();
        Image diceImage = new Image("resources\\images\\dice.png");
        int drawAreaXEdge;
        int drawAreaYEdge;
        int distanceMultiplier;

        switch (playerOrBowlNum){
            case -1:
                drawAreaXEdge = (int) baseCanvas.getWidth()/2-117;
                drawAreaYEdge = (int) baseCanvas.getHeight()/2-75;
                distanceMultiplier = 85;
                break;
            case 0:
                drawAreaXEdge=15;
                drawAreaYEdge=(int) baseCanvas.getHeight()-150;
                distanceMultiplier = 70;
                break;
            case 1:
                drawAreaXEdge=240;
                drawAreaYEdge=(int) baseCanvas.getHeight()-150;
                distanceMultiplier = 70;
                break;
            case 2:
                drawAreaXEdge=465;
                drawAreaYEdge=(int) baseCanvas.getHeight()-150;
                distanceMultiplier = 70;
                break;
            case 3:
                drawAreaXEdge=690;
                drawAreaYEdge=(int) baseCanvas.getHeight()-150;
                distanceMultiplier = 70;
                break;
            case 4:
                drawAreaXEdge=15;
                drawAreaYEdge=40;
                distanceMultiplier = 70;
                break;
            case 5:
                drawAreaXEdge=240;
                drawAreaYEdge=40;
                distanceMultiplier = 70;
                break;
            case 6:
                drawAreaXEdge=465;
                drawAreaYEdge=40;
                distanceMultiplier = 70;
                break;
            case 7:
                drawAreaXEdge=690;
                drawAreaYEdge=40;
                distanceMultiplier = 70;
                break;
            default:
                return;
        }

        int dieX;
        int dieY;
        switch (dieNumOfTotal){
            case 0:
                if(playerOrBowlNum == -1){
                    graphicsContext.clearRect(drawAreaXEdge, drawAreaYEdge,335,250);
                }
                dieX = drawAreaXEdge;
                dieY = drawAreaYEdge;
                break;
            case 1:
                dieX = drawAreaXEdge;
                dieY = drawAreaYEdge+distanceMultiplier;
                break;
            case 2:
                dieX = drawAreaXEdge+distanceMultiplier;
                dieY = drawAreaYEdge;
                break;
            case 3:
                dieX = drawAreaXEdge+distanceMultiplier;
                dieY = drawAreaYEdge+distanceMultiplier;
                break;
            case 4:
                dieX = drawAreaXEdge+distanceMultiplier*2;
                dieY = drawAreaYEdge;
                break;
            case 5:
                dieX = drawAreaXEdge+distanceMultiplier*2;
                dieY = drawAreaYEdge+distanceMultiplier;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + dieNumOfTotal);
        }
        graphicsContext.drawImage(diceImage,(die.getDieNum()-1)*65,0, 65, 65, dieX,dieY,65,65);
    }

    private void drawKeptDice(DiceHand playerDiceHand, int playerLocation){
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
        graphicsContext.setStroke(Color.SKYBLUE);
        graphicsContext.setLineWidth(2);
        graphicsContext.strokeRect(xStartingCoord,yStartingCoord,215,145);

        for(int i = 0; i<playerDiceHand.getHandSize();i++){
            Die die = playerDiceHand.getDiceInHand().get(i);
            drawDie(die,playerLocation,i);
        }
        doNotify();
    }

    private void drawRolledDice(DiceHand rolledDice){
        final int BOWL_LOCATION_NUM = -1;
        for(int i = 0; i<rolledDice.getHandSize();i++){
            Die die = rolledDice.getDiceInHand().get(i);
            drawDie(die, BOWL_LOCATION_NUM, i);
        }
    }

    private void showFarkle(int farkledDiceNum){
        int drawAreaXEdge = (int) baseCanvas.getWidth()/2-117;
        int drawAreaYEdge = (int) baseCanvas.getHeight()/2-75;
        animationCanvas.getGraphicsContext2D().setStroke(Color.RED);
        animationCanvas.getGraphicsContext2D().setLineWidth(2);
        int distanceMultiplier = 85;
        int dieX;
        int dieY;
        for(int i = 0; i<farkledDiceNum; i++){
            switch (i){
                case 0:
                    dieX = drawAreaXEdge;
                    dieY = drawAreaYEdge;
                    break;
                case 1:
                    dieX = drawAreaXEdge;
                    dieY = drawAreaYEdge+distanceMultiplier;
                    break;
                case 2:
                    dieX = drawAreaXEdge+distanceMultiplier;
                    dieY = drawAreaYEdge;
                    break;
                case 3:
                    dieX = drawAreaXEdge+distanceMultiplier;
                    dieY = drawAreaYEdge+distanceMultiplier;
                    break;
                case 4:
                    dieX = drawAreaXEdge+distanceMultiplier*2;
                    dieY = drawAreaYEdge;
                    break;
                case 5:
                    dieX = drawAreaXEdge+distanceMultiplier*2;
                    dieY = drawAreaYEdge+distanceMultiplier;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + farkledDiceNum);
            }
            animationCanvas.getGraphicsContext2D().strokeRect(dieX, dieY, 65,65);
        }
        synchronized (lock){
            try {
                lock.wait(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        animationCanvas.getGraphicsContext2D().clearRect(0,0,animationCanvas.getWidth(),animationCanvas.getHeight());
    }

    private void doRollAnimation(DiceHand rolledDice) {
        final long startTime = System.nanoTime();
        AnimationTimer rollTimer = new AnimationTimer() {
            long previousTime;
            @Override
            public void handle(long currentTime) {
                if(currentTime-previousTime > 10){
                    for(int i = 0; i<rolledDice.getHandSize();i++){
                        Die die = new Die((int)(Math.random()*6)+1);
                        drawDie(die, -1, i);
                    }
                }
                if(currentTime-startTime>0.5e9){
                    this.stop();
                    drawRolledDice(rolledDice);
                    doNotify();
                }
                previousTime = currentTime;
            }
        };
        rollTimer.start();
    }

    private void updateTurnInfo(boolean shouldIndicateLastCall) throws InterruptedException {
        if(shouldIndicateLastCall){
            notified = false;
            Platform.runLater(() -> toggleLastCallLabel(true));
            doWait();
            synchronized (lock){
                try {
                    lock.wait(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            notified = false;
            Platform.runLater(() -> toggleLastCallLabel(false));
            doWait();
        }
        notified = false;
        Platform.runLater(() -> showTurnInfoLable(true));
        doWait();
        synchronized (lock){
            try {
                lock.wait(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        notified = false;
        Platform.runLater(() -> showTurnInfoLable(false));
        doWait();
    }

    private void doEndGuiUpdate(BasicPlayer winningPlayer){
        turnInfoLabel.setVisible(true);
        turnInfoLabel.setText("Congratulations, Player " + (players.indexOf(winningPlayer)+1) + "!");
    }

    private void doUpdateGui(DiceHand playerHand, int playerNum) throws InterruptedException {
        while (paused){
            Thread.onSpinWait();
        }

        notified = false;
        Platform.runLater(() -> drawKeptDice(playerHand, playerNum));
        Platform.runLater(HotDiceGui.this::updateScoreBoard);
        doWait();

    }

    private void doRunAnimation1(DiceHand rolledDice) throws InterruptedException {
        notified = false;
        Platform.runLater(()->doRollAnimation(rolledDice));
        doWait();
        synchronized (lock){
            try {
                lock.wait(1800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    //--------------End of methods to update the gui------------------------

    //----------------------Thread Control------------------
    private void doWait() throws InterruptedException{
        if(shouldTerminateGamePlayThread){
            shouldTerminateGamePlayThread = false;
            clearToRestart = true;
            throw new InterruptedException("Thread terminated in doWait");
        }
        if(!notified){
            synchronized (lock){
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doNotify(){
        notified = true;
        synchronized (lock){
            lock.notify();
        }
    }

    private void doCheckPaused(){
        while (paused){
            synchronized (lock){
                try {
                    lock.wait(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //----------------------End of Thread control------------

    //----------------------Gui toggles-----------------------
    private void toggleLastCallLabel(boolean shouldShowLastCall){
        if(shouldShowLastCall){
            turnInfoLabel.setText("Last call - one more round.");
            turnInfoLabel.setVisible(true);
        }else{
            turnInfoLabel.setVisible(false);
        }
        doNotify();
    }

    private void showTurnInfoLable(boolean shouldShow){
        if(shouldShow){
            turnInfoLabel.setText("Player " + game.currentPlayerNum + "'s turn.");
            turnInfoLabel.setVisible(true);
        }else{
            turnInfoLabel.setVisible(false);
        }
        doNotify();
    }

    private void updateScoreBoard(){
        if(!shouldTerminateGamePlayThread){
            for(int i=0; i<players.size(); i++){
                scoreLabels[i].setText(String.valueOf(players.get(i).getPlayerScore()));
            }
        }
    }

    private void togglePausedOrResumed(){
        if(paused){
            pauseButton.setText("Pause");
        }else{
            pauseButton.setText("Resume");
        }
        paused = !paused;
    }

    private void enableButtons(MenuButton[] playerTypes, int numOfPlayers){
        for (int i = 0; i<8; i++){
            playerTypes[i].setDisable(!(i < numOfPlayers));
        }
    }
    //-------------------end of gui toggles--------------------

    //-------------------nested player classes--------------------

    private class ConservativeGuiPlayer extends ConservativePlayer {
        @Override
        public void checkPaused(){
            doCheckPaused();
        }
        @Override
        public void updateGui() throws InterruptedException {
            doUpdateGui(this.getPlayerHand(), players.indexOf(this));
        }
        @Override
        public void updateGui(int rolledDiceNum, DiceHand keptDice, boolean isFarkle) throws InterruptedException {
            if(isFarkle){
                showFarkle(rolledDiceNum);
            }else{
                animationCanvas.getGraphicsContext2D().clearRect(0,0,animationCanvas.getWidth(),animationCanvas.getHeight());
                for(Button diceButton : diceButtons){
                    diceButton.setStyle("-fx-border-color: transparent; -fx-border-width: 0; -fx-background-radius: 0; -fx-background-color: transparent;");
                }
                doUpdateGui(keptDice, game.currentPlayerNum-1);
            }
        }
        @Override
        public void runAnimation1(DiceHand rolledDice) throws InterruptedException {
            doRunAnimation1(rolledDice);
        }
    }//End of class ConservativeGuiPlayer

    private class UltraConservativeGuiPlayer extends UltraConservativePlayer {
        @Override
        public void checkPaused(){
            doCheckPaused();
        }
        @Override
        public void updateGui() throws InterruptedException {
            doUpdateGui(this.getPlayerHand(), players.indexOf(this));
        }
        @Override
        public void updateGui(int rolledDiceNum, DiceHand keptDice, boolean isFarkle) throws InterruptedException {
            if(isFarkle){
                showFarkle(rolledDiceNum);
            }else{
                animationCanvas.getGraphicsContext2D().clearRect(0,0,animationCanvas.getWidth(),animationCanvas.getHeight());
                for(Button diceButton : diceButtons){
                    diceButton.setStyle("-fx-border-color: transparent; -fx-border-width: 0; -fx-background-radius: 0; -fx-background-color: transparent;");
                }
                doUpdateGui(keptDice, game.currentPlayerNum-1);
            }
        }
        @Override
        public void runAnimation1(DiceHand rolledDice) throws InterruptedException {
            doRunAnimation1(rolledDice);
        }
    }//End of class UltraConservativeGuiPlayer

    /**
     * HumanGuiPlayer extends HumanPlayer and implements a number of methods for thread control and GUI manipulation.
     */
    private class HumanGuiPlayer extends HumanPlayer{
        private final ArrayList<Die> selectedDice = new ArrayList<>();//holds a list of dice that were selected by the user since the last dice roll.
        boolean playerChoseToEnd = false; //true if user clicked endTurn button since last roll

        //------button listeners-------------
        public void onDiceSelected(Button diceButton,Die die){
            if(selectedDice.contains(die)){
                selectedDice.remove(die);
                diceButton.setStyle("-fx-border-color: transparent; -fx-border-width: 0; -fx-background-radius: 0; -fx-background-color: transparent;");
            }else{
                selectedDice.add(die);
                diceButton.setStyle("-fx-border-color: skyblue; -fx-border-width: 2; -fx-background-radius: 0; -fx-background-color: rgba(135,206,250,.2);");
            }
        }

        private void onRollDiceButtonClicked(){
            doNotify();
            submitDiceButton.setDisable(false);
            rollDiceButton.setDisable(true);
            playerChoseToEnd = false;
        }

        private void onSubmitDiceButtonClicked(){
            if(selectedDice.size()<1){
                doMinDiceAlert();
                return;
            }
            rollDiceButton.setDisable(false);
            submitDiceButton.setDisable(true);
            endTurnButton.setDisable(false);
            doNotify();
        }

        private void onPlayerTurnButtonClicked(){
            endTurnButton.setDisable(true);
            rollDiceButton.setDisable(true);
            if(selectedDice.size()<1){
                doMinDiceAlert();
                return;
            }
            playerChoseToEnd = true;
            doNotify();
        }

        //------methods for human player specific gui updates
        private void doMinDiceAlert(){
            Alert notEnoughDiceAlert = new Alert(Alert.AlertType.WARNING);
            notEnoughDiceAlert.setContentText("You must select at least one die to continue!");
            notEnoughDiceAlert.show();
        }

        private void doGuiTurnPrep(){
            submitDiceButton.setOnAction(e -> onSubmitDiceButtonClicked());
            rollDiceButton.setOnAction(e -> onRollDiceButtonClicked());
            endTurnButton.setOnAction(e -> onPlayerTurnButtonClicked());
            endTurnButton.setDisable(true);
            submitDiceButton.setDisable(true);
            rollDiceButton.setDisable(false);
            revealDiceButtons(true);
            for(Button diceButton : diceButtons){
                diceButton.setDisable(false);
                diceButton.setStyle("-fx-border-color: transparent; -fx-border-width: 0; -fx-background-radius: 0; -fx-background-color: transparent;");
            }
            doNotify();
        }

        public void revealDiceButtons(boolean shouldReveal){
            if(shouldReveal){
                for(Button diceButton : diceButtons) {
                    diceButton.setVisible(true);
                }
                rollDiceButton.setVisible(true);
                submitDiceButton.setVisible(true);
                endTurnButton.setVisible(true);
            }
            else{
                for(Button diceButton : diceButtons) {
                    diceButton.setVisible(false);
                }
                rollDiceButton.setVisible(false);
                submitDiceButton.setVisible(false);
                endTurnButton.setVisible(false);
            }
        }

        private void doGuiRollPrep(DiceHand rolledDice){
            selectedDice.clear();
            for(Button diceButton : diceButtons){
                diceButton.setDisable(false);
                diceButton.setStyle("-fx-border-color: transparent; -fx-border-width: 0; -fx-background-radius: 0; -fx-background-color: transparent;");
            }
            diceButtons[0].setOnAction(e->onDiceSelected(diceButtons[0], rolledDice.getDiceInHand().get(0)));
            diceButtons[1].setOnAction(e->onDiceSelected(diceButtons[1], rolledDice.getDiceInHand().get(1)));
            diceButtons[2].setOnAction(e->onDiceSelected(diceButtons[2], rolledDice.getDiceInHand().get(2)));
            diceButtons[3].setOnAction(e->onDiceSelected(diceButtons[3], rolledDice.getDiceInHand().get(3)));
            diceButtons[4].setOnAction(e->onDiceSelected(diceButtons[4], rolledDice.getDiceInHand().get(4)));
            diceButtons[5].setOnAction(e->onDiceSelected(diceButtons[5], rolledDice.getDiceInHand().get(5)));
            doNotify();
        }

        //-----Thread control---------
        @Override
        public void waitForUser() throws InterruptedException {
            notified = false;
            doWait();
        }
        @Override
        public void checkPaused(){
            doCheckPaused();
        }
        //-----player specific animation implementations
        @Override
        public void runAnimation1(DiceHand rolledDice) throws InterruptedException {
            for (int i = 0; i< diceButtons.length; i++){
                diceButtons[i].setDisable(i >= rolledDice.getHandSize());
            }
            doRunAnimation1(rolledDice);
        }
        @Override
        public void prepGuiForUserDiceRoll(DiceHand rolledDice) throws InterruptedException {
            notified = false;
            Platform.runLater(() -> doGuiRollPrep(rolledDice));
            doWait();
        }
        @Override
        public void updateGui(int rolledDiceNum, DiceHand keptDice, boolean isFarkle) throws InterruptedException {

            if(isFarkle){
                showFarkle(rolledDiceNum);
            }else{
                animationCanvas.getGraphicsContext2D().clearRect(0,0,animationCanvas.getWidth(),animationCanvas.getHeight());
                for(Button diceButton : diceButtons){
                    diceButton.setStyle("-fx-border-color: transparent; -fx-border-width: 0; -fx-background-radius: 0; -fx-background-color: transparent;");
                }
                doUpdateGui(keptDice, game.currentPlayerNum-1);
            }
        }
        @Override
        public void prepGuiForUserTurn() throws InterruptedException {
            notified = false;
            Platform.runLater(this::doGuiTurnPrep);
            doWait();
        }
        @Override
        public DiceHand chooseDiceToKeep(DiceHand rolledDice, boolean initialRoll) throws InterruptedException {
            rolledDice.scoreInitialRoll();
            if(rolledDice.getHandScore()==0){
                return new DiceHand();
            }
            notified = false;
            doWait();
            DiceHand diceToReturn = new DiceHand(selectedDice);
            diceToReturn.scoreInitialRoll();
            diceToReturn = new DiceHand(diceToReturn.getScoringDice());
            return diceToReturn;
        }
        @Override
        public boolean isEndTurnConditionMet(DiceHand rolledDice) {
            if(rolledDice.isFarkle()){
                return true;
            }
            selectedDice.clear();
            return playerChoseToEnd;
        }
        @Override
        public String getPlayerString(){
            return "Human";
        }
    }//end of class HumanGuiPlayer
    //----------------------End of Section-------------------------

    //------------------other nested classes----------------------
    private class GamePlayThread extends Thread{
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()){
                try{
                    BasicPlayer winningPlayer = game.playGame();
                    notified = false;
                    Platform.runLater(() -> doEndGuiUpdate(winningPlayer));
                    doWait();
                }catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                }
            }
        }
    }//end of class GamePlayThread

    public class HotDiceGuiGame extends HotDiceGame{
        @Override
        public void updateGameBoard(boolean isLastCall) throws InterruptedException {
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
            updateTurnInfo(isLastCall);
        }

        @Override
        public BasicPlayer getPlayerTypeFromString(String playerType){
            BasicPlayer playerToAdd = null;
            switch (playerType){
                case "None":
                    break;
                case "Human":
                    playerToAdd = new HumanGuiPlayer();
                    break;
                case "Ultra Conservative Bot":
                    playerToAdd = new UltraConservativeGuiPlayer();
                    break;
                case "Conservative Bot":
                    playerToAdd = new ConservativeGuiPlayer();
                    break;
                default:
                    throw new IllegalArgumentException(playerType + " was not able to be added." +
                            "this is due to " + playerType + " not being defined in the getPlayerTypeFromString method." );
            }
            return playerToAdd;
        }

        private void selectSaveFileAndSave(){
            FileChooser fileDialog = new FileChooser();
            fileDialog.setInitialDirectory(
                    new File( System.getProperty("user.home") ) );
            fileDialog.setTitle("Select File to Save.");
            File selectedFile = fileDialog.showSaveDialog(gameStage);

            try {
                saveGame(selectedFile);
            } catch (Exception e) {
                Alert confirm = new Alert( Alert.AlertType.CONFIRMATION,
                        "Invalid save location. Click OK to try again, or cancel to exit without saving.");
                confirm.setHeaderText(null);
                Optional<ButtonType> alertResponse = confirm.showAndWait();
                if ( alertResponse.isPresent() && alertResponse.get() == ButtonType.OK ) {
                    saveGame(selectedFile);
                }
            }
        }

        private void loadGameFromFile(){
            players.clear();
            FileChooser fileDialog = new FileChooser();
            fileDialog.setInitialDirectory(
                    new File( System.getProperty("user.home") ) );
            File selectedFile = fileDialog.showOpenDialog(gameStage);
            try{
                game.loadGame(selectedFile);
            }catch(Exception e){
                Alert couldNotLoad = new Alert(Alert.AlertType.CONFIRMATION, "Couldn't load from that file. Click OK to choose a different file, " +
                        "\nor click cancel to start a new file.");
                couldNotLoad.setHeaderText(null);
                Optional<ButtonType> alertResponse = couldNotLoad.showAndWait();
                if ( alertResponse.isPresent() && alertResponse.get() == ButtonType.OK ) {
                    game.loadGame(selectedFile);
                }else {
                    return;
                }
            }

            setGameStartingVariables();
            makeInitialBoard(players.size());
            gameStage.show();

            GamePlayThread gamePlayThread = new GamePlayThread();
            gamePlayThread.setDaemon(true);
            gamePlayThread.start();
        }

    }//end of class HotDiceGuiGame

    private class StartMenuPopUp {
        Pane startRoot;

        private void setUp(){
            startRoot = new Pane();
            Scene scene = new Scene(startRoot, 350, 310);
            Stage stage = new Stage();
            stage.setScene(scene);
            startRoot.setStyle("-fx-background-color: rgb(140, 100, 80)");
            makeButtons(stage);
            makeLabels();

            stage.setResizable(false);
            stage.show();
        }
        private void makeLabels(){
            Label playerNumLabel = new Label("Choose how many players to Include.");
            playerNumLabel.setWrapText(true);
            playerNumLabel.setPrefWidth(150);
            playerNumLabel.setFont(Font.font(null, FontWeight.BOLD,15));
            playerNumLabel.setTextFill(Color.BLACK);
            playerNumLabel.relocate(startRoot.getWidth()-160,10);
            startRoot.getChildren().add(playerNumLabel);

            Label playerChoiceLabel = new Label("Choose players to include in the game.");
            playerChoiceLabel.setWrapText(true);
            playerChoiceLabel.setPrefWidth(150);
            playerChoiceLabel.setFont(Font.font(null, FontWeight.BOLD,15));
            playerChoiceLabel.setTextFill(Color.BLACK);
            playerChoiceLabel.relocate(10,10);
            startRoot.getChildren().add(playerChoiceLabel);
        }
        private void makeButtons(Stage stage){
            String[]choiceStrings = new String[]{"Human", "Ultra Conservative Bot", "Conservative Bot"};

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
            startGameButton.setOnAction(e-> {
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
        }

        private void onStartButtonClicked(MenuButton[] playerTypes){
            ArrayList<BasicPlayer> newPlayerList = new ArrayList<>();
            String buttonText;
            BasicPlayer playerToAdd = null;
            for(MenuButton menuButton : playerTypes){
                if(!menuButton.isDisabled()){
                    buttonText = menuButton.getText();
                }
                else{
                    continue;
                }
                playerToAdd = game.getPlayerTypeFromString(buttonText);
                if(playerToAdd!=null){
                    newPlayerList.add(playerToAdd);
                }
            }
            players.clear();
            players.addAll(newPlayerList);

            makeInitialBoard(players.size());
            gameStage.show();

            GamePlayThread gamePlayThread = new GamePlayThread();
            gamePlayThread.setDaemon(true);
            gamePlayThread.start();
        }
    }//end of class StartMenuPopUp
    //-----------------------End of other nested classes--------------------------

    public static void main(String[]args){
        launch(args);
    }

}
