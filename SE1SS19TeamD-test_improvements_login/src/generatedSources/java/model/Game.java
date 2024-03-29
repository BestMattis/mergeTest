package model;

import java.beans.PropertyChangeSupport;

import java.beans.PropertyChangeListener;

public class Game 
{

   public static final String PROPERTY_name = "name";

   private String name;

   public String getName()
   {
      return name;
   }

   public Game setName(String value)
   {
      if (value == null ? this.name != null : ! value.equals(this.name))
      {
         String oldValue = this.name;
         this.name = value;
         firePropertyChange("name", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_capacity = "capacity";

   private int capacity;

   public int getCapacity()
   {
      return capacity;
   }

   public Game setCapacity(int value)
   {
      if (value != this.capacity)
      {
         int oldValue = this.capacity;
         this.capacity = value;
         firePropertyChange("capacity", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_gameId = "gameId";

   private String gameId;

   public String getGameId()
   {
      return gameId;
   }

   public Game setGameId(String value)
   {
      if (value == null ? this.gameId != null : ! value.equals(this.gameId))
      {
         String oldValue = this.gameId;
         this.gameId = value;
         firePropertyChange("gameId", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_joinedPlayers = "joinedPlayers";

   private int joinedPlayers;

   public int getJoinedPlayers()
   {
      return joinedPlayers;
   }

   public Game setJoinedPlayers(int value)
   {
      if (value != this.joinedPlayers)
      {
         int oldValue = this.joinedPlayers;
         this.joinedPlayers = value;
         firePropertyChange("joinedPlayers", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_activePlayer = "activePlayer";

   private String activePlayer;

   public String getActivePlayer()
   {
      return activePlayer;
   }

   public Game setActivePlayer(String value)
   {
      if (value == null ? this.activePlayer != null : ! value.equals(this.activePlayer))
      {
         String oldValue = this.activePlayer;
         this.activePlayer = value;
         firePropertyChange("activePlayer", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_currentPhase = "currentPhase";

   private String currentPhase;

   public String getCurrentPhase()
   {
      return currentPhase;
   }

   public Game setCurrentPhase(String value)
   {
      if (value == null ? this.currentPhase != null : ! value.equals(this.currentPhase))
      {
         String oldValue = this.currentPhase;
         this.currentPhase = value;
         firePropertyChange("currentPhase", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_started = "started";

   private boolean started;

   public boolean getStarted()
   {
      return started;
   }

   public Game setStarted(boolean value)
   {
      if (value != this.started)
      {
         boolean oldValue = this.started;
         this.started = value;
         firePropertyChange("started", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_winner = "winner";

   private String winner;

   public String getWinner()
   {
      return winner;
   }

   public Game setWinner(String value)
   {
      if (value == null ? this.winner != null : ! value.equals(this.winner))
      {
         String oldValue = this.winner;
         this.winner = value;
         firePropertyChange("winner", oldValue, value);
      }
      return this;
   }


   public static final java.util.ArrayList<Player> EMPTY_players = new java.util.ArrayList<Player>()
   { @Override public boolean add(Player value){ throw new UnsupportedOperationException("No direct add! Use xy.withPlayers(obj)"); }};


   public static final String PROPERTY_players = "players";

   private java.util.ArrayList<Player> players = null;

   public java.util.ArrayList<Player> getPlayers()
   {
      if (this.players == null)
      {
         return EMPTY_players;
      }

      return this.players;
   }

   public Game withPlayers(Object... value)
   {
      if(value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withPlayers(i);
            }
         }
         else if (item instanceof Player)
         {
            if (this.players == null)
            {
               this.players = new java.util.ArrayList<Player>();
            }
            if ( ! this.players.contains(item))
            {
               this.players.add((Player)item);
               ((Player)item).setGame(this);
               firePropertyChange("players", null, item);
            }
         }
         else throw new IllegalArgumentException();
      }
      return this;
   }



   public Game withoutPlayers(Object... value)
   {
      if (this.players == null || value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withoutPlayers(i);
            }
         }
         else if (item instanceof Player)
         {
            if (this.players.contains(item))
            {
               this.players.remove((Player)item);
               ((Player)item).setGame(null);
               firePropertyChange("players", item, null);
            }
         }
      }
      return this;
   }


   public static final java.util.ArrayList<ChatMessage> EMPTY_ingameMessages = new java.util.ArrayList<ChatMessage>()
   { @Override public boolean add(ChatMessage value){ throw new UnsupportedOperationException("No direct add! Use xy.withIngameMessages(obj)"); }};


   public static final String PROPERTY_ingameMessages = "ingameMessages";

   private java.util.ArrayList<ChatMessage> ingameMessages = null;

   public java.util.ArrayList<ChatMessage> getIngameMessages()
   {
      if (this.ingameMessages == null)
      {
         return EMPTY_ingameMessages;
      }

      return this.ingameMessages;
   }

   public Game withIngameMessages(Object... value)
   {
      if(value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withIngameMessages(i);
            }
         }
         else if (item instanceof ChatMessage)
         {
            if (this.ingameMessages == null)
            {
               this.ingameMessages = new java.util.ArrayList<ChatMessage>();
            }
            if ( ! this.ingameMessages.contains(item))
            {
               this.ingameMessages.add((ChatMessage)item);
               ((ChatMessage)item).setGame(this);
               firePropertyChange("ingameMessages", null, item);
            }
         }
         else throw new IllegalArgumentException();
      }
      return this;
   }



   public Game withoutIngameMessages(Object... value)
   {
      if (this.ingameMessages == null || value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withoutIngameMessages(i);
            }
         }
         else if (item instanceof ChatMessage)
         {
            if (this.ingameMessages.contains(item))
            {
               this.ingameMessages.remove((ChatMessage)item);
               ((ChatMessage)item).setGame(null);
               firePropertyChange("ingameMessages", item, null);
            }
         }
      }
      return this;
   }


   public static final String PROPERTY_selectedUnit = "selectedUnit";

   private Unit selectedUnit = null;

   public Unit getSelectedUnit()
   {
      return this.selectedUnit;
   }

   public Game setSelectedUnit(Unit value)
   {
      if (this.selectedUnit != value)
      {
         Unit oldValue = this.selectedUnit;
         if (this.selectedUnit != null)
         {
            this.selectedUnit = null;
            oldValue.setSelectedBy(null);
         }
         this.selectedUnit = value;
         if (value != null)
         {
            value.setSelectedBy(this);
         }
         firePropertyChange("selectedUnit", oldValue, value);
      }
      return this;
   }



   public static final String PROPERTY_selectedField = "selectedField";

   private Field selectedField = null;

   public Field getSelectedField()
   {
      return this.selectedField;
   }

   public Game setSelectedField(Field value)
   {
      if (this.selectedField != value)
      {
         Field oldValue = this.selectedField;
         if (this.selectedField != null)
         {
            this.selectedField = null;
            oldValue.setSelectedBy(null);
         }
         this.selectedField = value;
         if (value != null)
         {
            value.setSelectedBy(this);
         }
         firePropertyChange("selectedField", oldValue, value);
      }
      return this;
   }



   public static final String PROPERTY_turnPlayer = "turnPlayer";

   private Player turnPlayer = null;

   public Player getTurnPlayer()
   {
      return this.turnPlayer;
   }

   public Game setTurnPlayer(Player value)
   {
      if (this.turnPlayer != value)
      {
         Player oldValue = this.turnPlayer;
         if (this.turnPlayer != null)
         {
            this.turnPlayer = null;
            oldValue.setTurnPlayerGame(null);
         }
         this.turnPlayer = value;
         if (value != null)
         {
            value.setTurnPlayerGame(this);
         }
         firePropertyChange("turnPlayer", oldValue, value);
      }
      return this;
   }



   public static final java.util.ArrayList<Unit> EMPTY_allUnits = new java.util.ArrayList<Unit>()
   { @Override public boolean add(Unit value){ throw new UnsupportedOperationException("No direct add! Use xy.withAllUnits(obj)"); }};


   public static final String PROPERTY_allUnits = "allUnits";

   private java.util.ArrayList<Unit> allUnits = null;

   public java.util.ArrayList<Unit> getAllUnits()
   {
      if (this.allUnits == null)
      {
         return EMPTY_allUnits;
      }

      return this.allUnits;
   }

   public Game withAllUnits(Object... value)
   {
      if(value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withAllUnits(i);
            }
         }
         else if (item instanceof Unit)
         {
            if (this.allUnits == null)
            {
               this.allUnits = new java.util.ArrayList<Unit>();
            }
            if ( ! this.allUnits.contains(item))
            {
               this.allUnits.add((Unit)item);
               ((Unit)item).setGame(this);
               firePropertyChange("allUnits", null, item);
            }
         }
         else throw new IllegalArgumentException();
      }
      return this;
   }



   public Game withoutAllUnits(Object... value)
   {
      if (this.allUnits == null || value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withoutAllUnits(i);
            }
         }
         else if (item instanceof Unit)
         {
            if (this.allUnits.contains(item))
            {
               this.allUnits.remove((Unit)item);
               ((Unit)item).setGame(null);
               firePropertyChange("allUnits", item, null);
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

   public Game setApp(App value)
   {
      if (this.app != value)
      {
         App oldValue = this.app;
         if (this.app != null)
         {
            this.app = null;
            oldValue.withoutAllGames(this);
         }
         this.app = value;
         if (value != null)
         {
            value.withAllGames(this);
         }
         firePropertyChange("app", oldValue, value);
      }
      return this;
   }



   public static final String PROPERTY_gameField = "gameField";

   private GameField gameField = null;

   public GameField getGameField()
   {
      return this.gameField;
   }

   public Game setGameField(GameField value)
   {
      if (this.gameField != value)
      {
         GameField oldValue = this.gameField;
         if (this.gameField != null)
         {
            this.gameField = null;
            oldValue.setGame(null);
         }
         this.gameField = value;
         if (value != null)
         {
            value.setGame(this);
         }
         firePropertyChange("gameField", oldValue, value);
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
      result.append(" ").append(this.getGameId());
      result.append(" ").append(this.getActivePlayer());
      result.append(" ").append(this.getCurrentPhase());
      result.append(" ").append(this.getWinner());


      return result.substring(1);
   }

   public void removeYou()
   {
      this.setSelectedUnit(null);
      this.setSelectedField(null);
      this.setTurnPlayer(null);
      this.setApp(null);
      this.setGameField(null);

      this.withoutPlayers(this.getPlayers().clone());


      this.withoutIngameMessages(this.getIngameMessages().clone());


      this.withoutAllUnits(this.getAllUnits().clone());


   }


}