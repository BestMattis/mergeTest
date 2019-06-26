package syncCommunication.RESTExceptions;

@SuppressWarnings("serial")
public class TooManyRequestsPerSecondException extends RuntimeException {
    public TooManyRequestsPerSecondException(String message) {
        super(message);
    }
}
