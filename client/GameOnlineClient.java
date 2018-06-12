import MG2D.geometrie.Couleur;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.UUID;

public class GameOnlineClient extends Game
{
   private static final String currentInstanceUserIdentifier = UUID.randomUUID().toString();
   private static String currentInstancePlayerPseudo = "rigwild-" + Config.rand(100, 999);
   private static int currentInstancePlayerId = -1;
   private WebSocketClient socketClient;
   private URI socketUrl;

   public GameOnlineClient()
   {
      super();
      //On crée la connexion au serveur
      try
      {
         socketUrl = new URI("ws://localhost" + ":" + 8887);
         socketClient = new WebSocketClient(socketUrl)
         {
            @Override
            public void onMessage(String message)
            {
               updateFromSocket(message);
            }

            @Override
            public void onOpen(ServerHandshake handshake)
            {
               System.out.println("connected to : " + getURI() + "\n");
            }

            @Override
            public void onClose(int code, String reason, boolean remote)
            {
               System.out.println("disconnected from: " + getURI() + "; Code: " + code + " " + reason + "\n");
               sendMsg("disconnected;pseudo=" + currentInstancePlayerPseudo);
            }

            @Override
            public void onError(Exception ex)
            {
               System.out.println("Exception occured ...\n");
               ex.printStackTrace();
            }
         };

         socketClient.connect();
         waitConnection();
         if (socketClient.isClosed())
            return;
         System.out.println("Your in-game pseudo : " + currentInstancePlayerPseudo);
         sendMsg("login;pseudo=" + currentInstancePlayerPseudo);
      }
      catch (URISyntaxException ex)
      {
         System.out.println(socketUrl + " is not a valid WebSocket URI\n");
      }
   }


   /**
    * Fait attendre que la connexion soit entièrement établie
    */
   private void waitConnection()
   {
      while (!socketClient.isOpen())
      {
         try
         {
            Thread.sleep(100);
         }
         catch (Exception e)
         {
         }
      }
   }


   void initPlayer()
   {
      playerList = new ArrayList<>();
      playerList.add(new Player(
         1,
         Couleur.ROUGE,
         ""
      ));

      playerList.add(new Player(
         2,
         Couleur.BLEU,
         ""
      ));
   }

   /**
    * Un tour de jeu
    *
    * @param player le joueur qui doit jouer son tour
    */
   void gameTurn(Player player)
   {
      Coordinate aimedCoordinate;
      int aimedX;
      int aimedY;
      String direction;
      boolean loop = true;

      while (loop)
      {
         try
         {
            Thread.sleep(Config.getFps());
         }
         catch (InterruptedException e)
         {
            e.printStackTrace();
         }

         //On affiche un indicateur visuel à coté de la ligne visée, on récupère sa coordonnée
         //sur le plateau de jeu
         aimedCoordinate = gui.showLineHint(player);
         aimedX = aimedCoordinate.getX();
         aimedY = aimedCoordinate.getY();

         //Si le joueur a cliqué, on vérifie que c'est bien à côté d'une croix
         if (gui.hasClicked() && aimedX != -1 && aimedY != -1)
         {
            //On récupère la direction cliquée et tente d'insérer un jeton
            if (aimedX == 0) direction = "left";
            else if (aimedX == Config.getGridSizeX() - 1) direction = "right";
            else direction = ""; //addToken gère cela

            if (grid.addToken(aimedY, player, direction))
            {
               //Jeton ajouté au plateau
               sendMsg("playerTurn_tokenY=" + String.valueOf(aimedY) + "~direction=" + direction + ";pseudo=" + player.getPlayerPseudo() + ";");
               loop = false;
            }
         }
      }
      gui.hideLineHint();
      gui.updateCrosses(grid, playerList);
   }


   public void sendMsg(String msg)
   {
      socketClient.send("id=" + currentInstanceUserIdentifier + ";" + msg);
   }

   private void initGame()
   {
      gui = new GraphicalInterface();
      grid = new Grid();

      //Set players start coordinates
      playerList.get(0).setPlayerStartCoordinate(new Coordinate(1, grid.board[0].length - 1));
      playerList.get(1).setPlayerStartCoordinate(new Coordinate(grid.board.length - 2, grid.board[0].length - 1));

      //Ajout des joueurs à la fenêtre
      gui.addAllPlayerToWindow(playerList);

      nbGameTurn = 0;
   }

   public void updateFromSocket(String message)
   {
      ArrayList<String> regexResult;
      String regex;

      System.out.println("wzfefhu : " + message);
      if (message.equals("server;waiting_for_other_player"))
      {
         System.out.println("Waiting for other player...");
      }
      else if (Config.matchesRegex(regex = "player_disconnected;pseudo=(.*?)", message))
      {
         regexResult = Config.getRegexResult(regex, message);
         for (int i = 0; i < playerList.size(); i++)
            if (playerList.get(i).getPlayerPseudo().equals(regexResult.get(0)))
            {
               playerList.remove(i);
            }
         System.out.println("Your opponent disconnected.");
      }
      else
      {
         regex = "server;gameConfig=x=(.*?);y=(.*?);"      //0, 1
            + "player_1_pseudo=(.*?);player_2_pseudo=(.*?);"      //2, 3
            + "grid=(.*?);"                                       //4
            + "nbTurn=(.*?);whoShouldPlayNow=joueur_(.*?)_(.*?);";//5, 6, 7

         if (Config.matchesRegex(regex, message))
         {//La configuration de la partie a été reçue
            regexResult = Config.getRegexResult(regex, message);
            if (!(regexResult.size() > 0))
               return;

            //Application des paramètres
            if (Config.getGridSizeX() != Integer.parseInt(regexResult.get(0))
               || Config.getGridSizeY() != Integer.parseInt(regexResult.get(1)))
            {
               Config.setGridSizeX(Integer.parseInt(regexResult.get(0)));
               Config.setGridSizeY(Integer.parseInt(regexResult.get(1)));
            }

            //Création des joueurs
            if (playerList == null)
            {
               initPlayer();
               playerList.get(0).setPlayerPseudo(regexResult.get(2));
               playerList.get(1).setPlayerPseudo(regexResult.get(3));

               //On sauvegarde le numéro de joueur de l'instance actuelle
               currentInstancePlayerId = (regexResult.get(2).equals(currentInstancePlayerPseudo)) ? 1 : 2;
               System.out.println("Found another player ! You are P" + currentInstancePlayerId);

               //Configuration OK, lancement des éléments du jeu
               initGame();

               for (Player aPlayer : playerList)
                  System.out.println(aPlayer);
            }

            //On met à jour le plateau de jeu
            grid.stringToGrid(regexResult.get(4));
            gui.updateCrosses(grid, playerList);

            //On met à jour le nombre de tours de jeu
            nbGameTurn = Integer.parseInt(regexResult.get(5));

            System.out.println("\nHere's the grid : \n" + grid);

            //On actualise le message indiquant le pseudo du joueur qui doit jouer
            gui.setPlayMessage(nbGameTurn, regexResult.get(7));

            //Si c'est à nous de jouer, go.
            if (regexResult.get(7).equals(currentInstancePlayerPseudo))
            {
               gameTurn(playerList.get(Integer.parseInt(regexResult.get(6)) - 1));
               System.out.println("You just played your turn !");
            }
            else
               System.out.println("The other player is playing is turn...");
         }
      }
   }
}
