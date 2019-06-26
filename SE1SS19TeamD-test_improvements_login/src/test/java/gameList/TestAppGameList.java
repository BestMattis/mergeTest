package gameList;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.App;
import model.Game;
import model.Model;
import model.Player;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class TestAppGameList extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        App app = new App();

        ArrayList<Game> games = new ArrayList<>();
        ArrayList<Player> players = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            games.add(new Game().setName("Game" + (i + 1)).setCapacity(4).setApp(app));
        }
        for (int i = 0; i < 40; i++) {
            players.add(new Player().setName("Player" + 1).setApp(app));
        }
        int index = 0;
        for (Game game : games) {
            for (int i = index; i < index + 2; i++) {
                game.withPlayers(players.get(i));
            }
            if (game.getName().equals("Game20")) {
                game.withPlayers(new Player(), new Player());
            }
            index = index + 2;
        }


        Model.getInstance().setApp(app);


        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream inputStream = classLoader.getResource("en-US.properties").openStream();
            ResourceBundle bundle = new PropertyResourceBundle(inputStream);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("gameList/gameList.fxml"), bundle);
            Parent parent = fxmlLoader.load();
            Scene scene = new Scene(parent, 600, 400);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

}
