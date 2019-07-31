package syncCommunication;

import model.ArmyConfiguration;
import model.Unit;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import registerLogin.LoginRegisterTestUtils;
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
            uComm.logIn(LoginRegisterTestUtils.getTestUserName(), LoginRegisterTestUtils.getTestUserPassword());
        } catch (LoginFailedException e) {
            e.printStackTrace();
        }

        // Test getting all unit types, as well as a correct unit id
        ArrayList<JSONObject> allUnits = null;
        System.out.println("Get all available unit types");

        try {
            allUnits = aComm.getAllUnitTypes();
        } catch (LoginFailedException e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(allUnits);
        for (JSONObject jO : allUnits) {
            System.out.println(jO);
        }

        String correctUnitID = null;
        if (allUnits.size() > 0) {
            correctUnitID = allUnits.get(0).getString("id");
        } else {
            Assert.fail();
        }

        String correctArmyCreation = null;
        ArmyConfiguration armyConfig = new ArmyConfiguration()
                .setName("Test Army");
        String wrongArmyCount = null;
        System.out.println("Creating games: ");

        for (int i = 0; i < 10; ++i) {
            armyConfig.withUnits(new Unit()
                    .setId(correctUnitID));
        }

        try {
            correctArmyCreation = aComm.createArmyOnServer(armyConfig);
            wrongArmyCount = aComm.createArmyOnServer("Wrong unit count", new String[1]);

        } catch (ArmyCreationException | InvalidUnitIdException | LoginFailedException e) {
            Assert.assertTrue(e instanceof ArmyCreationException);
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
            Assert.assertTrue(e instanceof ArmyIdNotFoundException);
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
            Assert.assertTrue(e instanceof InvalidUnitIdException
                    || e instanceof ArmyCreationException);
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
                if (!aComm.deleteArmyOnServer(j.getString("id"))) {
                    armyMassDeletion = false;
                }
            }
            allArmiesAfter = aComm.getAllOwnedArmies();

            deletionWithoutID = aComm.deleteArmyOnServer("No id");

        } catch (LoginFailedException | ArmyIdNotFoundException e) {
            Assert.assertTrue(e instanceof ArmyIdNotFoundException);
        }

        Assert.assertTrue(armyMassDeletion);

        Assert.assertFalse(deletionWithoutID);

        Assert.assertNotNull(allArmiesBefore);
        Assert.assertNotNull(allArmiesAfter);

        Assert.assertNotEquals(allArmiesAfter, allArmiesBefore);
        Assert.assertTrue(allArmiesBefore.size() > 0);
        Assert.assertEquals(0, allArmiesAfter.size());
    }

    @Test
    public void testUpdatingArmies() {

        HttpRequests hr = new HttpRequests();
        SynchronousUserCommunicator uComm = new SynchronousUserCommunicator(hr);
        SynchronousArmyCommunicator aComm = new SynchronousArmyCommunicator(hr);

        try {
            uComm.logIn(LoginRegisterTestUtils.getTestUserName(), LoginRegisterTestUtils.getTestUserPassword());
        } catch (LoginFailedException e) {
            e.printStackTrace();
        }

        JSONObject armyNew = null;

        ArrayList<JSONObject> allUnits = null;
        try {
            allUnits = aComm.getAllUnitTypes();
        } catch (LoginFailedException e) {
            e.printStackTrace();
        }

        String originalUnit = null;
        String replacementUnit = null;
        if (allUnits != null && allUnits.size() > 1) {
            originalUnit = allUnits.get(0).getString("id");
            replacementUnit = allUnits.get(1).getString("id");
        } else {
            Assert.fail();
        }

        ArmyConfiguration armyConfig1 = new ArmyConfiguration()
                .setName("Army1");

        ArmyConfiguration armyConfig2 = new ArmyConfiguration()
                .setName("Army2");

        ArmyConfiguration armyConfigNew = new ArmyConfiguration();

        for (int i = 0; i < 10; ++i) {
            armyConfig1.withUnits(new Unit()
                    .setId(originalUnit));
        }
        for (int i = 0; i < 10; ++i) {
            armyConfig2.withUnits(new Unit()
                    .setId(replacementUnit));
        }

        try {

            for (Object o : aComm.getAllOwnedArmies()) {
                aComm.deleteArmyOnServer(((JSONObject) o).getString("id"));
            }

            armyConfig1.setId(aComm.createArmyOnServer(armyConfig1));
            armyConfig2.setId(armyConfig1.getId());
            aComm.updateArmyOnServer(armyConfig2);
            armyNew = aComm.getArmyByID(armyConfig1.getId());
        } catch (ArmyCreationException | InvalidUnitIdException
                | LoginFailedException | ArmyIdNotFoundException e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(armyNew);
        armyConfigNew.setName(armyNew.getString("name"));

        for (Object j : armyNew.getJSONArray("units")) {
            armyConfigNew.withUnits(new Unit().setId(((String) j)));
        }

        System.out.println(armyConfig1.getUnits());
        System.out.println(armyConfig2.getUnits());
        System.out.println(armyConfigNew.getUnits());

        for (int i = 0; i < 10; ++i) {
            Assert.assertNotEquals(armyConfig1.getUnits().get(i).getId(), armyConfig2.getUnits().get(i).getId());
            Assert.assertEquals(armyConfig2.getUnits().get(i).getId(), armyConfigNew.getUnits().get(i).getId());
        }


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
            System.out.println("createArmyOnServer() correctly threw LoginFailedException.");
        }

        try {
            getAllOwned = aComm.getAllOwnedArmies();
        } catch (LoginFailedException e) {
            System.out.println("getAllOwnedArmies() correctly threw LoginFailedException.");
        }

        try {
            getArmyById = aComm.getArmyByID("No IDea");
        } catch (LoginFailedException | ArmyIdNotFoundException e) {
            Assert.assertTrue(e instanceof LoginFailedException);
            System.out.println("getArmyByID() correctly threw LoginFailedException.");
        }

        try {
            updateArmy = aComm.updateArmyOnServer("No ID", "No Login", new String[10]);
        } catch (ArmyCreationException | LoginFailedException | InvalidUnitIdException e) {
            Assert.assertTrue(e instanceof LoginFailedException);
            System.out.println("updateArmyOnServer() correctly threw LoginFailedException.");
        }

        try {
            deleteArmy = aComm.deleteArmyOnServer("Nope, no ID");
        } catch (LoginFailedException | ArmyIdNotFoundException e) {
            Assert.assertTrue(e instanceof LoginFailedException);
            System.out.println("deleteArmyOnServer() correctly threw LoginFailedException.");
        }

        try {
            getAllUnits = aComm.getAllUnitTypes();
        } catch (LoginFailedException e) {
            System.out.println("getAllUnitTypes() correctly threw LoginFailedException.");
        }

        Assert.assertNull(createGame);
        Assert.assertNull(getAllOwned);
        Assert.assertNull(getArmyById);
        Assert.assertFalse(updateArmy);
        Assert.assertFalse(deleteArmy);
        Assert.assertNull(getAllUnits);
    }
}
