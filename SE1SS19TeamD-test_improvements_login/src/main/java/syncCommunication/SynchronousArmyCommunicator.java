package syncCommunication;

import model.ArmyConfiguration;
import model.Unit;
import org.json.JSONObject;
import syncCommunication.RESTExceptions.ArmyCreationException;
import syncCommunication.RESTExceptions.ArmyIdNotFoundException;
import syncCommunication.RESTExceptions.InvalidUnitIdException;
import syncCommunication.RESTExceptions.LoginFailedException;

import java.util.ArrayList;

public class SynchronousArmyCommunicator extends SynchronousCommunicator {

    private HTTPArmyHandler aHandler;

    /**
     * @param httpReq Used to assign the user. Same HttpRequests leads to the same logged in user.
     */
    public SynchronousArmyCommunicator(HttpRequests httpReq) {
        super(httpReq);
        aHandler = new HTTPArmyHandler(httpReq);
    }

    private String[] idArrayFromUnits(ArrayList<Unit> units) {
        String[] unitIDs = new String[10];
        for (int i = 0; i < unitIDs.length; ++i) {
            unitIDs[i] = units.get(i).getId();
        }
        return unitIDs;
    }

    /**
     * @param armyConfiguration An Object of the data model
     * @return String of the created army's id on the server
     * @throws ArmyCreationException  If the army's size or other information was incorrect
     * @throws InvalidUnitIdException If an unit had an incorrect id
     * @throws LoginFailedException   If the user was not logged in
     */
    public String createArmyOnServer(ArmyConfiguration armyConfiguration)
            throws ArmyCreationException, InvalidUnitIdException, LoginFailedException {

        if (armyConfiguration.getUnits().size() != 10) {
            throw new ArmyCreationException("An army must have ten units");
        }

        return createArmyOnServer(armyConfiguration.getName(), idArrayFromUnits(armyConfiguration.getUnits()));
    }


    /**
     * @param name  Name of the army
     * @param units Array of size 10 of unit ids
     * @return String of the created army's id on the server
     * @throws ArmyCreationException  If the army's size or other information was incorrect
     * @throws InvalidUnitIdException If an unit had an incorrect id
     * @throws LoginFailedException   If the user was not logged in
     */
    public String createArmyOnServer(String name, String[] units)
            throws ArmyCreationException, InvalidUnitIdException, LoginFailedException {

        return aHandler.createArmy(aHandler.getUserKey(), name, units);
    }

    /**
     * @return An ArrayList of JSONObjects of all armies registered on the server
     * @throws LoginFailedException if the user was not logged in
     */
    public ArrayList<JSONObject> getAllOwnedArmies()
            throws LoginFailedException {

        return aHandler.getOwnedArmies(aHandler.getUserKey());
    }

    /**
     * @param id The id of an already registered army on the server
     * @return A JSONObject of the army corresponding to the id
     * @throws LoginFailedException    If the user was not logged in
     * @throws ArmyIdNotFoundException If the id did not correspond to an army
     */
    public JSONObject getArmyByID(String id)
            throws LoginFailedException, ArmyIdNotFoundException {

        return aHandler.getArmyById(aHandler.getUserKey(), id);
    }

    /**
     * @param armyConfiguration Object of the data model. It has to have an id set
     * @return boolean true if successful else false
     * @throws ArmyCreationException  If the army's size or other information was incorrect
     * @throws InvalidUnitIdException If an unit had an incorrect id
     * @throws LoginFailedException   If the user was not logged in
     */
    public boolean updateArmyOnServer(ArmyConfiguration armyConfiguration)
            throws ArmyCreationException, LoginFailedException, InvalidUnitIdException {

        if (armyConfiguration.getId() == null) {
            throw new ArmyCreationException("ID cannot be null. Create army on the server first.");
        }

        if (armyConfiguration.getUnits().size() != 10) {
            throw new ArmyCreationException("An army must have ten units");
        }

        return aHandler.updateArmy(aHandler.getUserKey(), armyConfiguration.getId(),
                armyConfiguration.getName(),
                idArrayFromUnits(armyConfiguration.getUnits()));
    }

    /**
     * @param id    Of the army on the server
     * @param name  New name for the army
     * @param units New set of units for the army
     * @return boolean true if successful else false
     * @throws ArmyCreationException  If the army's size or other information was incorrect
     * @throws InvalidUnitIdException If an unit had an incorrect id
     * @throws LoginFailedException   If the user was not logged in
     */
    public boolean updateArmyOnServer(String id, String name, String[] units)
            throws ArmyCreationException, LoginFailedException, InvalidUnitIdException {

        return aHandler.updateArmy(aHandler.getUserKey(), id, name, units);
    }

    /**
     * @param id Of the army on the server
     * @return boolean true if successful else false
     * @throws LoginFailedException    If the user was not logged in
     * @throws ArmyIdNotFoundException If the id did not correspond to an army
     */
    public boolean deleteArmyOnServer(String id) throws LoginFailedException, ArmyIdNotFoundException {
        return aHandler.deleteArmy(aHandler.getUserKey(), id);
    }

    /**
     * @return All possible unit types usable in the game
     * @throws LoginFailedException If the user was not logged in
     */
    public ArrayList<JSONObject> getAllUnitTypes() throws LoginFailedException {
        return aHandler.getUnits(aHandler.getUserKey());
    }

    public HTTPArmyHandler getArmyHandler() {
        return aHandler;
    }

    public String getUserKey() {
        return aHandler.getUserKey();
    }
}
