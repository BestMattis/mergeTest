package model;

import java.beans.PropertyChangeSupport;

import java.beans.PropertyChangeListener;

public class ChatMessage 
{

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


   public static final String PROPERTY_game = "game";

   private Game game = null;

   public Game getGame()
   {
      return this.game;
   }

   public ChatMessage setGame(Game value)
   {
      if (this.game != value)
      {
         Game oldValue = this.game;
         if (this.game != null)
         {
            this.game = null;
            oldValue.withoutIngameMessages(this);
         }
         this.game = value;
         if (value != null)
         {
            value.withIngameMessages(this);
         }
         firePropertyChange("game", oldValue, value);
      }
      return this;
   }



   public static final String PROPERTY_receiver = "receiver";

   private Player receiver = null;

   public Player getReceiver()
   {
      return this.receiver;
   }

   public ChatMessage setReceiver(Player value)
   {
      if (this.receiver != value)
      {
         Player oldValue = this.receiver;
         if (this.receiver != null)
         {
            this.receiver = null;
            oldValue.withoutReceivedMessages(this);
         }
         this.receiver = value;
         if (value != null)
         {
            value.withReceivedMessages(this);
         }
         firePropertyChange("receiver", oldValue, value);
      }
      return this;
   }



   public static final String PROPERTY_sender = "sender";

   private Player sender = null;

   public Player getSender()
   {
      return this.sender;
   }

   public ChatMessage setSender(Player value)
   {
      if (this.sender != value)
      {
         Player oldValue = this.sender;
         if (this.sender != null)
         {
            this.sender = null;
            oldValue.withoutSentMessages(this);
         }
         this.sender = value;
         if (value != null)
         {
            value.withSentMessages(this);
         }
         firePropertyChange("sender", oldValue, value);
      }
      return this;
   }



   public static final String PROPERTY_app = "app";

   private App app = null;

   public App getApp()
   {
      return this.app;
   }

   public ChatMessage setApp(App value)
   {
      if (this.app != value)
      {
         App oldValue = this.app;
         if (this.app != null)
         {
            this.app = null;
            oldValue.withoutAllChatMessages(this);
         }
         this.app = value;
         if (value != null)
         {
            value.withAllChatMessages(this);
         }
         firePropertyChange("app", oldValue, value);
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

      result.append(" ").append(this.getMessage());
      result.append(" ").append(this.getChannel());
      result.append(" ").append(this.getDate());


      return result.substring(1);
   }

   public void removeYou()
   {
      this.setGame(null);
      this.setReceiver(null);
      this.setSender(null);
      this.setApp(null);

   }


}