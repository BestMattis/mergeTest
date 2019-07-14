package gameScreen;

import com.sun.javafx.css.StyleCache;
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
import syncCommunication.RESTExceptions.GameIdNotFoundException;

import javax.swing.tree.TreeCellEditor;

import static org.hamcrest.CoreMatchers.is;

public class GameScreenTest extends ApplicationTest {

    FXMLLoad gameFXML;
    Game testgame;
    Stage pri;

    public void start(Stage stage){

        App app = new App();
        Model.setApp(app);
        new AdvancedWarsApplication().start(stage);
        AdvancedWarsApplication.getInstance().offtesting = true;
        pri = stage;

        Game game = new Game();

        gameFXML = new FXMLLoad("/gameScreen/gameScreen.fxml", new GameScreenController(game));
        stage.setScene(gameFXML.getScene());
        stage.setFullScreen(true);
        stage.show();
        testgame = new Game();
        testgame.setCapacity(2);
        testgame.setName("testgame0");
        Player player = new Player();
        player.setName("Player0");
        testgame.withPlayers(player);
        gameFXML.getController(GameScreenController.class).getGameLobbyController().update(testgame);
    }

    @Test
    public void gameFullTest(){
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
    public void chatTest(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                gameFXML.getController(GameScreenController.class).getGameLobbyController().hide();
            }
        });
        try {
            Thread.sleep(1000);
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
