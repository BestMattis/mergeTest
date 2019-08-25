package syncCommunication;

import org.json.JSONArray;
import org.json.JSONObject;
import syncCommunication.RESTExceptions.ArmyCreationException;
import syncCommunication.RESTExceptions.ArmyIdNotFoundException;
import syncCommunication.RESTExceptions.InvalidUnitIdException;
import syncCommunication.RESTExceptions.LoginFailedException;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class HTTPArmyHandler {

    private final int ARMY_SIZE = 10;

    private HttpRequests hr;
    private ArrayList<JSONObject> unitTypes = null;

    HTTPArmyHandler(HttpRequests httpReq) {
        hr = httpReq;
    }

    /*
     * A method to check if at least one incorrect unit id is int he array.
     * Returns true if there is.
     * It relies on having grabbed all unit types at least once, if not
     * it will grab them itself.
     */
    private boolean checkIfIncorrectUnitIDs(String[] units) throws LoginFailedException {

        if (unitTypes == null) {
            getUnits(getUserKey());
        }

        ArrayList<String> idList = new ArrayList<>();
        for (JSONObject jO : unitTypes) {
            idList.add(jO.getString("id"));
        }

        for (String s : units) {
            if (!idList.contains(s)) {

                return true;
            }
        }

        return false;
    }

    /*
     * Saves a armyConfiguration on the server. Up to 7 max and only with
     * exactly 10 units.
     * Returns the String of the id, once it was saved correctly online.
     * Throws ArmyCreationException, LoginFailedException, InvalidUnitIdException
     */
    String createArmy(String userKey, String name, String[] units)
            throws ArmyCreationException, LoginFailedException, InvalidUnitIdException {

        if (units.length != ARMY_SIZE) {
            throw new ArmyCreationException("An army must have ten units");
        }

        if (checkIfIncorrectUnitIDs(units)) {
            throw new InvalidUnitIdException("There was an unit with an incorrect ID");
        }

        JSONObject armyData = new JSONObject();
        armyData.put("name", name);
        armyData.put("units", units);

        try {
            JSONObject response = hr.postJsonAs(hr.getUserKey(), armyData, "/army");
            if (response.getString("status").equals("success")) {
                return response.getJSONObject("data").getString("id");
            } else {
                throw new ArmyCreationException(response.getString("message"));
            }

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     * Grabs all armies associated with the user(-Key).
     * Returns a JSONObject ArrayList of them all.
     * Throws LoginFailedException
     */
    ArrayList<JSONObject> getOwnedArmies(String userKey) throws LoginFailedException {
        try {
            JSONObject response = hr.getJsonAsUser(getUserKey(), "/army");

            if (response.getString("status").equals("success")) {
                JSONArray jArray = response.getJSONArray("data");
                ArrayList<JSONObject> userList = new ArrayList<>();

                if (jArray != null) {
                    for (int i = 0; i < jArray.length(); ++i) {
                        userList.add((JSONObject) jArray.get(i));
                    }
                    return userList;

                } else {
                    return null;
                }
            } else {
                throw new LoginFailedException(response.getString("message"));
            }

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * Gets you exactly your army to the given id.
     * Returns the JSONObject of that army.
     * Throws ArmyIdNotFoundException, LoginFailedException
     */
    JSONObject getArmyById(String userKey, String id) throws ArmyIdNotFoundException, LoginFailedException {

        try {
            JSONObject response = hr.getJsonAsUser(userKey, "/army/" + id);

            if (response.getString("status").equals("success")) {
                return response.getJSONObject("data");

            } else {
                String error = response.getString("message");

                if (error.equals("Log in first")) {
                    throw new LoginFailedException(error);
                }
                throw new ArmyIdNotFoundException(error);
            }

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    /*
     * Updates an army's name or its units by the id given.
     * Returns true if the update was successful.
     * Throws ArmyCreationException, LoginFailedException, InvalidUnitIdException
     */
    boolean updateArmy(String userKey, String id, String name, String[] units)
            throws ArmyCreationException, LoginFailedException, InvalidUnitIdException {

        if (units.length != ARMY_SIZE) {
            throw new ArmyCreationException("An army must have ten units");
        }

        if (checkIfIncorrectUnitIDs(units)) {
            throw new InvalidUnitIdException("There was an unit with an incorrect ID");
        }

        JSONObject armyData = new JSONObject();
        armyData.put("name", name);
        armyData.put("units", units);

        try {
            JSONObject response = hr.putJsonAs(userKey, armyData, "/army/" + id);
            if (response.getString("status").equals("success")) {
                return true;

            } else {
                String error = response.getString("message");

                if (error.equals("Log in first")) {
                    throw new LoginFailedException(error);
                }
                throw new ArmyCreationException(error);
            }

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    /*
     * Deletes the army.
     * Returns true if it was deleted successfully.
     * Throws LoginFailedException, ArmyIdNotFoundException
     */
    boolean deleteArmy(String userKey, String id) throws LoginFailedException, ArmyIdNotFoundException {

        try {
            JSONObject response = hr.deleteAsUser(userKey, "/army/" + id);
            if (response.getString("status").equals("success")) {
                return true;

            } else {
                String error = response.getString("message");

                if (error.equals("Log in first")) {
                    throw new LoginFailedException(error);
                }
                throw new ArmyIdNotFoundException(error);
            }

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    /*
     * Asks the server what units are allowed and recognized.
     * Returns an ArrayList of JSONObjects of all possible units.
     * Throws LoginFailedException
     */
    ArrayList<JSONObject> getUnits(String userKey) throws LoginFailedException {
        try {
            JSONObject response = hr.getJsonAsUser(getUserKey(), "/army/units");

            if (response.getString("status").equals("success")) {
                JSONArray jArray = response.getJSONArray("data");
                ArrayList<JSONObject> userList = new ArrayList<>();

                if (jArray != null) {
                    for (int i = 0; i < jArray.length(); ++i) {
                        userList.add((JSONObject) jArray.get(i));
                    }
                    if (unitTypes == null) {
                        unitTypes = userList;
                    }
                    return userList;

                } else {
                    return null;
                }
            } else {
                throw new LoginFailedException(response.getString("message"));
            }

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    String getUserKey() {
        return hr.getUserKey();
    }
}
