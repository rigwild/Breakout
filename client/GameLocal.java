import MG2D.geometrie.Couleur;

import java.util.ArrayList;

public class GameLocal extends Game
{
   public GameLocal()
   {
      gui = new GraphicalInterface();
      grid = new Grid();

      //Création des joueurs
      initPlayer();

      //Initialisation de l'identifiant du joueur qui doit jouer
      gameTurnPlayer = 0;

      //Ajout des joueurs à la fenêtre
      gui.addAllPlayerToWindow(playerList);

      nbGameTurn = 0;

      Player whoWon = grid.searchWinner(playerList);
      //Tant qu'il n'y a pas de gagnant, on continue
      while (whoWon.getPlayerId() == -1)
      {
         //Si on est arrivé au dernier joueur, revenir au premier
         if (gameTurnPlayer == playerList.size())
            gameTurnPlayer = 0;
         gameTurn(playerList.get(gameTurnPlayer));
         gameTurnPlayer++;

         whoWon = grid.searchWinner(playerList);
      }
      System.out.println(whoWon);
   }

   void initPlayer()
   {
      playerList = new ArrayList<>();
      playerList.add(new Player(
         1,
         Couleur.ROUGE,
         "rigwild1",
         new Coordinate(1, grid.board[0].length - 1)
      ));

      playerList.add(new Player(
         2,
         Couleur.BLEU,
         "rigwild2",
         new Coordinate(grid.board.length - 2, grid.board[0].length - 1)
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

            if (grid.addToken(aimedY, player, direction)) {
               //Jeton ajouté au plateau
               loop = false;
            }
         }
      }
      gui.updateCrosses(grid, playerList);
   }
}
