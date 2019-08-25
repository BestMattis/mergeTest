package gameController.units;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import gameController.gameLoop.sprites.MovementPath;
import gameController.gameLoop.sprites.UnitSprite;
import gameScreen.GameFieldController;
import gameScreen.routing.GameRouting;
import gameScreen.routing.GameRoutingBuilder;
import javafx.scene.paint.Color;
import main.AdvancedWarsApplication;
import model.Field;
import model.Model;
import model.Unit;

public class UnitController {

    private Model model;
    private List<Field> reachable;
    private HashMap<String,Integer> damageMap;
    private boolean test;
    private Field lastTarget;

    public UnitController(boolean test, Model model){
        this.model=model;
        fillDamageMap();
        this.test = test;
    }

    public UnitController(Model model){
        this.model=model;
        fillDamageMap();
        this.test = false;
    }

    public void highlightReachableFields(Unit unit) {
	GameRouting routing = GameRoutingBuilder.createGameRouting(unit.getOccupiesField(), unit.getMp(), model);
	reachable = routing.getReachableFields(unit.getMp());
	System.out.println("reachable fields " + reachable);
	GameFieldController gameFieldController = AdvancedWarsApplication.getInstance().getGameScreenCon().getGameFieldController();
	reachable.forEach(f -> gameFieldController.highlightField(f));
    }

    public void reverseHighlightLastReachableFields() {
        if(reachable !=null){
            GameFieldController gameFieldController = AdvancedWarsApplication.getInstance().getGameScreenCon().getGameFieldController();
            reachable.forEach(f -> gameFieldController.reverseHighlightField(f));
        }

    }
    
    /**
     * Initiate moving a unit to the given x | y coordinates
     *
     * @param unit The unit that will move
     * @param sprite The sprite corresponding the unit
     * @param target field you want the unit to move to
     * @return True when the unit will move to the given field
     */
    public boolean moveUnit(Unit unit, UnitSprite sprite, Field target) {

        GameFieldController gameFieldController = AdvancedWarsApplication.getInstance().getGameScreenCon().getGameFieldController();

        lastTarget = target;

        if (sprite.getUnit() != unit) {
            System.out.println("Incorrect sprite for the unit");
            return false;
        }
        
        GameRouting routing = GameRoutingBuilder.createGameRouting(unit.getOccupiesField(), unit.getMp(), model);


        if (!routing.isReachableIn(target, unit.getMp())) {
            System.out.println("Can't move; Field is out of reach");

//            double magnifiedX = target.getPosX() * 32 * sprite.getPixelMagnification();
//            double magnifiedY = target.getPosY() * 32 * sprite.getPixelMagnification();
//
//            Effect eff = new ColorInput(magnifiedX, magnifiedY,
//                    32 * sprite.getPixelMagnification(), 32 * sprite.getPixelMagnification(), Color.RED);
//
//            sprite.getCanvas().setEffect(eff);
            gameFieldController.highlightFieldwithColor(target, new Color(1,0,0,0.5));
            return false;
        } else {
            // Reset feedback whenever successful
//            sprite.getCanvas().setEffect(null);
            gameFieldController.reverseHighlightField(lastTarget);
        }

        MovementPath mPath = routing.getPath(target);

        sprite.addMovementPath(mPath);
        unit.setPosX(target.getPosX()).setPosY(target.getPosY()).setOccupiesField(target).setMp(unit.getMp()-routing.getPathLength(target));
        sendPositionToServer(unit, mPath.getFields());

        //movement on Minimap
        AdvancedWarsApplication.getInstance().getGameScreenCon().getMinimap().moveUnitOnMinimap(unit, target);
        return true;
    }

    public void reverseHighlightLastTarget(){
        GameFieldController gameFieldController = AdvancedWarsApplication.getInstance().getGameScreenCon().getGameFieldController();
        if(lastTarget!=null){
            gameFieldController.reverseHighlightField(lastTarget);
        }
    }

    private void sendPositionToServer(Unit unit, List<Field> path) {
        Stream<String> fieldIDs = path.stream().map(f -> f.getId());
        model.getWebSocketComponent().moveUnitIngame(unit.getId(), fieldIDs.toArray(String[]::new));
    }

    /**
     * Attack a unit with another unit
     * @param attackingUnit attacker
     * @param attackTarget defender
     * @return true if possible and false if not
     */
    public boolean attackUnit(Unit attackingUnit, Unit attackTarget) {
        boolean canAttackTarget = false;
        boolean targetCanAttackBack = false;

        for (String s : attackingUnit.getCanAttack()) {
            if (s.equals(attackTarget.getType())) {
                canAttackTarget = true;
                break;
            }
        }

        for (String s : attackTarget.getCanAttack()) {
            if (s.equals(attackingUnit.getType())) {
                targetCanAttackBack = true;
                break;
            }
        }

        if (canAttackTarget) {
            damageCalculation(attackingUnit, attackTarget, canAttackTarget, targetCanAttackBack);

            if (targetCanAttackBack) {
                if (attackTarget.getHp() > 0) {
                    System.out.println(attackingUnit.getType() + " attacks " + attackTarget.getType()
                            + " but takes damage");
                } else {
                    System.out.println(attackingUnit.getType() + " attacks " + attackTarget.getType()
                            + " and destroys it before taking damage");
                }

            } else {
                System.out.println(attackingUnit.getType() + " attacks " + attackTarget.getType());
            }

        } else {
            System.out.println("Invalid attack target");
            return false;
        }

        if (attackingUnit.getHp() < 1) {
            removeUnitWhenDead(attackingUnit);
        }

        if (attackTarget.getHp() < 1) {
            removeUnitWhenDead(attackTarget);
        }

        sendAttackToServer(attackingUnit, attackTarget);
        return true;
    }

    /**
     * Returns a list of fields that can be attacked
     * @param unit to get start position.
     * @return ArrayList</Field> that contains all attackable fields
     */
    public ArrayList<Field> getAttackableFields(Unit unit) {
        ArrayList<Field> attackableFields = new ArrayList<>();

        Field field = unit.getOccupiesField();

        if (field == null) {
            System.out.println("No field was found at " + unit.getPosX() + "|" + unit.getPosY());
            return null;
        }

        recursiveWalkAllNeighbors(attackableFields, unit, field, unit.getMp()+1);

        return attackableFields;
    }

    /**
     * Fills the given list with reachable attack targets
     * @param toBeFilled ArrayList of attackable fields
     * @param unit that attacks
     * @param field current field selected
     * @param movementDistance the distance that still can be moved
     */
    private void recursiveWalkAllNeighbors(ArrayList<Field> toBeFilled, Unit unit, Field field, int movementDistance) {
        if (movementDistance < 1) {
            return;
        }
        for (Field f : field.getNeighbour()) {
            if(f.getOccupiedBy()!=null && f.getOccupiedBy().getPlayer()!=unit.getPlayer()){
                if(!toBeFilled.contains(f)){
                    toBeFilled.add(f);
                }
            } else if(f.getIsPassable()){
                recursiveWalkAllNeighbors(toBeFilled,unit,f,movementDistance-1);
            }
        }
    }

    /**
     * Removes the unit when its dead
     * @param unit unit to remove
     */
    private void removeUnitWhenDead(Unit unit) {
        unit.removeYou();
    }

    /**
     * Calculates the damage
     * @param attackingUnit attacker
     * @param attackTarget defender
     * @param canAttackTarget attack is able to attack
     * @param targetCanAttackBack defender is able to fight back
     */
    private void damageCalculation(Unit attackingUnit, Unit attackTarget, boolean canAttackTarget, boolean targetCanAttackBack) {
        if(canAttackTarget){
            int h = attackTarget.getHp();
            int f = damageMap.get(attackTarget.getOccupiesField().getType());
            int a = damageMap.get(attackingUnit.getType()+attackTarget.getType());

            int v = (h/10)*(f*10);
            int s = (a-v)/10;

            if(s<1){
                s=1;
            }

            int newHP = h-s;
            if(newHP<0){
                newHP = 0;
            }
            attackTarget.setHp(newHP);
            System.out.println("HP calculated");

            if(targetCanAttackBack && newHP>0){
                damageCalculation(attackTarget,attackingUnit,true,false);
            }
        }
    }

    /**
     * Sends attack command to the server
     * @param attackingUnit attacker
     * @param attackTarget defender
     */
    private void sendAttackToServer(Unit attackingUnit, Unit attackTarget) {
        if(!test){
            model.getWebSocketComponent().attackUnitIngame(attackingUnit.getId(), attackTarget.getId());
        }
    }

    /**
     * Fills the HashMap with the damage values
     */
    private void fillDamageMap(){
        if(damageMap == null) {
            damageMap = new HashMap<>();
        }
        damageMap.put("InfantryInfantry",55);
        damageMap.put("InfantryBazookaTrooper",45);
        damageMap.put("InfantryJeep",12);
        damageMap.put("InfantryLightTank",5);
        damageMap.put("InfantryHeavyTank",1);
        damageMap.put("InfantryChopper",0);
        damageMap.put("BazookaTrooperInfantry",0);
        damageMap.put("BazookaTrooperBazookaTrooper",0);
        damageMap.put("BazookaTrooperJeep",85);
        damageMap.put("BazookaTrooperLightTank",55);
        damageMap.put("BazookaTrooperHeavyTank",15);
        damageMap.put("BazookaTrooperChopper",55);
        damageMap.put("JeepInfantry",70);
        damageMap.put("JeepBazookaTrooper",65);
        damageMap.put("JeepJeep",35);
        damageMap.put("JeepLightTank",0);
        damageMap.put("JeepHeavyTank",0);
        damageMap.put("JeepChopper",0);
        damageMap.put("LightTankInfantry",75);
        damageMap.put("LightTankBazookaTrooper",70);
        damageMap.put("LightTankJeep",85);
        damageMap.put("LightTankLightTank",55);
        damageMap.put("LightTankHeavyTank",15);
        damageMap.put("LightTankChopper",0);
        damageMap.put("HeavyTankInfantry",105);
        damageMap.put("HeavyTankBazookaTrooper",95);
        damageMap.put("HeavyTankJeep",105);
        damageMap.put("HeavyTankLightTank",85);
        damageMap.put("HeavyTankHeavyTank",55);
        damageMap.put("HeavyTankChopper",75);
        damageMap.put("ChopperInfantry",75);
        damageMap.put("ChopperBazookaTrooper",75);
        damageMap.put("ChopperJeep",55);
        damageMap.put("ChopperLightTank",25);
        damageMap.put("ChopperHeavyTank",20);
        damageMap.put("ChopperChopper",0);
        damageMap.put("Grass",1);
        damageMap.put("Forest",3);
        damageMap.put("Mountain",4);
    }

}
