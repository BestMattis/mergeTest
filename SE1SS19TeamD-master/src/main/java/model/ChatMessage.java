package model;

import java.beans.PropertyChangeSupport;

import java.beans.PropertyChangeListener;

public class ChatMessage  
{


   public static final String PROPERTY_channel = "channel";

   private String channel;

   public String getChannel()
   {
      return channel;
   }

   public ChatMessage setChannel(String value)
   {
      if (value == null ? this.channel != null : ! value.equals(this.channel))
      {
         String oldValue = this.channel;
         this.channel = value;
         firePropertyChange("channel", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_date = "date";

   private String date;

   public String getDate()
   {
      return date;
   }

   public ChatMessage setDate(String value)
   {
      if (value == null ? this.date != null : ! value.equals(this.date))
      {
         String oldValue = this.date;
         this.date = value;
         firePropertyChange("date", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_player = "player";

   private Player player = null;

   public Player getPlayer()
   {
      return this.player;
   }

   public ChatMessage setPlayer(Player value)
   {
      if (this.player != value)
      {
         Player oldValue = this.player;
         if (this.player != null)
         {
            this.player = null;
            oldValue.withoutMessages(this);
         }
         this.player = value;
         if (value != null)
         {
            value.withMessages(this);
         }
         firePropertyChange("player", oldValue, value);
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
      this.setPlayer(null);

   }


   public static final String PROPERTY_message = "message";

   private String message;

   public String getMessage()
   {
      return message;
   }

   public ChatMessage setMessage(String value)
   {
      if (value == null ? this.message != null : ! value.equals(this.message))
      {
         String oldValue = this.message;
         this.message = value;
         firePropertyChange("message", oldValue, value);
      }
      return this;
   }


   @Override
   public String toString()
   {
      StringBuilder result = new StringBuilder();

      result.append(" ").append(this.getMessage());
      result.append(" ").append(this.getChannel());
      result.append(" ").append(this.getDate());


      return result.substring(1);
   }

}