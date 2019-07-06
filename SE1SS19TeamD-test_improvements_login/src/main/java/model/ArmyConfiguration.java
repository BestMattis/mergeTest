package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ArmyConfiguration  
{

   public static final String PROPERTY_name = "name";

   private String name;

   public String getName()
   {
      return name;
   }

   public ArmyConfiguration setName(String value)
   {
      if (value == null ? this.name != null : ! value.equals(this.name))
      {
         String oldValue = this.name;
         this.name = value;
         firePropertyChange("name", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_id = "id";

   private String id;

   public String getId()
   {
      return id;
   }

   public ArmyConfiguration setId(String value)
   {
      if (value == null ? this.id != null : ! value.equals(this.id))
      {
         String oldValue = this.id;
         this.id = value;
         firePropertyChange("id", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_player = "player";

   private Player player = null;

   public Player getPlayer()
   {
      return this.player;
   }

   public ArmyConfiguration setPlayer(Player value)
   {
      if (this.player != value)
      {
         Player oldValue = this.player;
         if (this.player != null)
         {
            this.player = null;
            oldValue.withoutArmyConfigurations(this);
         }
         this.player = value;
         if (value != null)
         {
            value.withArmyConfigurations(this);
         }
         firePropertyChange("player", oldValue, value);
      }
      return this;
   }



   public static final java.util.ArrayList<Unit> EMPTY_units = new java.util.ArrayList<Unit>()
   { @Override public boolean add(Unit value){ throw new UnsupportedOperationException("No direct add! Use xy.withUnits(obj)"); }};


   public static final String PROPERTY_units = "units";

   private java.util.ArrayList<Unit> units = null;

   public java.util.ArrayList<Unit> getUnits()
   {
      if (this.units == null)
      {
         return EMPTY_units;
      }

      return this.units;
   }

   public ArmyConfiguration withUnits(Object... value)
   {
      if(value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withUnits(i);
            }
         }
         else if (item instanceof Unit)
         {
            if (this.units == null)
            {
               this.units = new java.util.ArrayList<Unit>();
            }
            if ( ! this.units.contains(item))
            {
               this.units.add((Unit)item);
               ((Unit)item).setArmyConfiguration(this);
               firePropertyChange("units", null, item);
            }
         }
         else throw new IllegalArgumentException();
      }
      return this;
   }



   public ArmyConfiguration withoutUnits(Object... value)
   {
      if (this.units == null || value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withoutUnits(i);
            }
         }
         else if (item instanceof Unit)
         {
            if (this.units.contains(item))
            {
               this.units.remove((Unit)item);
               ((Unit)item).setArmyConfiguration(null);
               firePropertyChange("units", item, null);
            }
         }
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

      result.append(" ").append(this.getName());
      result.append(" ").append(this.getId());


      return result.substring(1);
   }

   public void removeYou()
   {
      this.setPlayer(null);

      this.withoutUnits(this.getUnits().clone());


   }


}