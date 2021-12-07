# hot-dice-revisited
This program is original work that I have written from scratch.

Hot Dice, also Known as Farkle, is a folk dice game. Players roll hands of dice, competing to be the first person to reach 10,000 points. 
Points are accumulated by rolling scoring hands, which are hands that contain 1s, 5s, sets of 3, sets of 4, sets of 5, sets of 6, straights, two sets of 3 of a kind, or 3 pair.

This program allows users to play the game by competing against a variety of bots, competing against other players via pass-n-play, or through a combination.

Currently the game has the following features:
* gui with listeners for user interaction
* multithreaded to manage the gui and game logic
* 3 bot types with unique functionality
* complete gameplay functionality
* unit testing
* ability to pause, resume, or restart the game
* ability to save a game and load previously saved games
* game information available through popups

### NOTE: This is a javafx program. It requires the javafx library as a dependency. 

Javafx setup instructions: 

* Download javafx from: https://gluonhq.com/products/javafx/ (I used javafx 12).
* Save it to a location of your choice.
* Unpack the zip folder.
* Open the project with your IDE of choice (I use intellij IDEA).
* Add the javafx/lib folder as an external library for the project.
* For intellij, this means going to "project structure" -> "libraries" -> "add library" ->{javafx location}/lib
* Add the following as a VM argument for the project: --module-path "{full path to your javafx/lib folder}" --add-modules javafx.controls,javafx.fxml,javafx.base,javafx.graphics,javafx.media,javafx.swing,javafx.web
* Build and run the project as normal.
