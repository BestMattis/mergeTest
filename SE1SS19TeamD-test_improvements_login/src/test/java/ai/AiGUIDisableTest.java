package ai;

import gameScreen.MenuDisplayController;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import main.AdvancedWarsApplication;
import model.*;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import registerLogin.LoginRegisterTestUtils;

import java.util.ArrayList;

public class AiGUIDisableTest extends ApplicationTest {

    Model model;
    AdvancedWarsApplication awa;
    ArrayList<String> r;
    ArrayList<String> nr;

    /**
     * Setup stage and start application.
     *
     * @param primaryStage the stage to display the GUI.
     */
    @Override
    public void start(Stage primaryStage) throws InterruptedException {
        awa = new AdvancedWarsApplication();
        awa.start(primaryStage);
        awa.offtesting = true;
        awa.offlineTest = true;
        model = awa.model;
        Player player1 = new Player();
        player1.setName("Player1");
        model.getApp().withAllPlayers(player1);
        LoginRegisterTestUtils.loginForOfflineTest(this, model);
        r = new ArrayList<>();
        nr = new ArrayList<>();
        r.add("Player1");
        r.add(model.getApp().getCurrentPlayer().getName());
        Game game = new Game();
        GameField gameField = new GameField();
        gameField.setGame(game);
        Field field = new Field();
        field.setPosY(0).setPosX(0).setType("Water").setGameField(gameField);
        model.getApp().getCurrentPlayer().setGame(game);
        awa.goToGame(game);
    }

    @Test
    public void aiGUIDisableStoryTest(){

        //Check if Buttons are clickable, and Gamefield movable

        Assert.assertFalse(awa.getGameScreenCon().basicRound.isDisabled());
        Assert.assertFalse(awa.getGameScreenCon().getMenuFXML().getController(MenuDisplayController.class).getFinishRound().isDisabled());
        double v1 = awa.getGameScreenCon().getGamefield().getVvalue();
        for(int i = 0; i < 10; i++) {
            press(KeyCode.UP);
        }
        Assert.assertNotEquals(v1, awa.getGameScreenCon().getGamefield().getVvalue());
        Field f1 = awa.getGameScreenCon().getGameFieldController().getLastClickedField();
        clickOn("#colorCanvas");
        Assert.assertNotEquals(awa.getGameScreenCon().getGameFieldController().getLastClickedField(), f1);

        //activate AI

        clickOn("#border");
        clickOn("#aiButton");

        //Check if Buttons arent clickable, and Gamefield is movable
        Assert.assertTrue(awa.getGameScreenCon().basicRound.isDisabled());
        Assert.assertTrue(awa.getGameScreenCon().getMenuFXML().getController(MenuDisplayController.class).getFinishRound().isDisabled());
        f1 = awa.getGameScreenCon().getGameFieldController().getLastClickedField();
        clickOn("#colorCanvas");
        Assert.assertEquals(awa.getGameScreenCon().getGameFieldController().getLastClickedField(), f1);
        double v2= awa.getGameScreenCon().getGamefield().getVvalue();
        press(KeyCode.DOWN);
        Assert.assertNotEquals(v2, awa.getGameScreenCon().getGamefield().getVvalue());

        //deactivate AI

        clickOn("#border");
        clickOn("#aiButton");

        //Check if Buttons are clickable, and Gamefield movable
        Assert.assertFalse(awa.getGameScreenCon().basicRound.isDisabled());
        Assert.assertFalse(awa.getGameScreenCon().getMenuFXML().getController(MenuDisplayController.class).getFinishRound().isDisabled());
        f1 = awa.getGameScreenCon().getGameFieldController().getLastClickedField();
        double v3 = awa.getGameScreenCon().getGamefield().getVvalue();
        clickOn("#mappane");
        Assert.assertNotEquals(v3, awa.getGameScreenCon().getGamefield().getVvalue());
        clickOn("#colorCanvas");
        Assert.assertNotEquals(awa.getGameScreenCon().getGameFieldController().getLastClickedField(), f1);
        clickOn("#colorCanvas");
        clickOn("#mappane");
        Assert.assertNotEquals(v3, awa.getGameScreenCon().getGamefield().getVvalue());

    }


}
