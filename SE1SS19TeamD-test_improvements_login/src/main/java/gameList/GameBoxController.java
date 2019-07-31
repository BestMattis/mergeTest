package gameList;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import main.AdvancedWarsApplication;
import model.Game;
import model.GameField;

public class GameBoxController {

    @FXML
    private Label gameName;

    @FXML
    private Label playerCounter;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private HBox gameCard;

    private Game game;

    private int playerCount;
    private int maxPlayers;

    /**
     * Adds EventHandler to handle MouseEvents.
     */
    @FXML
    public void initialize() {
        gameCard.addEventHandler(MouseEvent.ANY, event -> {
            if (game.getCapacity() > game.getJoinedPlayers()) {
                /*game is not full*/
                if (event.getEventType().equals(MouseEvent.MOUSE_ENTERED)) {
                    /*Mouse entered -> setBorder*/
                    gameCard.setStyle("-fx-background-color: #38353a");

                } else if (event.getEventType().equals(MouseEvent.MOUSE_EXITED)) {
                    /*Mouse exited -> resetBorder*/
                    gameCard.setStyle("-fx-background-color: #2f2c31");
                } else if (event.getEventType().equals(MouseEvent.MOUSE_CLICKED) || event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
                    /*Clicked/Pressed -> Show GameLobby*/
                    nameClicked();
                }
            }
        });
    }

    /**
     * Sets gameName, playerCount, maxPlayers, progressBar.
     */
    public void update() {
        gameName.setText(game.getName());
        playerCount = game.getJoinedPlayers();
        maxPlayers = game.getCapacity();
        playerCounter.setText(playerCount + "/" + maxPlayers);
        progressBar.setProgress((double) playerCount / maxPlayers);
        if (game.getJoinedPlayers() == game.getCapacity()) {
            /*Game is full -> set Disable true*/
            gameCard.setDisable(true);
            gameCard.setStyle("-fx-background-color: #972805");
        } else {
            /*Game is open -> set Disable false*/
            gameCard.setDisable(false);
            gameCard.setStyle("-fx-background-color: #2f2c31");
        }
    }

    /**
     * Adds Listener on GameListController.scrollPane
     * so that it fills the ScrollPane.
     *
     * @param scrollPane ScrollPane to set Listener on.
     */
    public void addWidthListener(ScrollPane scrollPane) {
        gameCard.setMinWidth(scrollPane.getWidth());
        scrollPane.widthProperty().addListener((observable, oldValue, newValue) -> gameCard.setMinWidth(newValue.doubleValue()));
    }

    /**
     * Sets Game for GameBox.
     * Adds Listener on Game.
     *
     * @param game game to set Listener on
     */
    public void setGame(Game game) {
        this.game = game;
        game.addPropertyChangeListener(evt -> Platform.runLater(() -> update()));
        update();
    }

    /**
     * call the methodes to show the gameLobby and join the game
     */
    public void nameClicked() {
        game.setGameField(new GameField());
        AdvancedWarsApplication.getInstance().goToGame(game);
        AdvancedWarsApplication.getInstance().getGameScreenCon().getGameLobbyController().update(game);
        AdvancedWarsApplication.getInstance().getGameScreenCon().getGameLobbyController().show();
        if (!AdvancedWarsApplication.getInstance().offtesting) {
            AdvancedWarsApplication.getInstance().getGameScreenCon().getGameLobbyController().joinGameLobby(game);
        }
    }
}
