package registerLogin;

import asyncCommunication.WebSocketComponent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import main.AdvancedWarsApplication;
import model.Model;
import model.Player;
import syncCommunication.HttpRequests;
import syncCommunication.RESTExceptions.LoginFailedException;
import syncCommunication.RESTExceptions.RegistrationFailedException;
import syncCommunication.SynchronousUserCommunicator;

import java.io.IOException;
import java.io.InputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class RegisterLoginController {
    @SuppressWarnings("static-access")
    static AdvancedWarsApplication app = new AdvancedWarsApplication().getInstance();
    @FXML
    public Label msgLabel;    //public for the tests
    @FXML
    public TextField nameTextfield;
    @FXML
    public PasswordField pwTextfield;
    @FXML
    public Button regButton;
    @FXML
    private AnchorPane basePane;
    @FXML
    private Label topLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label pwLabel;
    @FXML
    private Button logButton;

    /*
     * called when the registerLogin scene is loaded. set the methods for clicking
     * on the buttons and the user input via text fields
     */
    public void initialize() {
        msgLabel.setVisible(false);

        /* textfield Listener */
        nameTextfield.textProperty().addListener((observable, oldValue, newValue) -> {
            msgLabel.setVisible(false);
        });
        pwTextfield.textProperty().addListener((observable, oldValue, newValue) -> {
            msgLabel.setVisible(false);
        });

        /* Button-Methoden aufrufe */
        logButton.setOnAction(e -> loginUser());
        regButton.setOnAction(e -> {
            try {
                registerUser();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        
        pwTextfield.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    loginUser();
                }
            }
        });
    }

    /*
     * called from logButton loads the Login-module into the registerLoginScreen
     */
    private void loginUser() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = null;
        ResourceBundle bundle = null;
        try {
            inputStream = classLoader.getResource("en-US.properties").openStream();
            bundle = new PropertyResourceBundle(inputStream);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
        String username = this.nameTextfield.getText();
        String password = this.pwTextfield.getText();

        if (username != null && password != null) {
            if (username.contains(" ") || password.contains(" ")) { // errorMsg false input
                msgLabel.setVisible(true);
                msgLabel.setTextFill(Color.RED);
                msgLabel.setText(bundle.getString("regLog.SpaceErrorRegister"));
            }
        }
        
        boolean success = false;
        Player currentPlayer = Model.getApp().getCurrentPlayer();
        HttpRequests httpRequests = Model.getPlayerHttpRequestsHashMap().get(currentPlayer);
        String userKey = "";
        if(httpRequests != null)
        {
            success = true;
        }
        else
        {
            httpRequests = new HttpRequests();
        }
        SynchronousUserCommunicator synchronousUserCommunicator =
                new SynchronousUserCommunicator(httpRequests);
        
        try {
            success = synchronousUserCommunicator.logIn(username, password);
        } catch (LoginFailedException e) {
            this.msgLabel.setText(bundle.getString("regLog.FailedLogin"));
        }

        userKey = synchronousUserCommunicator.getUserKey();
        
        if (!success) {
            msgLabel.setTextFill(Color.RED); // registrierung unsuccessful
            msgLabel.setText(bundle.getString("regLog.FailedLogin"));
        } else {
            if(currentPlayer == null)
            {
        	currentPlayer = new Player().setName(username)
                    .setPassword(password).setApp(Model.getApp());
        	Model.getApp().setCurrentPlayer(currentPlayer);
        	Model.getPlayerHttpRequestsHashMap()
                    .put(currentPlayer, httpRequests);
            }
            AdvancedWarsApplication.getInstance().goToLobby();
            //start WS-component
            if(Model.getWebSocketComponent() == null)
            {
        	Model.setWebSocketComponent(new WebSocketComponent(username, userKey));
            }
        }

        nameTextfield.clear();
        pwTextfield.clear();
        msgLabel.setVisible(true);
    }

    /*
     * called from regButton takes NAME and PW and registers a user on the server.
     * filters user input, shows error messages on false input or failed registration
     */
    private void registerUser() throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = null;
        inputStream = classLoader.getResource("en-US.properties").openStream();
        ResourceBundle bundle = new PropertyResourceBundle(inputStream);
        String userName = this.nameTextfield.getText();
        String userPw = this.pwTextfield.getText();
        String errorMsg = null;

        Boolean success = false;

        if (userName != null && userName.length() > 0 && !userName.contains(" ") && // dont allow empty user names or
                // pws
                userPw != null && userPw.length() > 0 && !userPw.contains(" ")) { // dont allow spaces

            nameTextfield.clear();
            pwTextfield.clear();
            msgLabel.setVisible(true);

            try {
                HttpRequests hr = new HttpRequests();
                success = new SynchronousUserCommunicator(hr).register(userName, userPw);
            } catch (RegistrationFailedException e) {
                errorMsg = e.getMessage();
            }

            if (success) {
                msgLabel.setTextFill(Color.LIGHTGREEN); // registrierung successful
                msgLabel.setText(bundle.getString("regLog.SuccRegister"));
            } else {
                msgLabel.setTextFill(Color.RED); // registrierung unsuccessful
                msgLabel.setText(bundle.getString("regLog.FailedRegistration") + ": " + errorMsg);
            }
        }
        if (userName != null && userPw != null) {
            if (userName.contains(" ") || userPw.contains(" ")) { // errorMsg false input
                msgLabel.setVisible(true);
                msgLabel.setTextFill(Color.RED);
                msgLabel.setText(bundle.getString("regLog.SpaceErrorRegister"));
            }
        }
    }
}
