package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class GameField  
{

   public static final String PROPERTY_sizeX = "sizeX";

   private int sizeX;

   public int getSizeX()
   {
      return sizeX;
   }

   public GameField setSizeX(int value)
   {
      if (value != this.sizeX)
      {
         int oldValue = this.sizeX;
         this.sizeX = value;
         firePropertyChange("sizeX", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_sizeY = "sizeY";

   private int sizeY;

   public int getSizeY()
   {
      return sizeY;
   }

   public GameField setSizeY(int value)
   {
      if (value != this.sizeY)
      {
         int oldValue = this.sizeY;
         this.sizeY = value;
         firePropertyChange("sizeY", oldValue, value);
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
      this.setGame(null);

      this.withoutFields(this.getFields().clone());


   }







   public static final java.util.ArrayList<Field> EMPTY_fields = new java.util.ArrayList<Field>()
   { @Override public boolean add(Field value){ throw new UnsupportedOperationException("No direct add! Use xy.withFields(obj)"); }};


   public static final String PROPERTY_fields = "fields";

   private java.util.ArrayList<Field> fields = null;

   public java.util.ArrayList<Field> getFields()
   {
      if (this.fields == null)
      {
         return EMPTY_fields;
      }

      return this.fields;
   }

   public GameField withFields(Object... value)
   {
      if(value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withFields(i);
            }
         }
         else if (item instanceof Field)
         {
            if (this.fields == null)
            {
               this.fields = new java.util.ArrayList<Field>();
            }
            if ( ! this.fields.contains(item))
            {
               this.fields.add((Field)item);
               ((Field)item).setGameField(this);
               firePropertyChange("fields", null, item);
            }
         }
         else throw new IllegalArgumentException();
      }
      return this;
   }



   public GameField withoutFields(Object... value)
   {
      if (this.fields == null || value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withoutFields(i);
            }
         }
         else if (item instanceof Field)
         {
            if (this.fields.contains(item))
            {
               this.fields.remove((Field)item);
               ((Field)item).setGameField(null);
               firePropertyChange("fields", item, null);
            }
         }
      }
      return this;
   }






   public static final String PROPERTY_game = "game";

   private Game game = null;

   public Game getGame()
   {
      return this.game;
   }

   public GameField setGame(Game value)
   {
      if (this.game != value)
      {
         Game oldValue = this.game;
         if (this.game != null)
         {
            this.game = null;
            oldValue.setGameField(null);
         }
         this.game = value;
         if (value != null)
         {
            value.setGameField(this);
         }
         firePropertyChange("game", oldValue, value);
      }
      return this;
   }













}