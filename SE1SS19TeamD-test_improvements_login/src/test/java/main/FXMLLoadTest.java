package main;


import javafx.application.Platform;
import javafx.stage.Stage;
import lobby.LobbyScreenController;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

public class FXMLLoadTest extends ApplicationTest {

    static Stage pri;
    FXMLLoad fxmlLoad;

    public void start(Stage stage) throws Exception {
        AdvancedWarsApplication awa = new AdvancedWarsApplication();
        awa.start(stage);
        awa.goToLobby();

        pri = stage;
        fxmlLoad = new FXMLLoad("/lobby/LobbyScreen.fxml", "en-US.properties");

    }

    @Test
    public void testFull(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                pri.setScene(fxmlLoad.getScene());
            }
        });
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Object con = fxmlLoad.getController();
        Assert.assertEquals(fxmlLoad.getController().getClass(), LobbyScreenController.class);
        Assert.assertEquals(fxmlLoad.getScene(), pri.getScene());
        Assert.assertEquals(FXMLLoad.getFXMLFor(con).get(0).getScene(), pri.getScene());

        fxmlLoad.delete();

        Assert.assertEquals(FXMLLoad.getFXMLFor(con).isEmpty(), true);
    }

}
