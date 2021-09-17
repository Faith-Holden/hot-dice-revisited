package main.com;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import main.players.BasicPlayer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class HotDiceGame {
    private ArrayList<BasicPlayer> players = new ArrayList<>();
    public  int currentPlayerNum = 0;
    public HotDiceGame(){

    }

    public void updateGameBoard(boolean isLastCall) throws InterruptedException {}

    public BasicPlayer playGame() throws InterruptedException {
        boolean inProgress = true;
        int lastCallPlayer = -1;
        BasicPlayer winningPlayer = null;
        int winningScore = 0;
        boolean isLastCall = false;
        while (inProgress){
            for(BasicPlayer player : players){
                if(players.indexOf(player)==lastCallPlayer){
                    inProgress = false;
                    break;

                }else{
                    updateGameBoard(isLastCall);
                    player.playTurn();
                    if(player.getPlayerScore()>10000){
                        if(lastCallPlayer==-1){
                            lastCallPlayer = players.indexOf(player);
                            isLastCall = true;
                        }
                        if(player.getPlayerScore()>winningScore){
                            winningScore = player.getPlayerScore();
                            winningPlayer = player;
                        }
                    }
                }
            }
        }
        return winningPlayer;
    }

    public void loadGame(File saveFile){
        String currentPlayer = "";
        String playerNumString = "";
        String playerScore = "";
        String playerType = "";
        Document xmldoc;
        Element phoneDirectory;

        try{
            DocumentBuilder docReader = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            xmldoc = docReader.parse(saveFile);
            phoneDirectory = xmldoc.getDocumentElement();
        }catch (Exception e){
            e.printStackTrace();
            return;
        }

        NodeList entries = phoneDirectory.getElementsByTagName("player");
        for(int i=0; i<entries.getLength(); i++) {
            if (entries.item(i) instanceof Element) {
                Element entry = (Element) entries.item(i);
                if(entry.getTagName().equals("player")) {
                    playerNumString = entry.getAttribute("player_num");
                    playerScore = entry.getAttribute("player_score");
                    playerType = entry.getAttribute("player_type");
                }
            }
        }

        entries = phoneDirectory.getElementsByTagName("current_player");
        for(int i=0; i<entries.getLength(); i++) {
            if (entries.item(i) instanceof Element) {
                Element entry = (Element) entries.item(i);
                if(entry.getTagName().equals("current_player")) {
                    currentPlayer = entry.getAttribute("num");
                }
            }
        }
        BasicPlayer player = getPlayerTypeFromString(playerType);
        int playerNum = Integer.parseInt(playerNumString);
        currentPlayerNum = Integer.parseInt(currentPlayer);
        player.setPlayerScore(Integer.parseInt(playerScore));
        players.add(playerNum, player);
    }

    public boolean saveGame(File saveFile){
        try{
            PrintWriter out = new PrintWriter(new FileWriter(saveFile));
            out.println("<?xml version=\"1.0\"?>");
            out.println("<hot_dice_game version=\"1.0\">");
            for(BasicPlayer player : players){
                out.println("       <player player_num ='" + players.indexOf(player) + "' player_score = '" + player.getPlayerScore() + "' player_type = '" + player.getPlayerType() +  "'/>");
            }
            out.println("       <current_player num = '" + currentPlayerNum + "'/>");
            out.println("</hot_dice_game>");
            out.flush();
            out.close();
            return true;
        }catch (IOException e){
            return false;
        }
    }

    public BasicPlayer getPlayerTypeFromString(String playerTypeString){
        return null;
    }

    //--------------Getters and setters---------------------
    public ArrayList<BasicPlayer> getPlayers() {
        return players;
    }
    public void setPlayers(ArrayList<BasicPlayer> players) {
        this.players = players;
    }
    //------------------------------------------------------
}
