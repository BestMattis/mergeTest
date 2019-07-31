package syncCommunication.RESTExceptions;

@SuppressWarnings("serial")
public class GameLobbyCreationFailedException extends Exception {
    public GameLobbyCreationFailedException(String message) {
        super(message);
    }
}
