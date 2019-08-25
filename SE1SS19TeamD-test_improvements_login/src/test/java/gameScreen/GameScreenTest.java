package gameScreen;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import main.AdvancedWarsApplication;
import main.FXMLLoad;
import model.App;
import model.Game;
import model.Model;
import model.Player;
import org.junit.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit.ApplicationTest;

import static org.hamcrest.CoreMatchers.is;

public class GameScreenTest extends ApplicationTest {

    FXMLLoad gameFXML;
    Game testgame;
    Stage pri;
    private Model model;

    public void start(Stage stage) {

        AdvancedWarsApplication.offtesting = true;
        AdvancedWarsApplication awa = new AdvancedWarsApplication();
        awa.start(stage);
        model = awa.model;
        App app = new App();
        model.setApp(app);
        AdvancedWarsApplication.getInstance().offtesting = true;
        pri = stage;
        testgame = new Game();
        testgame.setCapacity(2);
        testgame.setName("testgame0");
        Player player = new Player();
        player.setName("Player0");
        testgame.withPlayers(player);
        app.setCurrentPlayer(player);
        model.getApp().withAllGames(testgame);

        Game game = new Game();

        gameFXML = new FXMLLoad("/gameScreen/gameScreen.fxml", new GameScreenController(game));
        stage.setScene(gameFXML.getScene());
        stage.setFullScreen(true);
        stage.show();

        gameFXML.getController(GameScreenController.class).getGameLobbyController().update(testgame);
    }

    @Test
    public void gameFullTest() {
        FxAssert.verifyThat(gameFXML.getController(GameScreenController.class).getGameLobbyController().base.isVisible(), is(true));
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Player player1 = new Player();
                player1.setName("Player1");
                testgame.withPlayers(player1);
            }
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        FxAssert.verifyThat(gameFXML.getController(GameScreenController.class).getGameLobbyController().base.isVisible(), is(false));

    }

    @Test
    public void chatTest() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                gameFXML.getController(GameScreenController.class).getGameLobbyController().hide();
            }
        });
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        FxAssert.verifyThat(gameFXML.getController(GameScreenController.class).chatFXML.getParent().isVisible(), is(false));
        moveTo("#border");
        clickOn("#chat");
        FxAssert.verifyThat(gameFXML.getController(GameScreenController.class).chatFXML.getParent().isVisible(), is(true));
        clickOn("#message");
        push(KeyCode.ENTER);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        FxAssert.verifyThat(gameFXML.getController(GameScreenController.class).chatFXML.getParent().isVisible(), is(true));

        clickOn("#message");
        push(KeyCode.ENTER);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        FxAssert.verifyThat(gameFXML.getController(GameScreenController.class).chatFXML.getParent().isVisible(), is(false));
        moveTo("#border");
        clickOn("#chat");
        moveTo("#message");
        FxAssert.verifyThat(gameFXML.getController(GameScreenController.class).chatFXML.getParent().isVisible(), is(true));
        moveTo("#border");
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        FxAssert.verifyThat(gameFXML.getController(GameScreenController.class).chatFXML.getParent().isVisible(), is(false));
    }
}
