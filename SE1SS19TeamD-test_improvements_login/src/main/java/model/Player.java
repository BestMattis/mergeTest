package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Player  
{

   public static final String PROPERTY_name = "name";

   private String name;

   public String getName()
   {
      return name;
   }

   public Player setName(String value)
   {
      if (value == null ? this.name != null : ! value.equals(this.name))
      {
         String oldValue = this.name;
         this.name = value;
         firePropertyChange("name", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_password = "password";

   private String password;

   public String getPassword()
   {
      return password;
   }

   public Player setPassword(String value)
   {
      if (value == null ? this.password != null : ! value.equals(this.password))
      {
         String oldValue = this.password;
         this.password = value;
         firePropertyChange("password", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_game = "game";

   private Game game = null;

   public Game getGame()
   {
      return this.game;
   }

   public Player setGame(Game value)
   {
      if (this.game != value)
      {
         Game oldValue = this.game;
         if (this.game != null)
         {
            this.game = null;
            oldValue.withoutPlayers(this);
         }
         this.game = value;
         if (value != null)
         {
            value.withPlayers(this);
         }
         firePropertyChange("game", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_app = "app";

   private App app = null;

   public App getApp()
   {
      return this.app;
   }

   public Player setApp(App value)
   {
      if (this.app != value)
      {
         App oldValue = this.app;
         if (this.app != null)
         {
            this.app = null;
            oldValue.withoutAllPlayers(this);
         }
         this.app = value;
         if (value != null)
         {
            value.withAllPlayers(this);
         }
         firePropertyChange("app", oldValue, value);
      }
      return this;
   }



   public static final String PROPERTY_myApp = "myApp";

   private App myApp = null;

   public App getMyApp()
   {
      return this.myApp;
   }

   public Player setMyApp(App value)
   {
      if (this.myApp != value)
      {
         App oldValue = this.myApp;
         if (this.myApp != null)
         {
            this.myApp = null;
            oldValue.setCurrentPlayer(null);
         }
         this.myApp = value;
         if (value != null)
         {
            value.setCurrentPlayer(this);
         }
         firePropertyChange("myApp", oldValue, value);
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
      result.append(" ").append(this.getPassword());


      return result.substring(1);
   }

   public void removeYou()
   {
      this.setGame(null);
      this.setApp(null);
      this.setMyApp(null);

      this.withoutReceivedMessages(this.getReceivedMessages().clone());


      this.withoutSentMessages(this.getSentMessages().clone());


      this.withoutArmyConfigurations(this.getArmyConfigurations().clone());


   }


   public static final java.util.ArrayList<ArmyConfiguration> EMPTY_armyConfigurations = new java.util.ArrayList<ArmyConfiguration>()
   { @Override public boolean add(ArmyConfiguration value){ throw new UnsupportedOperationException("No direct add! Use xy.withArmyConfigurations(obj)"); }};


   public static final String PROPERTY_armyConfigurations = "armyConfigurations";

   private java.util.ArrayList<ArmyConfiguration> armyConfigurations = null;

   public java.util.ArrayList<ArmyConfiguration> getArmyConfigurations()
   {
      if (this.armyConfigurations == null)
      {
         return EMPTY_armyConfigurations;
      }

      return this.armyConfigurations;
   }

   public Player withArmyConfigurations(Object... value)
   {
      if(value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withArmyConfigurations(i);
            }
         }
         else if (item instanceof ArmyConfiguration)
         {
            if (this.armyConfigurations == null)
            {
               this.armyConfigurations = new java.util.ArrayList<ArmyConfiguration>();
            }
            if ( ! this.armyConfigurations.contains(item))
            {
               this.armyConfigurations.add((ArmyConfiguration)item);
               ((ArmyConfiguration)item).setPlayer(this);
               firePropertyChange("armyConfigurations", null, item);
            }
         }
         else throw new IllegalArgumentException();
      }
      return this;
   }



   public Player withoutArmyConfigurations(Object... value)
   {
      if (this.armyConfigurations == null || value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withoutArmyConfigurations(i);
            }
         }
         else if (item instanceof ArmyConfiguration)
         {
            if (this.armyConfigurations.contains(item))
            {
               this.armyConfigurations.remove((ArmyConfiguration)item);
               ((ArmyConfiguration)item).setPlayer(null);
               firePropertyChange("armyConfigurations", item, null);
            }
         }
      }
      return this;
   }


   public static final java.util.ArrayList<ChatMessage> EMPTY_receivedMessages = new java.util.ArrayList<ChatMessage>()
   { @Override public boolean add(ChatMessage value){ throw new UnsupportedOperationException("No direct add! Use xy.withReceivedMessages(obj)"); }};


   public static final String PROPERTY_receivedMessages = "receivedMessages";

   private java.util.ArrayList<ChatMessage> receivedMessages = null;

   public java.util.ArrayList<ChatMessage> getReceivedMessages()
   {
      if (this.receivedMessages == null)
      {
         return EMPTY_receivedMessages;
      }

      return this.receivedMessages;
   }

   public Player withReceivedMessages(Object... value)
   {
      if(value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withReceivedMessages(i);
            }
         }
         else if (item instanceof ChatMessage)
         {
            if (this.receivedMessages == null)
            {
               this.receivedMessages = new java.util.ArrayList<ChatMessage>();
            }
            if ( ! this.receivedMessages.contains(item))
            {
               this.receivedMessages.add((ChatMessage)item);
               ((ChatMessage)item).setReceiver(this);
               firePropertyChange("receivedMessages", null, item);
            }
         }
         else throw new IllegalArgumentException();
      }
      return this;
   }



   public Player withoutReceivedMessages(Object... value)
   {
      if (this.receivedMessages == null || value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withoutReceivedMessages(i);
            }
         }
         else if (item instanceof ChatMessage)
         {
            if (this.receivedMessages.contains(item))
            {
               this.receivedMessages.remove((ChatMessage)item);
               ((ChatMessage)item).setReceiver(null);
               firePropertyChange("receivedMessages", item, null);
            }
         }
      }
      return this;
   }


   public static final java.util.ArrayList<ChatMessage> EMPTY_sentMessages = new java.util.ArrayList<ChatMessage>()
   { @Override public boolean add(ChatMessage value){ throw new UnsupportedOperationException("No direct add! Use xy.withSentMessages(obj)"); }};


   public static final String PROPERTY_sentMessages = "sentMessages";

   private java.util.ArrayList<ChatMessage> sentMessages = null;

   public java.util.ArrayList<ChatMessage> getSentMessages()
   {
      if (this.sentMessages == null)
      {
         return EMPTY_sentMessages;
      }

      return this.sentMessages;
   }

   public Player withSentMessages(Object... value)
   {
      if(value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withSentMessages(i);
            }
         }
         else if (item instanceof ChatMessage)
         {
            if (this.sentMessages == null)
            {
               this.sentMessages = new java.util.ArrayList<ChatMessage>();
            }
            if ( ! this.sentMessages.contains(item))
            {
               this.sentMessages.add((ChatMessage)item);
               ((ChatMessage)item).setSender(this);
               firePropertyChange("sentMessages", null, item);
            }
         }
         else throw new IllegalArgumentException();
      }
      return this;
   }



   public Player withoutSentMessages(Object... value)
   {
      if (this.sentMessages == null || value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withoutSentMessages(i);
            }
         }
         else if (item instanceof ChatMessage)
         {
            if (this.sentMessages.contains(item))
            {
               this.sentMessages.remove((ChatMessage)item);
               ((ChatMessage)item).setSender(null);
               firePropertyChange("sentMessages", item, null);
            }
         }
      }
      return this;
   }


}