package gameList;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.App;
import model.Game;
import model.Model;
import model.Player;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class GameListTest extends ApplicationTest {

    private GameListController gameListController;

    @Override
    public void start(Stage stage) throws Exception {

        App app = new App();

        ArrayList<Game> games = new ArrayList<>();
        ArrayList<Player> players = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            games.add(new Game().setName("Game" + (i + 1)).setCapacity(4).setApp(app));
        }
        for (int i = 0; i < 40; i++) {
            players.add(new Player().setName("Player" + 1).setApp(app));
        }
        int index = 0;
        for (Game game : games) {
            for (int i = index; i < index + 2; i++) {
                game.withPlayers(players.get(i));
            }
            index = index + 2;
        }

        Model.setApp(app);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classLoader.getResource("en-US.properties").openStream();
        ResourceBundle bundle = new PropertyResourceBundle(inputStream);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("gameList/gameList.fxml"), bundle);

        Parent mainNode = fxmlLoader.load();
        gameListController = fxmlLoader.getController();

        stage.setScene(new Scene(mainNode));
        stage.show();
        stage.toFront();
    }

    @SuppressWarnings("unchecked")
    public <T extends Node> T find(final String query) {
        return (T) lookup(query).queryAll().iterator().next();
    }

    @Test
    public void numberOfGames() {
        Assert.assertEquals(Integer.parseInt(gameListController.getNumberOfGames().getText()), Model.getApp().getAllGames().size());
    }

    @Test
    public void numberOfOpenGames() {
        int numberOfOpenGames = 0;
        for (Game game : Model.getApp().getAllGames()) {
            if (game.getPlayers().size() < game.getCapacity()) {
                numberOfOpenGames++;
            }
        }
        Assert.assertEquals(Integer.parseInt(gameListController.getNumberOfOpenGames().getText()), numberOfOpenGames);
    }

    @Test
    public void propertyChange() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Model.getApp().withAllGames(new Game().setCapacity(4).setName("NewGame"));
                Model.getApp().getAllGames().get(0).withPlayers(new Player());
            }
        });
        numberOfGames();
        numberOfOpenGames();
    }


}
