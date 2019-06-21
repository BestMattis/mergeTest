package syncCommunication.RESTExceptions;

@SuppressWarnings("serial")
public class TooManyRequestsPerSecondException extends Exception {
    public TooManyRequestsPerSecondException(String message) {
        super(message);
    }
}
