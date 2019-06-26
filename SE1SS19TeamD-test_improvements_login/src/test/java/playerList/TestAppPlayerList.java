package playerList;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Model;
import model.Player;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class TestAppPlayerList extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        ArrayList<Player> players = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            players.add(new Player().setName("Player" + (i + 1)));
        }
        Model.getApp().withAllPlayers(players);

        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream inputStream = classLoader.getResource("en-US.properties").openStream();
            ResourceBundle bundle = new PropertyResourceBundle(inputStream);
            FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getClassLoader().getResource("playerList/playerList.fxml"), bundle);
            Parent parent = fxmlLoader.load();
            Scene scene = new Scene(parent, 600, 400);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e1) {
            e1.printStackTrace();
        }


    }
}
