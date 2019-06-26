package syncCommunication.RESTExceptions;

@SuppressWarnings("serial")
public class GameIdNotFoundException extends RuntimeException {
    public GameIdNotFoundException(String message) {
        super(message);
    }
}
