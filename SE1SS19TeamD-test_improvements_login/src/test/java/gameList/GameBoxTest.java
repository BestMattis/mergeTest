package gameList;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.App;
import model.Game;
import model.Model;
import model.Player;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

public class GameBoxTest extends ApplicationTest {

    private GameBoxController gameBoxController;

    @Override
    public void start(Stage stage) throws Exception {

        App app = new App().withAllGames(new Game().setName("TestGame").setCapacity(4).withPlayers(new Player()));

        Model.setApp(app);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("gameList/GameBox.fxml"));

        Parent mainNode = fxmlLoader.load();
        VBox vBox = new VBox(mainNode);
        gameBoxController = fxmlLoader.getController();
        gameBoxController.setGame(Model.getApp().getAllGames().get(0));

        stage.setScene(new Scene(vBox));
        stage.show();
        stage.toFront();
    }

    @SuppressWarnings("unchecked")
    public <T extends Node> T find(final String query) {
        return (T) lookup(query).queryAll().iterator().next();
    }

    @Test
    public void gameToGameCard() {
        Assert.assertEquals(gameBoxController.getGame(), Model.getApp().getAllGames().get(0));
    }

    @Test
    public void gameName() {
        Assert.assertEquals(gameBoxController.getGameName().getText(), Model.getApp().getAllGames().get(0).getName());
    }

    @Test
    public void gameCapacity() {
        Assert.assertEquals(Integer.parseInt(gameBoxController.getPlayerCounter().getText().split("/")[1]), Model.getApp().getAllGames().get(0).getCapacity());
    }

    @Test
    public void gamePlayerCount() {
        Assert.assertEquals(Integer.parseInt(gameBoxController.getPlayerCounter().getText().split("/")[0]), Model.getApp().getAllGames().get(0).getPlayers().size());
    }

    @Test
    public void propertyChange() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Model.getApp().getAllGames().get(0).withPlayers(new Player());
            }
        });
        gamePlayerCount();
    }

}
