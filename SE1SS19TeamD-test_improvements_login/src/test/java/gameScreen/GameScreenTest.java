package gameScreen;

import javafx.application.Platform;
import javafx.stage.Stage;
import main.FXMLLoad;
import model.Game;
import model.Player;
import org.junit.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit.ApplicationTest;
import syncCommunication.RESTExceptions.GameIdNotFoundException;

import static org.hamcrest.CoreMatchers.is;

public class GameScreenTest extends ApplicationTest {

    FXMLLoad gameFXML;
    Game testgame;

    public void start(Stage stage){
        gameFXML = new FXMLLoad("/gameScreen/gameScreen.fxml");
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

        FxAssert.verifyThat(gameFXML.getController(GameScreenController.class).chatFXML.getParent().isVisible(), is(false));
        moveTo("#border");
        clickOn("#chat");
        FxAssert.verifyThat(gameFXML.getController(GameScreenController.class).chatFXML.getParent().isVisible(), is(true));

    }

}
