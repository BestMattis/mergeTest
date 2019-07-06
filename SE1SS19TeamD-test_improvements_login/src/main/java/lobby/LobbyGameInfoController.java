package lobby;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebView;

import java.io.IOException;
import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class LobbyGameInfoController {

    @FXML
    Button back;
    @FXML
    AnchorPane base2;
    @FXML
    WebView web;
    @FXML
    TextFlow credits;

    /**
     * sets the action on the back button and hides the panel
     */
    public void initialize(){
        back.setOnAction(t -> backToLobby());
        resetGameInfo();
        initCredits();
        base2.setVisible(false);
    }

    /**
     * action called by the back button to go back to the lobby
     */
    public void backToLobby(){
        System.out.println("back");
        base2.setVisible(false);
        resetGameInfo();
    }

    public void show(){
        base2.setVisible(true);
    }

    /**
     * resets the infopage every time the panel is closed
     */
    public void resetGameInfo(){
        web.getEngine().load("https://warswiki.org/wiki/Main_Page");
    }

    /**
     * method to generate the credits text
     */
    public void initCredits(){
        ResourceBundle cbundle = loadBundle();
        Text poTitle = new Text();
        poTitle.setText(cbundle.getString("credits.owner") + "\n");
        poTitle.setId("creditTextTitle");
        Text po = new Text();
        String poString = cbundle.getString("credits.christian") + "\n\n";
        po.setText(poString);
        po.setId("creditText");

        Text smTitle = new Text();
        smTitle.setText(cbundle.getString("credits.master") + "\n");
        smTitle.setId("creditTextTitle");
        Text sm = new Text();
        String smString = cbundle.getString("credits.lukas") + "\n\n";
        sm.setText(smString);
        sm.setId("creditText");

        Text devTitle = new Text();
        devTitle.setText(cbundle.getString("credits.dev") + "\n");
        devTitle.setId("creditTextTitle");
        Text devs = new Text();
        String devString = cbundle.getString("credits.sebastian") + "\n" + cbundle.getString("credits.marco") + "\n" + cbundle.getString("credits.mattis") + "\n" + cbundle.getString("credits.joshua") + "\n" + cbundle.getString("credits.ahmad") + "\n" + cbundle.getString("credits.felix") + "\n\n";
        devs.setText(devString);
        devs.setId("creditText");


        credits.getChildren().addAll(poTitle, po, smTitle, sm, devTitle, devs);
    }

    /** loads the properties
     * @return the properties bundle
     */
    public ResourceBundle loadBundle(){
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = null;
        try {
            inputStream = classLoader.getResource("en-US.properties").openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ResourceBundle bundle = null;
        try {
            bundle = new PropertyResourceBundle(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }

}
