package model;

import java.beans.PropertyChangeSupport;

import java.beans.PropertyChangeListener;

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


   public static final String PROPERTY_id = "id";

   private String id;

   public String getId()
   {
      return id;
   }

   public Player setId(String value)
   {
      if (value == null ? this.id != null : ! value.equals(this.id))
      {
         String oldValue = this.id;
         this.id = value;
         firePropertyChange("id", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_color = "color";

   private String color;

   public String getColor()
   {
      return color;
   }

   public Player setColor(String value)
   {
      if (value == null ? this.color != null : ! value.equals(this.color))
      {
         String oldValue = this.color;
         this.color = value;
         firePropertyChange("color", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_isReady = "isReady";

   private boolean isReady;

   public boolean getIsReady()
   {
      return isReady;
   }

   public Player setIsReady(boolean value)
   {
      if (value != this.isReady)
      {
         boolean oldValue = this.isReady;
         this.isReady = value;
         firePropertyChange("isReady", oldValue, value);
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


   public static final String PROPERTY_observer = "observer";

   private boolean observer;

   public boolean getObserver()
   {
      return observer;
   }

   public Player setObserver(boolean value)
   {
      if (value != this.observer)
      {
         boolean oldValue = this.observer;
         this.observer = value;
         firePropertyChange("observer", oldValue, value);
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



   public static final String PROPERTY_turnPlayerGame = "turnPlayerGame";

   private Game turnPlayerGame = null;

   public Game getTurnPlayerGame()
   {
      return this.turnPlayerGame;
   }

   public Player setTurnPlayerGame(Game value)
   {
      if (this.turnPlayerGame != value)
      {
         Game oldValue = this.turnPlayerGame;
         if (this.turnPlayerGame != null)
         {
            this.turnPlayerGame = null;
            oldValue.setTurnPlayer(null);
         }
         this.turnPlayerGame = value;
         if (value != null)
         {
            value.setTurnPlayer(this);
         }
         firePropertyChange("turnPlayerGame", oldValue, value);
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


   public static final String PROPERTY_currentArmyConfiguration = "currentArmyConfiguration";

   private ArmyConfiguration currentArmyConfiguration = null;

   public ArmyConfiguration getCurrentArmyConfiguration()
   {
      return this.currentArmyConfiguration;
   }

   public Player setCurrentArmyConfiguration(ArmyConfiguration value)
   {
      if (this.currentArmyConfiguration != value)
      {
         ArmyConfiguration oldValue = this.currentArmyConfiguration;
         if (this.currentArmyConfiguration != null)
         {
            this.currentArmyConfiguration = null;
            oldValue.setPlayer(null);
         }
         this.currentArmyConfiguration = value;
         if (value != null)
         {
            value.setPlayer(this);
         }
         firePropertyChange("currentArmyConfiguration", oldValue, value);
      }
      return this;
   }



   public static final java.util.ArrayList<Unit> EMPTY_currentUnits = new java.util.ArrayList<Unit>()
   { @Override public boolean add(Unit value){ throw new UnsupportedOperationException("No direct add! Use xy.withCurrentUnits(obj)"); }};


   public static final String PROPERTY_currentUnits = "currentUnits";

   private java.util.ArrayList<Unit> currentUnits = null;

   public java.util.ArrayList<Unit> getCurrentUnits()
   {
      if (this.currentUnits == null)
      {
         return EMPTY_currentUnits;
      }

      return this.currentUnits;
   }

   public Player withCurrentUnits(Object... value)
   {
      if(value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withCurrentUnits(i);
            }
         }
         else if (item instanceof Unit)
         {
            if (this.currentUnits == null)
            {
               this.currentUnits = new java.util.ArrayList<Unit>();
            }
            if ( ! this.currentUnits.contains(item))
            {
               this.currentUnits.add((Unit)item);
               ((Unit)item).setPlayer(this);
               firePropertyChange("currentUnits", null, item);
            }
         }
         else throw new IllegalArgumentException();
      }
      return this;
   }



   public Player withoutCurrentUnits(Object... value)
   {
      if (this.currentUnits == null || value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withoutCurrentUnits(i);
            }
         }
         else if (item instanceof Unit)
         {
            if (this.currentUnits.contains(item))
            {
               this.currentUnits.remove((Unit)item);
               ((Unit)item).setPlayer(null);
               firePropertyChange("currentUnits", item, null);
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
      result.append(" ").append(this.getColor());
      result.append(" ").append(this.getPassword());


      return result.substring(1);
   }

   public void removeYou()
   {
      this.setGame(null);
      this.setTurnPlayerGame(null);
      this.setApp(null);
      this.setMyApp(null);
      this.setCurrentArmyConfiguration(null);

      this.withoutReceivedMessages(this.getReceivedMessages().clone());


      this.withoutSentMessages(this.getSentMessages().clone());


      this.withoutArmyConfigurations(this.getArmyConfigurations().clone());


      this.withoutCurrentUnits(this.getCurrentUnits().clone());


   }


}