package gameLobby;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import main.AdvancedWarsApplication;
import model.App;
import model.Game;
import model.Model;
import model.Player;
import org.junit.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit.ApplicationTest;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static org.hamcrest.CoreMatchers.is;

public class GameLobbyTest extends ApplicationTest {

    static Stage pri;
    private Model model;

    public void start(Stage stage) throws Exception {

        AdvancedWarsApplication awa = new AdvancedWarsApplication();
        awa.start(stage);
        model = awa.model;
        AdvancedWarsApplication.getInstance().offtesting = true;
        App app = new App();

        ArrayList<Game> games = new ArrayList<>();
        ArrayList<Player> players = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            games.add(new Game().setName("Game" + (i + 1)).setCapacity(4).setApp(app));
        }
        for (int i = 0; i < 40; i++) {
            players.add(new Player().setName("Player" + i).setApp(app));
        }
        int index = 0;
        for (Game game : games) {
            for (int i = index; i < index + 2; i++) {
                game.withPlayers(players.get(i));
            }
            index = index + 2;
        }

        model.setApp(app);

        pri = stage;
        //AdvancedWarsApplication.getInstance().goToLobby();

    }

    @SuppressWarnings("unchecked")
    public <T extends Node> T find(final String query) {
        return (T) lookup(query).queryAll().iterator().next();
    }

    public void login() {
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
    }

    @Test
    public void waitTest() {
        login();
        FxAssert.verifyThat(AdvancedWarsApplication.getInstance().primaryStage.getScene(), is(AdvancedWarsApplication.getInstance().getLobbyScene()));
        clickOn("#gameName");
        AnchorPane base = find("#waitingbase");
        FxAssert.verifyThat(base.isVisible(), is(true));
        FxAssert.verifyThat((AdvancedWarsApplication.getInstance().primaryStage.getScene() == AdvancedWarsApplication.getInstance().getLobbyScene()), is(false));
        clickOn("#back");
        FxAssert.verifyThat(base.isVisible(), is(false));
        FxAssert.verifyThat(AdvancedWarsApplication.getInstance().primaryStage.getScene(), is(AdvancedWarsApplication.getInstance().getLobbyScene()));
    }

    @Test
    public void addingPlayerTest() {
        login();
        clickOn("#gameName");
        Label name = find("#gameName");
        FxAssert.verifyThat(name.getText(), is(model.getApp().getAllGames().get(0).getName()));
        Label count = find("#count");
        FxAssert.verifyThat(count.getText(), is("2/4"));
        Player player = new Player();
        player.setName("TestPlayer");


        final FutureTask query = new FutureTask(new Callable() {
            @Override
            public Object call() throws Exception {
                model.getApp().getAllGames().get(0).withPlayers(player);
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


        FxAssert.verifyThat(count.getText(), is("3/4"));

    }

    @Test
    public void test() {
        login();
        clickOn("#gameName");
    }

    @Test
    public void armyImplementationTest() {
        login();
        FxAssert.verifyThat(AdvancedWarsApplication.getInstance().getLobbyCon().armymanagerFXML.getParent().isVisible(), is(false));
        clickOn("#armymanager");
        FxAssert.verifyThat(AdvancedWarsApplication.getInstance().getLobbyCon().armymanagerFXML.getParent().isVisible(), is(true));
        clickOn("#back");
        FxAssert.verifyThat(AdvancedWarsApplication.getInstance().getLobbyCon().armymanagerFXML.getParent().isVisible(), is(false));
        clickOn("#gameName");
        FxAssert.verifyThat(AdvancedWarsApplication.getInstance().getLobbyCon().armymanagerFXML.getParent().isVisible(), is(false));
        clickOn("#manager");
        FxAssert.verifyThat(AdvancedWarsApplication.getInstance().getGameScreenCon().getGameLobbyController().armymanagerFXML.getParent().isVisible(), is(true));
        clickOn("#back");
        FxAssert.verifyThat(AdvancedWarsApplication.getInstance().getLobbyCon().armymanagerFXML.getParent().isVisible(), is(false));
        moveTo("#count");

    }
}
