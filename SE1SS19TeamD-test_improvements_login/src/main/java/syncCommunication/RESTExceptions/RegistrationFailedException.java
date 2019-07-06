package syncCommunication.RESTExceptions;

@SuppressWarnings("serial")
public class RegistrationFailedException extends RuntimeException {
    public RegistrationFailedException(String message) {
        super(message);
    }
}
