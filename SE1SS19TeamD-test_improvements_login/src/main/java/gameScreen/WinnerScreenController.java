package gameScreen;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import main.AdvancedWarsApplication;
import model.Model;
import model.Player;

public class WinnerScreenController {
	@FXML
	private AnchorPane winnerBase;
	@FXML 
	private Button backButton;
	@FXML
	private Label winnerLabel;
	@FXML
	private Label winTextLabel;
	@FXML
	private Label nameLabel;
	
	private ObjectProperty<Font> fontTracking = new SimpleObjectProperty<Font>(Font.getDefault());
	
	public void initialize() {
		winnerLabel.fontProperty().bind(fontTracking);
		nameLabel.fontProperty().bind(fontTracking);
		winTextLabel.fontProperty().bind(fontTracking);
		winnerBase.setOpacity(0.95);
		winnerBase.widthProperty().addListener(new ChangeListener<Number>(){
	        @Override
	        public void changed(ObservableValue<? extends Number> observableValue, Number oldWidth, Number newWidth){
	            fontTracking.set(Font.font(newWidth.doubleValue() / 10));
	        }
	    });
		String winner = Model.getApp().getCurrentPlayer().getGame().getWinner();
		nameLabel.setText(winner);
		backButton.setOnAction(t -> backToLobby());
	}

	private void backToLobby() {
		Model.getWebSocketComponent().leaveGame();
        AdvancedWarsApplication.getInstance().goToLobby();
        AdvancedWarsApplication.getInstance().getGameScreenCon().getGameLobbyController().hide();
        Model.getApp().getCurrentPlayer().setGame(null);
	}
}
