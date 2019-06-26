package syncCommunication.RESTExceptions;

@SuppressWarnings("serial")
public class LoginFailedException extends Exception {
    public LoginFailedException(String message) {
        super(message);
    }
}
