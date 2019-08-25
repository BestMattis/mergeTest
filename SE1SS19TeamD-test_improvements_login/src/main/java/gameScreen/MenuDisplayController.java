package gameScreen;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import lobby.OptionsController;
import main.AdvancedWarsApplication;
import model.Game;
import model.Model;
import model.Player;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class MenuDisplayController {

    @FXML
    private Button leave;
    @FXML
    private Button finishRound;
    @FXML
    private Button chatbutton;
    @FXML
    private Label player1;
    @FXML
    private Label player2;
    @FXML
    Label player3; 
    @FXML
    Label roundCount;
    @FXML
    private Button options;
    @FXML
    private AnchorPane base;
    @FXML
    public Button aiTakeover;
    private Model model;
    private GameScreenController par;
    private ResourceBundle bundle = null;

    public Button getFinishRound(){
        return finishRound;
    }

    public MenuDisplayController(GameScreenController p, Model model) {
        this.model = model;
        par = p;
    }


    /**
     * add the action for the leave-button and the button to open the chat
     */
    public void initialize() {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = null;

        try {
            inputStream = classLoader.getResource("en-US.properties").openStream();
            bundle = new PropertyResourceBundle(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        leave.setOnAction(t -> {
            leaveGame();
        });
        chatbutton.setOnAction(t -> showChat());
        finishRound.setOnAction(t -> AdvancedWarsApplication.getInstance().getGameScreenCon().endRound());
        options.setOnAction(t -> openOptions());
        updateAIButton();
        finishRound.setDisable(AdvancedWarsApplication.getInstance().isAiActive());
        AdvancedWarsApplication.getInstance().addPropertyChangeListener("aiActive", t -> finishRound.setDisable(AdvancedWarsApplication.getInstance().isAiActive()));
        aiTakeover.setOnAction(t -> changeAIActivity());
    }

    /**
     * this methode is called by the AI-Takeover button in the gamemenu an changes the activitystate of the ai
     */
    private void changeAIActivity(){
        AdvancedWarsApplication.getInstance().setAiActive(!AdvancedWarsApplication.getInstance().isAiActive());
        updateAIButton();
    }

    /**
     * changes the color and text of the AI-Takeoverbutton
     */
     private void updateAIButton(){
         System.out.println("Button changed:");
        if (AdvancedWarsApplication.getInstance().isAiActive()){
            if (bundle != null) {
                //aiTakeover.setText(bundle.getString("menu.player"));
                aiTakeover.setText("Player\nTakeover");
            }
            aiTakeover.getStyleClass().clear();
            aiTakeover.getStyleClass().add("button");
            aiTakeover.getStyleClass().add("greenButton");
        } else {
            if (bundle != null) {
                //aiTakeover.setText(bundle.getString("menu.ai"));
                aiTakeover.setText("AI\nTakeover");
            }
            aiTakeover.getStyleClass().clear();
            aiTakeover.getStyleClass().add("button");
            aiTakeover.getStyleClass().add("purpleButton");
        }
     }

    /**
     * display the chat
     */
    private void showChat() {
        par.chatFXML.getController(GameChatController.class).animateUsingTimeline();
    }

    /**
     * set the screen to the lobby
     */
    public void leaveGame() {
        model.getWebSocketComponent().leaveGame();
        AdvancedWarsApplication.getInstance().goToLobby();
        model.getApp().getCurrentPlayer().setGame(null);
        AdvancedWarsApplication.getInstance().resetGameScreenFXML();
    }

    /**
     * displays the names of the other players in the menu
     */
    public void setPlayers() {
        Game cgame = model.getApp().getCurrentPlayer().getGame();
        ArrayList<Player> players = (ArrayList<Player>) cgame.getPlayers().clone();
        players.remove(model.getApp().getCurrentPlayer());
        if (players.size() > 0) {
            player1.setText(players.get(0).getName());
            player1.setVisible(true);
        }
        if (players.size() > 1) {
            player2.setText(players.get(1).getName());
            player2.setVisible(true);
        }
        if (players.size() > 2) {
            player3.setText(players.get(2).getName());
            player3.setVisible(true);
        } else {
            player3.setText("You could invite more friends...");
            player3.setVisible(true);
        }
    }
	
	public ArrayList<Label> getPlayerLabels(){
		ArrayList<Label> labels = new ArrayList<Label>();
		labels.add(player1);
		labels.add(player2);
		labels.add(player3);
		return labels;
	}
	
	public Label getRoundCount() {
		return roundCount;
	}
	
	public Button getEndRound() {
		return finishRound;
	}

	/**
     * this methode hides / disables all useable elements
     * for the observer
     */
    public void observerModeGameScreenMenu() {
        finishRound.setVisible(false);
    }

    /**
     * this methode adds the action to open the options to the optionsbutton
     */
    private void openOptions() {
        par.optionsFXML.getController(OptionsController.class).show();
        base.setVisible(false);
    }
}
