package syncCommunication.RESTExceptions;

@SuppressWarnings("serial")
public class RegistrationFailedException extends Exception {
    public RegistrationFailedException(String message) {
        super(message);
    }
}
