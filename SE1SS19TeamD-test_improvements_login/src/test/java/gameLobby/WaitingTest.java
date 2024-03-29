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

public class WaitingTest extends ApplicationTest {

    static Stage pri;
    private Model model;

    public void start(Stage stage) throws Exception {

        App app = new App();

        ArrayList<Game> games = new ArrayList<>();
        ArrayList<Player> players = new ArrayList<>();

        AdvancedWarsApplication awa = new AdvancedWarsApplication();
        AdvancedWarsApplication.getInstance().offtesting = true;
        awa.start(stage);
        model = awa.model;


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


    @Test
    public void waitTest() {
        clickOn("#nameTextfield");
        write("testname");
        clickOn("#pwTextfield");
        write("testpw");
        clickOn("#logButton");
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
        clickOn("#nameTextfield");
        write("testname");
        clickOn("#pwTextfield");
        write("testpw");
        clickOn("#logButton");
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
    public void nameTest() {
        clickOn("#nameTextfield");
        write("testname");
        clickOn("#pwTextfield");
        write("testpw");
        clickOn("#logButton");
        clickOn("#gameName");
        Label playerBox = find("#p1");
        FxAssert.verifyThat(playerBox.getText(), is("Player0"));

    }

    @Test
    public void test() {
        clickOn("#nameTextfield");
        write("testname");
        clickOn("#pwTextfield");
        write("testpw");
        clickOn("#logButton");
        clickOn("#gameName");
    }
}
