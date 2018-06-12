import org.java_websocket.WebSocket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GameOnlineServer
{
   private static SocketServer socketServer;
   private static int nbPlayer = 0;
   private static int sizeGameX = 15;
   private static int sizeGameY = 15;
   private static ArrayList<PlayerOnline> player = new ArrayList<>();
   private static Grid grid;

   private static boolean gameStarted = false;
   private static int nbTurn = 0;
   private static PlayerOnline whoShouldPlayNow;
   private static boolean receivedMessage;

   public GameOnlineServer()
   {
      startSocketServer(8887);
   }

   private void startSocketServer(int port)
   {
      try
      {
         socketServer = new SocketServer(port);
         socketServer.start();

         BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
         while (true)
         {
            String in = sysin.readLine();
            socketServer.broadcast(in);
            if (in.equals("exit"))
            {
               socketServer.stop(1000);
               break;
            }
         }
      }
      catch (Exception e)
      {
      }
   }

   public static void updateFromSocket(String message, WebSocket conn)
   {
      System.out.println("Received : " + message + " # conn_id = " + conn);
      ArrayList<String> res;
      String regex;
      if (Config.matchesRegex(regex = "id=(.*?);login;pseudo=(.*)", message))
      {
         nbPlayer++;
         if (nbPlayer > 2)
         {
            nbPlayer--;
            conn.close();
            System.out.println("Refused connection : we already have 2 players !");
            return;
         }
         else
         {
            res = Config.getRegexResult(regex, message);
            if (res.size() > 0)
            {
               player.add(new PlayerOnline(
                  res.get(1),
                  res.get(0),
                  conn.toString()
               ));
               if (nbPlayer == 2)
               {
                  System.out.println("\nWe have 2 players. The game starts.");
                  //Les 2 joueurs sont présents, on peut commencer la partie
                  startGame();
                  return;
               }
            }
         }
         //1 Seul joueur est connecté, attente d'un autre joueur
         socketServer.serverBroadcast("server;waiting_for_other_player");
         return;
      }
      else if (message.equals("disconnected"))
      {
         //Un joueur s'est déconnecté
         for (int i = 0; i < player.size(); i++)
         {
            if (player.get(i).getWebSocketID().equals(conn.toString()))
            {
               socketServer.serverBroadcast("player_disconnected;pseudo=" + player.get(i).getPlayerPseudo());
               player.remove(i);
               nbPlayer--;
            }
         }
         return;
      }
      else if (Config.matchesRegex(regex = "id=(.*?);playerTurn_tokenY=(.*?)~direction=(.*?);pseudo=(.*?);", message))
      {
         receivedMessage = true;
         res = Config.getRegexResult(regex, message);
         if (!gameStarted || !(res.size() > 0))
            return;

         //On vérifie que le joueur qui transmet ce message est bien celui qui doit jouer (UDID + pseudo correct)
         if (whoShouldPlayNow.equals(new PlayerOnline(res.get(3), res.get(0))))
         {
            int y = Integer.parseInt(res.get(1));
            String direction = res.get(2);
            //On joue le tour, si c'est en dehors du jeu (addToken retourne false), il y a triche, on ne fait rien
            if (grid.addToken(y, new Player(whoShouldPlayNow.getPlayerId()), direction))
               playTheTurn();
         }
         receivedMessage = false;
      }
      return;
   }

   private static void setPlayerData()
   {
      for (int i = 0; i < player.size(); i++)
      {
         if (player.get(i).getPlayerId() == -1)
            player.get(i).setPlayerId(i + 1);

         if (i == 0)
            player.get(i).setPlayerStartCoordinate(new Coordinate(1, grid.board[0].length - 1));
         else if (i == 1)
            player.get(i).setPlayerStartCoordinate(new Coordinate(grid.board.length - 2, grid.board[0].length - 1));

      }
   }

   private static String getGameConfig()
   {
      return "server;gameConfig=x=" + sizeGameX + ";y=" + sizeGameY + ";";
   }

   private static String getAllPseudo()
   {
      String toReturn = "";
      int i = 1;
      for (PlayerOnline aPlayer : player)
      {
         toReturn += "player_" + i + "_pseudo=" + aPlayer.getPlayerPseudo() + ";";
         i++;
      }
      return toReturn;
   }

   private static String getGrid()
   {
      return "grid=" + grid.toStringInline() + ";";
   }

   private static String getNbTurn()
   {
      return "nbTurn=" + nbTurn + ";";
   }

   private static String getNextPlayer()
   {
      return "whoShouldPlayNow=joueur_" + whoShouldPlayNow.getPlayerId() + "_" + whoShouldPlayNow.getPlayerPseudo() + ";";
   }

   private static String getGameTurn()
   {
      return getGameConfig()
         + getAllPseudo()
         + getGrid()
         + getNbTurn()
         + getNextPlayer();
   }

   private static void startGame()
   {
      gameStarted = true;
      nbTurn = 0;

      //Création de la grille de jeu
      grid = new Grid(sizeGameX, sizeGameY);

      //Initialisation des numéros de joueur (j1,j2)
      setPlayerData();
      //Le joueur qui commence est choisi aléatoirement
      whoShouldPlayNow = new PlayerOnline(player.get(Config.rand(0, 1)));

      //On envoit le premier tour
      socketServer.serverBroadcast(getGameTurn());
   }

   private static void playTheTurn()
   {
      nbTurn++;

      //On test si il y a égalité ou si un joueur a gagné
      if (nbTurn >= Config.getGridSizeX() * Config.getGridSizeY())
      {
         gameEquality();
         return;
      }

      PlayerOnline whoWon = grid.searchWinner(player);
      //Tant qu'il n'y a pas de gagnant, on continue
      if (!whoWon.getUDID().equals("not_found"))
      {
         gameWin(whoWon);
         return;
      }

      //C'est à l'autre joueur de jouer
      whoShouldPlayNow = new PlayerOnline(player.get((whoShouldPlayNow.getPlayerId() - 1 == 1) ? 0 : 1));

      //On envoit le résultat du tour de jeu
      socketServer.serverBroadcast(getGameTurn());
   }

   private static void gameEquality()
   {

   }

   private static void gameWin(PlayerOnline winner)
   {
      System.out.println("\nFound winner ! : " + winner.getPlayerPseudo());
   }

   public static void main(String[] args)
   {
      GameOnlineServer launcherServer = new GameOnlineServer();
   }
}