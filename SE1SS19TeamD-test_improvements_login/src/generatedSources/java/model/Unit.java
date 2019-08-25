package model;

import java.beans.PropertyChangeSupport;

import java.beans.PropertyChangeListener;

public class Unit 
{

   public static final String PROPERTY_id = "id";

   private String id;

   public String getId()
   {
      return id;
   }

   public Unit setId(String value)
   {
      if (value == null ? this.id != null : ! value.equals(this.id))
      {
         String oldValue = this.id;
         this.id = value;
         firePropertyChange("id", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_canAttack = "canAttack";

   private java.util.ArrayList<String> canAttack;

   public java.util.ArrayList<String> getCanAttack()
   {
      return canAttack;
   }

   public Unit setCanAttack(java.util.ArrayList<String> value)
   {
      if (value != this.canAttack)
      {
         java.util.ArrayList<String> oldValue = this.canAttack;
         this.canAttack = value;
         firePropertyChange("canAttack", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_type = "type";

   private String type;

   public String getType()
   {
      return type;
   }

   public Unit setType(String value)
   {
      if (value == null ? this.type != null : ! value.equals(this.type))
      {
         String oldValue = this.type;
         this.type = value;
         firePropertyChange("type", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_mp = "mp";

   private int mp;

   public int getMp()
   {
      return mp;
   }

   public Unit setMp(int value)
   {
      if (value != this.mp)
      {
         int oldValue = this.mp;
         this.mp = value;
         firePropertyChange("mp", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_hp = "hp";

   private int hp;

   public int getHp()
   {
      return hp;
   }

   public Unit setHp(int value)
   {
      if (value != this.hp)
      {
         int oldValue = this.hp;
         this.hp = value;
         firePropertyChange("hp", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_maxHp = "maxHp";

   private int maxHp;

   public int getMaxHp()
   {
      return maxHp;
   }

   public Unit setMaxHp(int value)
   {
      if (value != this.maxHp)
      {
         int oldValue = this.maxHp;
         this.maxHp = value;
         firePropertyChange("maxHp", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_maxMp = "maxMp";

   private int maxMp;

   public int getMaxMp()
   {
      return maxMp;
   }

   public Unit setMaxMp(int value)
   {
      if (value != this.maxMp)
      {
         int oldValue = this.maxMp;
         this.maxMp = value;
         firePropertyChange("maxMp", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_posX = "posX";

   private int posX;

   public int getPosX()
   {
      return posX;
   }

   public Unit setPosX(int value)
   {
      if (value != this.posX)
      {
         int oldValue = this.posX;
         this.posX = value;
         firePropertyChange("posX", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_posY = "posY";

   private int posY;

   public int getPosY()
   {
      return posY;
   }

   public Unit setPosY(int value)
   {
      if (value != this.posY)
      {
         int oldValue = this.posY;
         this.posY = value;
         firePropertyChange("posY", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_hasAttacked = "hasAttacked";

   private boolean hasAttacked;

   public boolean getHasAttacked()
   {
      return hasAttacked;
   }

   public Unit setHasAttacked(boolean value)
   {
      if (value != this.hasAttacked)
      {
         boolean oldValue = this.hasAttacked;
         this.hasAttacked = value;
         firePropertyChange("hasAttacked", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_selectedBy = "selectedBy";

   private Game selectedBy = null;

   public Game getSelectedBy()
   {
      return this.selectedBy;
   }

   public Unit setSelectedBy(Game value)
   {
      if (this.selectedBy != value)
      {
         Game oldValue = this.selectedBy;
         if (this.selectedBy != null)
         {
            this.selectedBy = null;
            oldValue.setSelectedUnit(null);
         }
         this.selectedBy = value;
         if (value != null)
         {
            value.setSelectedUnit(this);
         }
         firePropertyChange("selectedBy", oldValue, value);
      }
      return this;
   }



   public static final String PROPERTY_game = "game";

   private Game game = null;

   public Game getGame()
   {
      return this.game;
   }

   public Unit setGame(Game value)
   {
      if (this.game != value)
      {
         Game oldValue = this.game;
         if (this.game != null)
         {
            this.game = null;
            oldValue.withoutAllUnits(this);
         }
         this.game = value;
         if (value != null)
         {
            value.withAllUnits(this);
         }
         firePropertyChange("game", oldValue, value);
      }
      return this;
   }



   public static final String PROPERTY_player = "player";

   private Player player = null;

   public Player getPlayer()
   {
      return this.player;
   }

   public Unit setPlayer(Player value)
   {
      if (this.player != value)
      {
         Player oldValue = this.player;
         if (this.player != null)
         {
            this.player = null;
            oldValue.withoutCurrentUnits(this);
         }
         this.player = value;
         if (value != null)
         {
            value.withCurrentUnits(this);
         }
         firePropertyChange("player", oldValue, value);
      }
      return this;
   }



   public static final String PROPERTY_armyConfiguration = "armyConfiguration";

   private ArmyConfiguration armyConfiguration = null;

   public ArmyConfiguration getArmyConfiguration()
   {
      return this.armyConfiguration;
   }

   public Unit setArmyConfiguration(ArmyConfiguration value)
   {
      if (this.armyConfiguration != value)
      {
         ArmyConfiguration oldValue = this.armyConfiguration;
         if (this.armyConfiguration != null)
         {
            this.armyConfiguration = null;
            oldValue.withoutUnits(this);
         }
         this.armyConfiguration = value;
         if (value != null)
         {
            value.withUnits(this);
         }
         firePropertyChange("armyConfiguration", oldValue, value);
      }
      return this;
   }



   public static final String PROPERTY_occupiesField = "occupiesField";

   private Field occupiesField = null;

   public Field getOccupiesField()
   {
      return this.occupiesField;
   }

   public Unit setOccupiesField(Field value)
   {
      if (this.occupiesField != value)
      {
         Field oldValue = this.occupiesField;
         if (this.occupiesField != null)
         {
            this.occupiesField = null;
            oldValue.setOccupiedBy(null);
         }
         this.occupiesField = value;
         if (value != null)
         {
            value.setOccupiedBy(this);
         }
         firePropertyChange("occupiesField", oldValue, value);
      }
      return this;
   }



   protected PropertyChangeSupport listeners = null;

   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
   {
      if (listeners != null)
      {
         listeners.firePropertyChange(propertyName, oldValue, newValue);
         return true;
      }
      return false;
   }

   public boolean addPropertyChangeListener(PropertyChangeListener listener)
   {
      if (listeners == null)
      {
         listeners = new PropertyChangeSupport(this);
      }
      listeners.addPropertyChangeListener(listener);
      return true;
   }

   public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
   {
      if (listeners == null)
      {
         listeners = new PropertyChangeSupport(this);
      }
      listeners.addPropertyChangeListener(propertyName, listener);
      return true;
   }

   public boolean removePropertyChangeListener(PropertyChangeListener listener)
   {
      if (listeners != null)
      {
         listeners.removePropertyChangeListener(listener);
      }
      return true;
   }

   public boolean removePropertyChangeListener(String propertyName,PropertyChangeListener listener)
   {
      if (listeners != null)
      {
         listeners.removePropertyChangeListener(propertyName, listener);
      }
      return true;
   }

   @Override
   public String toString()
   {
      StringBuilder result = new StringBuilder();

      result.append(" ").append(this.getId());
      result.append(" ").append(this.getType());


      return result.substring(1);
   }

   public void removeYou()
   {
      this.setSelectedBy(null);
      this.setGame(null);
      this.setPlayer(null);
      this.setArmyConfiguration(null);
      this.setOccupiesField(null);

   }


}