package model;

import java.beans.PropertyChangeSupport;

import java.beans.PropertyChangeListener;

public class Field 
{

   public static final String PROPERTY_isPassable = "isPassable";

   private boolean isPassable;

   public boolean getIsPassable()
   {
      return isPassable;
   }

   public Field setIsPassable(boolean value)
   {
      if (value != this.isPassable)
      {
         boolean oldValue = this.isPassable;
         this.isPassable = value;
         firePropertyChange("isPassable", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_posX = "posX";

   private int posX;

   public int getPosX()
   {
      return posX;
   }

   public Field setPosX(int value)
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

   public Field setPosY(int value)
   {
      if (value != this.posY)
      {
         int oldValue = this.posY;
         this.posY = value;
         firePropertyChange("posY", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_id = "id";

   private String id;

   public String getId()
   {
      return id;
   }

   public Field setId(String value)
   {
      if (value == null ? this.id != null : ! value.equals(this.id))
      {
         String oldValue = this.id;
         this.id = value;
         firePropertyChange("id", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_type = "type";

   private String type;

   public String getType()
   {
      return type;
   }

   public Field setType(String value)
   {
      if (value == null ? this.type != null : ! value.equals(this.type))
      {
         String oldValue = this.type;
         this.type = value;
         firePropertyChange("type", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_gameField = "gameField";

   private GameField gameField = null;

   public GameField getGameField()
   {
      return this.gameField;
   }

   public Field setGameField(GameField value)
   {
      if (this.gameField != value)
      {
         GameField oldValue = this.gameField;
         if (this.gameField != null)
         {
            this.gameField = null;
            oldValue.withoutFields(this);
         }
         this.gameField = value;
         if (value != null)
         {
            value.withFields(this);
         }
         firePropertyChange("gameField", oldValue, value);
      }
      return this;
   }



public static final java.util.ArrayList<Field> EMPTY_neighbour = new java.util.ArrayList<Field>()
   { @Override public boolean add(Field value){ throw new UnsupportedOperationException("No direct add! Use xy.withNeighbour(obj)"); }};


public static final String PROPERTY_neighbour = "neighbour";

private java.util.ArrayList<Field> neighbour = null;

public java.util.ArrayList<Field> getNeighbour()
   {
      if (this.neighbour == null)
      {
         return EMPTY_neighbour;
      }

      return this.neighbour;
   }

public Field withNeighbour(Object... value)
   {
      if(value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withNeighbour(i);
            }
         }
         else if (item instanceof Field)
         {
            if (this.neighbour == null)
            {
               this.neighbour = new java.util.ArrayList<Field>();
            }
            if ( ! this.neighbour.contains(item))
            {
               this.neighbour.add((Field)item);
               ((Field)item).withNeighbour(this);
               firePropertyChange("neighbour", null, item);
            }
         }
         else throw new IllegalArgumentException();
      }
      return this;
   }


public Field withoutNeighbour(Object... value)
   {
      if (this.neighbour == null || value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withoutNeighbour(i);
            }
         }
         else if (item instanceof Field)
         {
            if (this.neighbour.contains(item))
            {
               this.neighbour.remove((Field)item);
               ((Field)item).withoutNeighbour(this);
               firePropertyChange("neighbour", item, null);
            }
         }
      }
      return this;
   }


   public static final String PROPERTY_occupiedBy = "occupiedBy";

   private Unit occupiedBy = null;

   public Unit getOccupiedBy()
   {
      return this.occupiedBy;
   }

   public Field setOccupiedBy(Unit value)
   {
      if (this.occupiedBy != value)
      {
         Unit oldValue = this.occupiedBy;
         if (this.occupiedBy != null)
         {
            this.occupiedBy = null;
            oldValue.setOccupiesField(null);
         }
         this.occupiedBy = value;
         if (value != null)
         {
            value.setOccupiesField(this);
         }
         firePropertyChange("occupiedBy", oldValue, value);
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
      this.setGameField(null);
      this.setOccupiedBy(null);

      this.withoutNeighbour(this.getNeighbour().clone());


      this.withoutNeighbour(this.getNeighbour().clone());


   }


}