package model;

import java.beans.PropertyChangeSupport;

import java.beans.PropertyChangeListener;

public class Unit  
{


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

   public void removeYou()
   {
      this.setArmyConfiguration(null);

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


   @Override
   public String toString()
   {
      StringBuilder result = new StringBuilder();

      result.append(" ").append(this.getId());


      return result.substring(1);
   }
}