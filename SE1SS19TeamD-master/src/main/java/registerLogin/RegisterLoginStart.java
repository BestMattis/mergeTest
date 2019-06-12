package registerLogin;

/*
 * This Class is now only used for testing purposes
 */

/*
 * gets the console-Parameters and launches the application
 * @param args a Array of Strings
 */
public class RegisterLoginStart {
    public static void main(String[] args) {
        new Thread() {
            @Override
            public void run() {
                javafx.application.Application.launch(RegisterLogin.class);
            }
        }.start();
    }
}