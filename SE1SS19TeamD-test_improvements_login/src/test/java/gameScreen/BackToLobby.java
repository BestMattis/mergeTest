package gameScreen;

import javafx.application.Platform;
import javafx.stage.Stage;
import main.AdvancedWarsApplication;
import main.FXMLLoad;
import model.Game;
import model.Player;
import msgToAllPlayers.WSChatEndpoint;
import org.junit.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit.ApplicationTest;
import syncCommunication.RESTExceptions.GameIdNotFoundException;

import static org.hamcrest.CoreMatchers.is;

public class BackToLobby extends ApplicationTest {

    FXMLLoad gameFXML;
    Stage stage1;

    public void start(Stage stage){
        stage1 = stage;
        AdvancedWarsApplication awa = new AdvancedWarsApplication();
        AdvancedWarsApplication.advancedWarsApplication = awa;
        awa.offtesting = true;
        //awa.start(stage);
        //stage.hide();
        awa.primaryStage = stage;
        awa.goToLobby();
        gameFXML = new FXMLLoad("/gameScreen/gameScreen.fxml");
        stage.setScene(gameFXML.getScene());
        stage.setFullScreen(true);

        gameFXML.getController(GameScreenController.class).getGameLobbyController().hide();
        stage.show();
    }

    @Test
    public void backToLobby(){
        FxAssert.verifyThat(stage1.getScene(), is(gameFXML.getScene()));
        moveTo("#border");
        clickOn("#leave");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        FxAssert.verifyThat(stage1.getScene(), is(AdvancedWarsApplication.getInstance().getLobbyScene()));
    }

}

