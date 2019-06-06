package login;

import javafx.scene.control.TextField;
import main.AdvancedWarsApplication;

public class Login {

    /**
     * Method used to get the username and password from the textfields and give them to another method(login in ClientSession)
     *
     * @param username a Textfield
     * @param passwort a Textfield
     * @return success a boolean indicating the success of the login
     */
    public boolean login(TextField username, TextField passwort) {
        boolean success = true; //new ClientSession().login(username.getText(), passwort.getText());
        if (success) {
            AdvancedWarsApplication.getInstance().goToLobby();
        }
        return success;
    }
}
