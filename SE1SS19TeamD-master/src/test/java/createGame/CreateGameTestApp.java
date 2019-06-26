package createGame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class CreateGameTestApp extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream inputStream = classLoader.getResource("en-US.properties").openStream();
            ResourceBundle bundle = new PropertyResourceBundle(inputStream);
            FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getClassLoader().getResource("createGame/CreateGame.fxml"), bundle);
            Parent parent = fxmlLoader.load();
            Scene scene = new Scene(parent, 600, 400);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
