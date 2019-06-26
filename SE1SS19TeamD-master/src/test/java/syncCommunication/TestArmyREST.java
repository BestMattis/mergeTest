package syncCommunication;

import model.ArmyConfiguration;
import model.BazookaTrooper;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import syncCommunication.RESTExceptions.ArmyCreationException;
import syncCommunication.RESTExceptions.ArmyIdNotFoundException;
import syncCommunication.RESTExceptions.InvalidUnitIdException;
import syncCommunication.RESTExceptions.LoginFailedException;

import java.util.ArrayList;

public class TestArmyREST {

    @Test
    public void testArmyHandler() {
        HttpRequests hr = new HttpRequests();
        SynchronousUserCommunicator uComm = new SynchronousUserCommunicator(hr);
        SynchronousArmyCommunicator aComm = new SynchronousArmyCommunicator(hr);

        HTTPArmyHandler aHandler = aComm.getArmyHandler();
        Assert.assertNotNull(aHandler);

        try {
            uComm.logIn("katanari", "Password");
        } catch (LoginFailedException e) {
            e.printStackTrace();
        }

        String correctArmyCreation = null;
        ArmyConfiguration armyConfig = new ArmyConfiguration()
                .setName("Test Army");
        String wrongArmyCount = null;
        System.out.println("Creating games: ");

        for (int i = 0; i < 10; ++i) {
            armyConfig.withUnits(new BazookaTrooper()
                    .setId("5cc051bd62083600017db3b7"));
        }

        try {
            correctArmyCreation = aComm.createArmyOnServer(armyConfig);
            wrongArmyCount = aComm.createArmyOnServer("Wrong unit count", new String[1]);

        } catch (ArmyCreationException | InvalidUnitIdException | LoginFailedException e) {
            Assert.assertTrue(e instanceof ArmyCreationException);
            e.printStackTrace();
        }

        Assert.assertNotNull(correctArmyCreation);
        Assert.assertNull(wrongArmyCount);

        JSONObject armyByCorrectId = null;
        JSONObject armyByIncorrectId = null;
        System.out.println("Getting armies by IDs");

        try {
            armyByCorrectId = aComm.getArmyByID(correctArmyCreation);
            armyByIncorrectId = aComm.getArmyByID("Incorrect");

        } catch (LoginFailedException | ArmyIdNotFoundException e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(armyByCorrectId);
        Assert.assertNull(armyByIncorrectId);

        boolean updateCorrect = false;
        boolean updateIncorrect = false;
        armyConfig.setId(correctArmyCreation);
        System.out.println("Updating armies: ");

        try {
            updateCorrect = aComm.updateArmyOnServer(armyConfig);
            updateIncorrect = aComm.updateArmyOnServer("No ID",
                    "Wrong Army", new String[10]);

        } catch (ArmyCreationException | LoginFailedException | InvalidUnitIdException e) {
            e.printStackTrace();
        }

        Assert.assertTrue(updateCorrect);
        Assert.assertFalse(updateIncorrect);

        ArrayList<JSONObject> allArmiesBefore = null;
        ArrayList<JSONObject> allArmiesAfter = null;

        boolean armyMassDeletion = true;
        boolean deletionWithoutID = false;
        System.out.println("Getting all games and deleting them: ");

        try {
            allArmiesBefore = aComm.getAllOwnedArmies();
            for (JSONObject j : allArmiesBefore) {
                if(!aComm.deleteArmyOnServer(j.getString("id"))) {
                    armyMassDeletion = false;
                }
            }
            allArmiesAfter = aComm.getAllOwnedArmies();

            deletionWithoutID = aComm.deleteArmyOnServer("No id");

        } catch (LoginFailedException | ArmyIdNotFoundException e) {
            e.printStackTrace();
        }

        Assert.assertTrue(armyMassDeletion);

        Assert.assertFalse(deletionWithoutID);

        Assert.assertNotNull(allArmiesBefore);
        Assert.assertNotNull(allArmiesAfter);

        Assert.assertNotEquals(allArmiesAfter, allArmiesBefore);
        Assert.assertTrue(allArmiesBefore.size() > 0);
        Assert.assertEquals(0, allArmiesAfter.size());

        ArrayList<JSONObject> allUnits = null;
        System.out.println("Get all available unit types");

        try {
            allUnits = aComm.getAllUnitTypes();
        } catch (LoginFailedException e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(allUnits);
    }

    @Test
    public void noLoginTests() {
        HttpRequests hr = new HttpRequests();
        SynchronousArmyCommunicator aComm = new SynchronousArmyCommunicator(hr);

        String createGame = null;
        ArrayList<JSONObject> getAllOwned = null;
        JSONObject getArmyById = null;
        boolean updateArmy = false;
        boolean deleteArmy = false;
        ArrayList<JSONObject> getAllUnits = null;

        try {
            createGame = aComm.createArmyOnServer("Name", new String[10]);
        } catch (ArmyCreationException | InvalidUnitIdException | LoginFailedException e) {
            Assert.assertTrue(e instanceof LoginFailedException);
            //e.printStackTrace();
        }

        try {
            getAllOwned = aComm.getAllOwnedArmies();
        } catch (LoginFailedException e) {
            //e.printStackTrace();
        }

        try {
            getArmyById = aComm.getArmyByID("No IDea");
        } catch (LoginFailedException | ArmyIdNotFoundException e) {
            Assert.assertTrue(e instanceof LoginFailedException);
            //e.printStackTrace();
        }

        try {
            updateArmy = aComm.updateArmyOnServer("No ID", "No Login", new String[10]);
        } catch (ArmyCreationException | LoginFailedException | InvalidUnitIdException e) {
            Assert.assertTrue(e instanceof LoginFailedException);
            //e.printStackTrace();
        }

        try {
            deleteArmy = aComm.deleteArmyOnServer("Nope, no ID");
        } catch (LoginFailedException | ArmyIdNotFoundException e) {
            Assert.assertTrue(e instanceof LoginFailedException);
            //e.printStackTrace();
        }

        try {
            getAllUnits = aComm.getAllUnitTypes();
        } catch (LoginFailedException e) {
            //e.printStackTrace();
        }

        Assert.assertNull(createGame);
        Assert.assertNull(getAllOwned);
        Assert.assertNull(getArmyById);
        Assert.assertFalse(updateArmy);
        Assert.assertFalse(deleteArmy);
        Assert.assertNull(getAllUnits);
    }
}
