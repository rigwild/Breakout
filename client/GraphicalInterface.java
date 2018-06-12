import MG2D.Fenetre;
import MG2D.Souris;
import MG2D.geometrie.Couleur;
import MG2D.geometrie.Point;
import MG2D.geometrie.Rectangle;
import MG2D.geometrie.Texte;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GraphicalInterface
{
   private Fenetre window;
   private Souris mouse;
   private Cross[][] crosses;
   private int crossSize;
   private Rectangle gameArea;
   private Cross lineHint;

   private Texte playMessage;

   public GraphicalInterface()
   {
      window = new Fenetre("Break out by rigwild", Config.getWindowSizeX(), Config.getWindowSizeY());
      mouse = window.getSouris();

      JTextField t = new JTextField("hello", 15);
      window.getContentPane().add(t);
      window.getP().add(t);
      window.add(t);

      startMenuGUI();

      initGameGUI();

      window.rafraichir();
   }

   private void startMenuGUI()
   {

   }

   private void initGameGUI()
   {
      //Rectangle qui définit l'espace du jeu
      gameArea = new Rectangle(
         new Point(0, 0),
         new Point(Config.getGameAreaSizeX(), Config.getGameAreaSizeY()),
         true
      );
      window.ajouter(gameArea);

      //On choisit la taille des croix
      if (Config.getGridSizeX() > Config.getGridSizeY())
         crossSize = gameArea.getLargeur() / Config.getGridSizeX();
      else
         crossSize = gameArea.getHauteur() / Config.getGridSizeY();

      //On centre l'espace de jeu avec la fenêtre
      int centerAreaX = (window.getMilieu().getX() * 2 - gameArea.getLargeur()) / 2;
      int centerAreaY = (window.getMilieu().getY() * 2 - gameArea.getHauteur()) / 2;

      gameArea.setB(new Point(
         gameArea.getB().getX() + centerAreaX,
         gameArea.getB().getY() + centerAreaY
      ));
      gameArea.setA(new Point(
         gameArea.getA().getX() + centerAreaX,
         gameArea.getA().getY() + centerAreaY
      ));

      //On génère les croix du plateau
      crosses = generateCrosses();

      //On crée l'indicateur visuel de sélection de ligne à viser
      lineHint = new Cross(
         window,
         new Point(-500, -500),
         crossSize,
         crossSize / 3,
         true
      );

      //On ajoute le message du joueur qui doit jouer en haut (pour le online)
      playMessage = new Texte(
         Couleur.NOIR,
         "",
         new Font("Calibri", Font.BOLD, 20),
         new Point(window.getMilieu().getX(), window.getMilieu().getY() * 2 - 30)
      );
      window.ajouter(playMessage);
   }

   /**
    * Génère les croix nécessaires au jeu
    *
    * @return Un tableau de croix à 2 dimensions contenant les croix du plateau
    */
   private Cross[][] generateCrosses()
   {
      Cross[][] generatedCrosses = new Cross[Config.getGridSizeX()][Config.getGridSizeY()];


      for (int i = 0; i < Config.getGridSizeX(); i++)
      {
         for (int j = 0; j < Config.getGridSizeY(); j++)
         {
            generatedCrosses[i][j] = new Cross(
               window,
               new Point(
                  i * crossSize + gameArea.getA().getX(),
                  j * crossSize + gameArea.getA().getY()
               ),
               crossSize,
               crossSize / 3,
               true
            );
         }
      }
      return generatedCrosses;
   }


   /**
    * Met à jour les croix avec le plateau du jeu
    *
    * @param board      Le plateau de jeu
    * @param playerList La liste des joueurs
    */
   public void updateCrosses(Grid board, ArrayList<Player> playerList)
   {
      for (int i = 0; i < board.board.length; i++)
      {
         for (int j = 0; j < board.board[0].length; j++)
         {
            int idPlayer = board.board[i][j];
            if (idPlayer != 0)
               crosses[i][j].setColor(playerList.get(idPlayer - 1).getPlayerColor());
         }
      }
      window.rafraichir();
   }

   /**
    * Ajoute tous les joueurs à la fenêtre (leur Texture image)
    */
   public void addAllPlayerToWindow(ArrayList<Player> playerList)
   {
      for (int i = 0; i < playerList.size(); i++)
      {
         Coordinate c = playerList.get(i).getPlayerStartCoordinate();
         //On positionne l'image du joueur
         Point posPlayerImage = new Point(
            crosses[c.getX()][c.getY()].getOrigin().getX(),
            crosses[c.getX()][c.getY()].getOrigin().getY() + crossSize
         );

         playerList.get(i).getPlayerImage().setA(posPlayerImage);

         //On taille correctement l'image
         // (Plus grand que la croix car l'image du prisonnier a un boulet qui augmente la largeur de l'image
         // et enlève l'impression d'être centré !)
         playerList.get(i).getPlayerImage().setLargeur((int) (crossSize * 1.6));
         playerList.get(i).getPlayerImage().setHauteur((int) (crossSize * 1.8));
         window.ajouter(playerList.get(i).getPlayerImage());

         //On affiche les pseudos des joueurs
         Point pseudoPos = new Point(posPlayerImage.getX() + 100, posPlayerImage.getY() + 80);
         window.ajouter(new Texte(
            playerList.get(i).getPlayerColor(),
            playerList.get(i).getPlayerPseudo(),
            new Font("Calibri", Font.BOLD, 15),
            pseudoPos
         ));
      }
   }

   /**
    * Affiche un indicateur visuel à coté de la ligne visée
    *
    * @param player Le joueur entrain de jouer son tour
    * @return les coordonnées sur le plateau qui sont visées ou -1,-1 sinon
    */
   public Coordinate showLineHint(Player player)
   {
      //On applique la couleur du joueur à l'indicateur si ce n'est pas déjà fait
      if (!(lineHint.getColor().equals(player.getPlayerColor())))
      {
         lineHint.setColor(player.getPlayerColor());
      }
      Point position = mouse.getPosition();
      Coordinate toReturn = new Coordinate(-1, -1);

      //Définition de la zone de détection en X à gauche et à droite du plateau de jeu
      int hitboxX_end_left = crosses[0][0].getOrigin().getX();
      int hitboxX_start_left = hitboxX_end_left - crossSize;

      int hitboxX_start_right = crosses[crosses.length - 1][0].getOrigin().getX() + crossSize;
      int hitboxX_end_right = hitboxX_start_right + crossSize;


      //On vérifie si la souris est bien dans la zone de détection horizontale,
      //puis on test pour chaque croix si la souris est dans leur zone de détection verticale
      //Il vaut mieux faire un IF avant qu'une boucle (pour la performance)
      if (position.getX() > hitboxX_start_left && position.getX() < hitboxX_end_left)
      {
         toReturn = setHintIfMouseInLine(position, player, crosses[0], "left");
      }
      else if (position.getX() > hitboxX_start_right && position.getX() < hitboxX_end_right)
      {
         toReturn = setHintIfMouseInLine(position, player, crosses[crosses.length - 1], "right");
      }
      else
      {
         //Si la souris ne vise pas de colonne, on regarde si l'indicateur visuel est en -500,-500
         //Si c'est le cas, on ne fait rien, sinon on met l'indicateur à -500,-500
         //Cela sert à économiser de la performance
         if (!(lineHint.getOrigin().getX() == -500 && lineHint.getOrigin().getY() == -500))
         {
            lineHint.setOrigin(new Point(-500, -500));
            window.rafraichir();
         }
      }
      return toReturn;
   }

   /**
    * Applique à l'indicateur visuel sa nouvelle position et couleur si il est bien
    * dans une ligne du plateau
    *
    * @param position  La postion de la souris
    * @param player    Le joueur entrain de jouer son tour
    * @param crossRow  un tableau à 1 dimension de la colonne visée (droite ou gauche du plateau de jeu)
    * @param direction "left" ou "right", définit de quel côté du plateau il est question
    * @return les coordonnées sur le plateau qui sont visées ou -1,-1 sinon
    */
   private Coordinate setHintIfMouseInLine(Point position, Player player, Cross[] crossRow, String direction)
   {
      Coordinate toReturn = new Coordinate(-1, -1);
      int hitboxY_start;
      int hitboxY_end;

      int i = 0;
      int c = crossRow.length;
      boolean loop = true;

      while (loop && i < c)
      {
         //Définition de la zone de détection en Y
         hitboxY_start = crossRow[i].getOrigin().getY();
         hitboxY_end = crossRow[i].getOrigin().getY() + crossSize;
         if (position.getY() > hitboxY_start && position.getY() < hitboxY_end)
         {
            //La souris est bien dans la zone, on arrête la boucle et modifie l'indicateur visuel
            //On définit la position de l'indicateur par rapport à la direction visée
            loop = false;
            int x = -500;
            toReturn.setY(i);
            if (direction.equals("left"))
            {
               x = crossRow[i].getOrigin().getX() - crossSize;
               toReturn.setX(0);
            }
            else if (direction.equals("right"))
            {
               x = crossRow[i].getOrigin().getX() + crossSize;
               toReturn.setX(crosses.length - 1);
            }
            lineHint.setOrigin(new Point(
               x,
               crossRow[i].getOrigin().getY()
            ));

            //On rafraichit la fenêtre seulement si l'indicateur est modifié !
            window.rafraichir();
         }
         i++;
      }
      return toReturn;
   }

   public void hideLineHint()
   {
      lineHint.setOrigin(new Point(
         -5000,
         -5000
      ));
   }

   /**
    * Check si la fenêtre a reçu un clic gauche de la souris
    *
    * @return true = la fenêtre a reçu un clic gauche, false = la fenêtre n'a pas reçu de clic gauche
    */
   public boolean hasClicked()
   {
      return mouse.getClicGauche();
   }


   public void setPlayMessage(int nbTurn, String player)
   {
      playMessage.setTexte("Tour " + String.valueOf(nbTurn + 1) + ". Au tour de : " + player);
      window.rafraichir();
   }
}
