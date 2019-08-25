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

public class WinnerScreenController {

    Model model;

    @FXML
    private AnchorPane winnerBase;
    @FXML
    private Button backButtonWinner;
    @FXML
    private Label winnerLabel;
    @FXML
    private Label winTextLabel;
    @FXML
    private Label nameLabel;

    private ObjectProperty<Font> fontTracking = new SimpleObjectProperty<Font>(Font.getDefault());

    public WinnerScreenController(Model model) {
        this.model = model;
    }

    public void initialize() {
        winnerLabel.fontProperty().bind(fontTracking);
        nameLabel.fontProperty().bind(fontTracking);
        winTextLabel.fontProperty().bind(fontTracking);
        winnerBase.setOpacity(0.95);
        winnerBase.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldWidth, Number newWidth) {
                fontTracking.set(Font.font(newWidth.doubleValue() / 10));
            }
        });
        String winner = model.getApp().getCurrentPlayer().getGame().getWinner();
        nameLabel.setText(winner);
        backButtonWinner.setOnAction(t -> backToLobby());
    }

    private void backToLobby() {
        model.getWebSocketComponent().leaveGame();
        AdvancedWarsApplication.getInstance().goToLobby();
        AdvancedWarsApplication.getInstance().getGameScreenCon().getGameLobbyController().hide();
        model.getApp().getCurrentPlayer().setGame(null);
    }
}
