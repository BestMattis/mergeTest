package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class AdvancedWarsApplication extends Application {

    static AdvancedWarsApplication advancedWarsApplication;
    public Stage primaryStage;
    private Scene lobbyScene;

    /**
     * Method to start the app
     *
     * @param args commandline-parameter
     */
    public static void main(String[] args) {
        Application.launch(AdvancedWarsApplication.class, args);
    }

    /**
     * This method returns the instance of this class
     *
     * @return AdvancedWarsApplication the instace of the class
     */
    static public AdvancedWarsApplication getInstance() {
        return advancedWarsApplication;
    }

    /**
     * This method starts the first Stage, displays it in Fullscreen, and sets the Title. is called from the main method
     *
     * @param priStage the stage the app is displayed in
     */
    @Override
    public void start(Stage priStage) {
        advancedWarsApplication = this;
        primaryStage = priStage;
        primaryStage.setFullScreen(true);
        primaryStage.setTitle("Title can not be loaded");
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream inputStream = null;
            inputStream = classLoader.getResource("lobby/en-US.properties").openStream();
            ResourceBundle bundle = new PropertyResourceBundle(inputStream);
            primaryStage.setTitle(bundle.getString("Applicationtitle"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        goToLobby();

        primaryStage.show();
    }

    /**
     * Loads the LobbyScreen.fxml and displays it on the stage given(the primaryStage)
     */
    public void goToLobby() {
        if (lobbyScene == null) {
            try {
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                InputStream inputStream = classLoader.getResource("lobby/en-US.properties").openStream();
                ResourceBundle bundle = new PropertyResourceBundle(inputStream);
                URL loc = getClass().getResource("/lobby/LobbyScreen.fxml");
                if (loc == null) {
                    System.out.println("loc null");
                }
                FXMLLoader fxmlLoader = new FXMLLoader(loc, bundle);
                Parent parent = fxmlLoader.load();
                Scene scene = new Scene(parent);
                lobbyScene = scene;
                primaryStage.setScene(scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            primaryStage.setScene(lobbyScene);
        }
    }

}
