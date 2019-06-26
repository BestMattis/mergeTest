package lobby;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import main.AdvancedWarsApplication;
import model.Player;
import org.junit.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit.ApplicationTest;

import static org.hamcrest.CoreMatchers.is;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;


public class Screentest extends ApplicationTest {

    static Stage pri;

    @SuppressWarnings("unchecked")
    public <T extends Node> T find(final String query) {
        return (T) lookup(query).queryAll().iterator().next();
    }

    public void start(Stage stage) throws Exception {
        AdvancedWarsApplication awa = new AdvancedWarsApplication();
        awa.start(stage);


        //Player player = new Player();
        //player.setName("testname");
        //Model.getApp().setCurrentPlayer(player);

        pri = stage;
        //AdvancedWarsApplication.getInstance().goToLobby();
    }

    @Test
    public void test() {
        FxAssert.verifyThat(true, is(pri.isFullScreen()));
        FxAssert.verifyThat(pri, is(AdvancedWarsApplication.getInstance().primaryStage));
    }

    @Test
    public void infoShowTest() {
        clickOn("#nameTextfield");
        write("testname");
        clickOn("#pwTextfield");
        write("testpw");
        clickOn("#regButton");

        clickOn("#nameTextfield");
        write("testname");
        clickOn("#pwTextfield");
        write("testpw");
        clickOn("#logButton");

        AnchorPane base2 = find("#trpane");
        FxAssert.verifyThat(base2.isVisible(), is(false));
        clickOn("#logo");
        FxAssert.verifyThat(base2.isVisible(), is(true));
        TabPane credpane = find("#credpane");
        FxAssert.verifyThat(credpane.getTabs().size(), is(2));
        FxAssert.verifyThat(credpane.getTabs().get(0).isSelected(), is(true));
        clickOn(from(base2).lookup("#logout").queryButton());
        FxAssert.verifyThat(base2.isVisible(), is(false));

    }

    @Test
    public void chatTabTest() {
        clickOn("#nameTextfield");
        write("testname");
        clickOn("#pwTextfield");
        write("testpw");
        clickOn("#regButton");

        clickOn("#nameTextfield");
        write("testname");
        clickOn("#pwTextfield");
        write("testpw");
        clickOn("#logButton");

        TabPane tabPane = find("#singleTabPane");
        FxAssert.verifyThat(tabPane.getTabs().isEmpty(), is(true));
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Player player = new Player();
                player.setName("blubb");
                AdvancedWarsApplication.getInstance().getLobbyCon().getChatCon().getSingleController().newTab(player);
            }
        });
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        FxAssert.verifyThat(tabPane.getTabs().size(), is(1));
    }

    @Test
    public void chatMessageDisplayTest() {
        /*
        clickOn("#nameTextfield");
        write("testname");
        clickOn("#pwTextfield");
        write("testpw");
        clickOn("#regButton");

        clickOn("#nameTextfield");
        write("testname");
        clickOn("#pwTextfield");
        write("testpw");
        clickOn("#logButton");

        VBox history = find("#history");
        FxAssert.verifyThat(history.getChildren().isEmpty(), is(true));
        final Player player1 = new Player();
        player1.setName("bla");
        final Player player2 = new Player();
        player2.setName("blubb");
        FutureTask query = new FutureTask(new Callable() {
            @Override
            public Object call() throws Exception {
                AdvancedWarsApplication.getInstance().getLobbyCon().getChatCon().getSingleController().newTab(player1);
                AdvancedWarsApplication.getInstance().getLobbyCon().getChatCon().getSingleController().newTab(player2);
                return null;
            }
        });
        Platform.runLater(query);
        try {
            query.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        FxAssert.verifyThat(true, is(AdvancedWarsApplication.getInstance().getLobbyCon().getChatCon().getSingleController().chatTabs.get(0).AllController.getHistory().getChildren().isEmpty()));
        clickOn("#chatAll");
        clickOn("#message");
        write("hallo");
        clickOn("#send");
        clickOn("#message");
        write("testtext FX");
        clickOn("#send");

        FxAssert.verifyThat(2, is(AdvancedWarsApplication.getInstance().getLobbyCon().getChatCon().getAllController().getHistory().getChildren().size()));
        FxAssert.verifyThat(true, is(AdvancedWarsApplication.getInstance().getLobbyCon().getChatCon().getSingleController().chatTabs.get(0).AllController.getHistory().getChildren().isEmpty()));
        FxAssert.verifyThat(true, is(AdvancedWarsApplication.getInstance().getLobbyCon().getChatCon().getSingleController().chatTabs.get(1).AllController.getHistory().getChildren().isEmpty()));
        FxAssert.verifyThat(0, is(player1.getSendedMessages().size()));
        FxAssert.verifyThat(0, is(player2.getReceivedMessages().size()));

        clickOn("#chatPlayers");
        clickOn("#message");
        write("testtext 2 FX");
        clickOn("#send");
        clickOn("#chatAll");

        FxAssert.verifyThat(2, is(AdvancedWarsApplication.getInstance().getLobbyCon().getChatCon().getAllController().getHistory().getChildren().size()));
        FxAssert.verifyThat(1, is(AdvancedWarsApplication.getInstance().getLobbyCon().getChatCon().getSingleController().chatTabs.get(0).AllController.getHistory().getChildren().size()));
        FxAssert.verifyThat(true, is(AdvancedWarsApplication.getInstance().getLobbyCon().getChatCon().getSingleController().chatTabs.get(1).AllController.getHistory().getChildren().isEmpty()));
        FxAssert.verifyThat(1, is(player1.getSendedMessages().size()));
        FxAssert.verifyThat(0, is(player2.getReceivedMessages().size()));
        */
    }

    @Test
    public void tabNamingTest() {
        clickOn("#nameTextfield");
        write("testname");
        clickOn("#pwTextfield");
        write("testpw");
        clickOn("#regButton");

        clickOn("#nameTextfield");
        write("testname");
        clickOn("#pwTextfield");
        write("testpw");
        clickOn("#logButton");

        final Player player1 = new Player();
        final Player player2 = new Player();
        player1.setName("blubb");
        player2.setName("hans");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                AdvancedWarsApplication.getInstance().getLobbyCon().getChatCon().getSingleController().newTab(player1);
                AdvancedWarsApplication.getInstance().getLobbyCon().getChatCon().getSingleController().newTab(player2);
            }
        });
        try {
            Thread.sleep(100);  //Diesen Test mit Einfügen der Chat-Buttons in die Playerliste überarbeiten und platform runlater nit einem FxTest ersetzen.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        FxAssert.verifyThat("blubb", is(AdvancedWarsApplication.getInstance().getLobbyCon().getChatCon().getSingleController().chatTabs.get(0).getTab().getText()));
        FxAssert.verifyThat("hans", is(AdvancedWarsApplication.getInstance().getLobbyCon().getChatCon().getSingleController().chatTabs.get(1).getTab().getText()));

    }

}
