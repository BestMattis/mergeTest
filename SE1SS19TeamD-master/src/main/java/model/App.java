package model;

import java.beans.PropertyChangeSupport;

import java.beans.PropertyChangeListener;

public class App  
{

   public static final java.util.ArrayList<Player> EMPTY_allPlayers = new java.util.ArrayList<Player>()
   { @Override public boolean add(Player value){ throw new UnsupportedOperationException("No direct add! Use xy.withAllPlayers(obj)"); }};


   public static final String PROPERTY_allPlayers = "allPlayers";

   private java.util.ArrayList<Player> allPlayers = null;

   public java.util.ArrayList<Player> getAllPlayers()
   {
      if (this.allPlayers == null)
      {
         return EMPTY_allPlayers;
      }

      return this.allPlayers;
   }

   public App withAllPlayers(Object... value)
   {
      if(value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withAllPlayers(i);
            }
         }
         else if (item instanceof Player)
         {
            if (this.allPlayers == null)
            {
               this.allPlayers = new java.util.ArrayList<Player>();
            }
            if ( ! this.allPlayers.contains(item))
            {
               this.allPlayers.add((Player)item);
               ((Player)item).setApp(this);
               firePropertyChange("allPlayers", null, item);
            }
         }
         else throw new IllegalArgumentException();
      }
      return this;
   }



   public App withoutAllPlayers(Object... value)
   {
      if (this.allPlayers == null || value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withoutAllPlayers(i);
            }
         }
         else if (item instanceof Player)
         {
            if (this.allPlayers.contains(item))
            {
               this.allPlayers.remove((Player)item);
               ((Player)item).setApp(null);
               firePropertyChange("allPlayers", item, null);
            }
         }
      }
      return this;
   }


   public static final java.util.ArrayList<Game> EMPTY_allGames = new java.util.ArrayList<Game>()
   { @Override public boolean add(Game value){ throw new UnsupportedOperationException("No direct add! Use xy.withAllGames(obj)"); }};


   public static final String PROPERTY_allGames = "allGames";

   private java.util.ArrayList<Game> allGames = null;

   public java.util.ArrayList<Game> getAllGames()
   {
      if (this.allGames == null)
      {
         return EMPTY_allGames;
      }

      return this.allGames;
   }

   public App withAllGames(Object... value)
   {
      if(value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withAllGames(i);
            }
         }
         else if (item instanceof Game)
         {
            if (this.allGames == null)
            {
               this.allGames = new java.util.ArrayList<Game>();
            }
            if ( ! this.allGames.contains(item))
            {
               this.allGames.add((Game)item);
               ((Game)item).setApp(this);
               firePropertyChange("allGames", null, item);
            }
         }
         else throw new IllegalArgumentException();
      }
      return this;
   }



   public App withoutAllGames(Object... value)
   {
      if (this.allGames == null || value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withoutAllGames(i);
            }
         }
         else if (item instanceof Game)
         {
            if (this.allGames.contains(item))
            {
               this.allGames.remove((Game)item);
               ((Game)item).setApp(null);
               firePropertyChange("allGames", item, null);
            }
         }
      }
      return this;
   }


   public static final String PROPERTY_currentPlayer = "currentPlayer";

   private Player currentPlayer = null;

   public Player getCurrentPlayer()
   {
      return this.currentPlayer;
   }

   public App setCurrentPlayer(Player value)
   {
      if (this.currentPlayer != value)
      {
         Player oldValue = this.currentPlayer;
         if (this.currentPlayer != null)
         {
            this.currentPlayer = null;
            oldValue.setMyApp(null);
         }
         this.currentPlayer = value;
         if (value != null)
         {
            value.setMyApp(this);
         }
         firePropertyChange("currentPlayer", oldValue, value);
      }
      return this;
   }



   public static final java.util.ArrayList<ChatMessage> EMPTY_allChatMessages = new java.util.ArrayList<ChatMessage>()
   { @Override public boolean add(ChatMessage value){ throw new UnsupportedOperationException("No direct add! Use xy.withAllChatMessages(obj)"); }};


   public static final String PROPERTY_allChatMessages = "allChatMessages";

   private java.util.ArrayList<ChatMessage> allChatMessages = null;

   public java.util.ArrayList<ChatMessage> getAllChatMessages()
   {
      if (this.allChatMessages == null)
      {
         return EMPTY_allChatMessages;
      }

      return this.allChatMessages;
   }

   public App withAllChatMessages(Object... value)
   {
      if(value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withAllChatMessages(i);
            }
         }
         else if (item instanceof ChatMessage)
         {
            if (this.allChatMessages == null)
            {
               this.allChatMessages = new java.util.ArrayList<ChatMessage>();
            }
            if ( ! this.allChatMessages.contains(item))
            {
               this.allChatMessages.add((ChatMessage)item);
               ((ChatMessage)item).setApp(this);
               firePropertyChange("allChatMessages", null, item);
            }
         }
         else throw new IllegalArgumentException();
      }
      return this;
   }



   public App withoutAllChatMessages(Object... value)
   {
      if (this.allChatMessages == null || value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withoutAllChatMessages(i);
            }
         }
         else if (item instanceof ChatMessage)
         {
            if (this.allChatMessages.contains(item))
            {
               this.allChatMessages.remove((ChatMessage)item);
               ((ChatMessage)item).setApp(null);
               firePropertyChange("allChatMessages", item, null);
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



   public void removeYou()
   {
      this.setCurrentPlayer(null);

      this.withoutAllPlayers(this.getAllPlayers().clone());


      this.withoutAllGames(this.getAllGames().clone());


      this.withoutAllChatMessages(this.getAllChatMessages().clone());


   }






















}