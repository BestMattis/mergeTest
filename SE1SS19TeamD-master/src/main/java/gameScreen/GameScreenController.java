package gameScreen;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import gamelobby.WaitingScreenContoller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class GameScreenController {

    @FXML
    AnchorPane base;

    private WaitingScreenContoller waitingScreenContoller;

    @FXML
    public void initialize(){
        System.out.println("gameScreenLoaded");
        loadWaiting();
    }

    private void loadWaiting(){
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream inputStream = classLoader.getResource("en-US.properties").openStream();
            ResourceBundle bundle = new PropertyResourceBundle(inputStream);
            URL loc = getClass().getResource("/waitingScreen/WaitingScreen.fxml");
            if (loc == null) {
                System.out.println("loc null");
            }
            FXMLLoader fxmlLoader = new FXMLLoader(loc, bundle);
            waitingScreenContoller = new WaitingScreenContoller();
            fxmlLoader.setController(waitingScreenContoller);
            Parent parent = fxmlLoader.load();
            AnchorPane.setTopAnchor(parent, 0d);
            AnchorPane.setRightAnchor(parent, 0d);
            AnchorPane.setBottomAnchor(parent, 0d);
            AnchorPane.setLeftAnchor(parent, 0d);
            base.getChildren().add(parent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the controller of the waiting Screen
     */
    public WaitingScreenContoller getWaitingScreenContoller() {
        return waitingScreenContoller;
    }
}
