package syncCommunication.RESTExceptions;

@SuppressWarnings("serial")
public class GameIdNotFoundException extends Exception {
    public GameIdNotFoundException(String message) {
        super(message);
    }
}
