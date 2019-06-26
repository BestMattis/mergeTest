package playerList;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.App;
import model.Model;
import model.Player;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class PlayerListTest extends ApplicationTest {

    private PlayerListController playerListController;

    @Override
    public void start(Stage stage) throws Exception {

        App app = new App().withAllPlayers(new Player().setName("TestPlayer"));

        Model.getInstance().setApp(app);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classLoader.getResource("en-US.properties").openStream();
        ResourceBundle bundle = new PropertyResourceBundle(inputStream);
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getClassLoader().getResource("playerList/playerList.fxml"), bundle);

        Parent mainNode = fxmlLoader.load();
        VBox vBox = new VBox(mainNode);
        playerListController = fxmlLoader.getController();

        stage.setScene(new Scene(vBox));
        stage.show();
        stage.toFront();
    }

    @SuppressWarnings("unchecked")
    public <T extends Node> T find(final String query) {
        return (T) lookup(query).queryAll().iterator().next();
    }

    @Test
    public void numberOfPlayers() {
        Assert.assertEquals(Model.getApp().getAllPlayers().size(), Integer.parseInt(playerListController.getNumberOfPlayers().getText()));
    }

    @Test
    public void propertyChange() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Model.getApp().withAllPlayers(new Player().setName("TestPlayer"));
            }
        });
        numberOfPlayers();
    }

}
