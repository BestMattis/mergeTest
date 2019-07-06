package syncCommunication.RESTExceptions;

@SuppressWarnings("serial")
public class GameLobbyCreationFailedException extends RuntimeException {
    public GameLobbyCreationFailedException(String message) {
        super(message);
    }
}
