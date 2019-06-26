package gameList;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.App;
import model.Game;
import model.Model;

import java.io.IOException;

public class GameListController {

    @FXML
    private VBox list;
    @FXML
    private Label numberOfGames;
    @FXML
    private Label numberOfOpenGames;
    @FXML
    private ScrollPane scrollPane;

    private App app;

    /**
     * Call setApp with the app from Model.
     */
    @FXML
    public void initialize() {
        setApp(Model.getInstance().getApp());
    }

    /**
     * Sets Listener on given app.
     * Calls update().
     *
     * @param app to set Lister on
     */
    public void setApp(App app) {
        this.app = app;
        app.addPropertyChangeListener(App.PROPERTY_allGames, evt -> update());
        update();
    }

    /**
     * Clears list and loads GameBox.fxml for each Game
     * and adds them to list.
     * Sets Listener on each game.
     * Sets numberOfGames and numberOfOpenGames.
     */
    public void update() {
        Platform.runLater(() ->
        {
            list.getChildren().clear();
            numberOfGames.setText("" + app.getAllGames().size());
            int openGameCounter = 0;
            for (Game game : app.getAllGames()) {
                if (game.getPlayers().size() < game.getCapacity()) {
                    /*this game is open*/
                    openGameCounter++;
                }
                FXMLLoader fxmlLoader = new FXMLLoader(
                        getClass().getClassLoader().getResource("gameList/GameBox.fxml"));
                try {
                    Parent parent = fxmlLoader.load();
                    GameBoxController gameBoxController = fxmlLoader.getController();
                    gameBoxController.setGame(game);
                    game.addPropertyChangeListener(Game.PROPERTY_players, evt -> {
                        if (game.getPlayers().size() == game.getCapacity()) {
                            /*game is full*/
                            gameBoxController.setGame(game);
                            numberOfOpenGames.setText("" + (Integer.parseInt(numberOfOpenGames.getText()) - 1));
                            /*there is now one less open game*/
                        }
                    });
                    list.getChildren().addAll(parent);
                    gameBoxController.addWidthListener(scrollPane);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            numberOfOpenGames.setText("" + openGameCounter);
        });
    }

    /**
     * For Tests.
     *
     * @return numberOfOpenGames
     */
    public Label getNumberOfOpenGames() {
        return numberOfOpenGames;
    }

    /**
     * For Tests.
     *
     * @return numberOfGames
     */
    public Label getNumberOfGames() {
        return numberOfGames;
    }
}
