import MG2D.geometrie.Couleur;

import java.util.ArrayList;

public class Grid
{
   int[][] board;

   public Grid()
   {
      board = new int[Config.getGridSizeX()][Config.getGridSizeY()];
      initBoard(board);
   }

   public Grid(int x, int y)
   {
      board = new int[x][y];
      initBoard(board);
   }

   public Grid(Grid coppiedGrid)
   {
      int x = coppiedGrid.board.length;
      int y = coppiedGrid.board[0].length;
      board = new int[x][y];
      initBoard(board);
   }

   /**
    * Rempli le plateau de jeu avec des 0
    *
    * @param board Le plateau de jeu à remplir
    */
   private void initBoard(int[][] board)
   {
      for (int i = 0; i < board.length; i++)
      {
         for (int j = 0; j < board[0].length; j++)
         {
            board[i][j] = 0;
         }
      }
   }

   /**
    * Ajoute un jeton au plateau, depuis la direction choisie
    *
    * @param y         Ligne visée
    * @param player    Joueur qui ajoute un jeton
    * @param direction Coté où doit être inséré le jeton
    * @return true = jeton ajouté au plateau, false = Ligne pleine
    */
   public boolean addToken(int y, Player player, String direction)
   {
      //On vérifie que la direction est correcte
      if (!(direction.equals("left") || direction.equals("right"))) return false;
      //On vérifie que la ligne visée est bien dans le tableau
      if (!isInBoud(y)) return false;
      //On vérifie que la ligne visée n'est pas pleine
      if (isLineFull(y)) return false;

      int playerId = player.getPlayerId();
      //On pousse la ligne et insère le jeton du joueur en première position
      pushLine(y, direction);

      if (direction.equals("left"))
         board[0][y] = playerId;
      else
         board[board.length - 1][y] = playerId;
      return true;
   }

   /**
    * Pousse les éléments d'une ligne depuis une direction
    *
    * @param y         Ligne visée
    * @param direction Direction du décalage (Depuis la direction)
    */
   private void pushLine(int y, String direction)
   {

      //Le jeton est envoyé depuis la gauche
      if (direction.equals("left"))
      {
         //On récupère la première case vide depuis la gauche
         int i = 0;
         boolean continuer = true;
         while (i < board.length && continuer)
         {
            if (board[i][y] == 0)
               continuer = false;
            i++;
         }
         int firstEmpty = i - 1;

         for (i = firstEmpty; i > 0; i--)
         {
            board[i][y] = board[i - 1][y];
         }
      }
      //Le jeton est envoyé depuis la droite
      else
      {
         //On récupère la première case vide depuis la droite
         int i = board.length - 1;
         boolean continuer = true;
         while (i >= 0 && continuer)
         {
            if (board[i][y] == 0)
               continuer = false;
            i--;
         }
         int firstEmpty = i + 1;

         for (i = firstEmpty; i < board.length - 1; i++)
         {
            board[i][y] = board[i + 1][y];
         }
      }
   }


   /**
    * Vérifie que la ligne est bien dans le tableau
    *
    * @param y Ligne visée
    * @return true = dans le plateau, false = hors du plateau
    */
   public boolean isInBoud(int y)
   {
      return (y < board[0].length && y >= 0);
   }

   /**
    * Vérifie que la case est bien dans le tableau
    *
    * @param x colonne visée
    * @param y Ligne visée
    * @return true = dans le plateau, false = hors du plateau
    */
   public boolean isInBoud(int x, int y)
   {
      return (x < board.length && x >= 0 && y < board[0].length && y >= 0);
   }

   /**
    * Vérifie si la ligne est pleine
    *
    * @param y Ligne visée
    * @return true = ligne pleine, false = ligne non pleine
    */
   public boolean isLineFull(int y)
   {
      for (int i = 0; i < board.length; i++)
      {
         if (board[i][y] == 0) return false;
      }
      return true;
   }

   /**
    * Retourne une str avec les valeurs dans le plateau
    *
    * @return String
    */
   public String toString()
   {
      String str = "";
      for (int i = board[0].length - 1; i >= 0; i--)
      {
         for (int j = 0; j < board.length; j++)
         {
            str += board[j][i] + " ";
         }
         str += "\n";
      }
      return str;
   }

   /**
    * toString spécial pour le serveur
    *
    * @return Une string de la grille sur une seule ligne au format : "x_y~01022100102020211"
    */
   public String toStringInline()
   {
      String str = String.valueOf(board.length) + "_" + String.valueOf(board[0].length) + "~";
      for (int i = 0; i < board.length; i++)
         for (int j = 0; j < board[0].length; j++)
            str += board[i][j];
      return str;
   }

   /**
    * toString spécial pour le client,
    */
   public void stringToGrid(String str)
   {
      ArrayList<String> res = Config.getRegexResult("(.*?)_(.*?)~(.*)", str);
      if (!(res.size() > 0))
         return;

      int x = Integer.parseInt(res.get(0));
      int y = Integer.parseInt(res.get(1));
      if (Config.getGridSizeX() != x || Config.getGridSizeX() != y)
      {
         Config.setGridSizeX(x);
         Config.setGridSizeY(y);
      }
      String gridStr = res.get(2);
      board = new int[x][y];
      int i = 0;
      for (int k = 0; k < x; k++)
      {
         for (int j = 0; j < y; j++)
         {
            board[k][j] = Integer.parseInt(String.valueOf(gridStr.charAt(i)));
            i++;
         }
      }
   }


   /**
    * Indique si un joueur a gagné la partie ou non
    *
    * @param player le joueur qui doit être testé
    * @return true = le joueur a gagné, false = le joueur n'a pas gagné
    */
   private boolean hasWon(Player player)
   {
      Coordinate playerStartCoordinate = player.getPlayerStartCoordinate();
      int playerStartCoordinateX = playerStartCoordinate.getX();
      int playerStartCoordinateY = playerStartCoordinate.getY();

      //Si la case de départ du joueur n'est pas de sa couleur, il n'a pas gagné
      if (board[playerStartCoordinateX][playerStartCoordinateY] != player.getPlayerId())
         return false;

      //Si il n'y a aucune fois la couleur du joueur dans la ligne du bas, il n'a pas gagné
      int i = 0;
      boolean continuer = true;
      while (continuer && i < board.length)
      {
         if (board[i][0] == player.getPlayerId())
            continuer = false;
         i++;
      }
      if (continuer && i == board.length)
         return false;


      /*
      Parcours par largeur de la grille pour trouver un chemin de victoire
      On ajoute les cases adjacentes de la case de départ, puis pour chaque cases adjacentes,
      leurs cases adjacentes et ainsi de suite jusqu'à arriver à la ligne 0 (gagné) ou non (pas gagné).
      */

      //On crée une liste des coordonnées appartenant au joueur déjà visitées
      ArrayList<Coordinate> coordinates = new ArrayList<>();
      //On ajoute la coordonnée de départ du joueur dans la liste
      coordinates.add(playerStartCoordinate);

      //Déclaration de la liste des coordonnées adjacentes pour après
      ArrayList<Coordinate> adjacentCoordinate;

      i = 0;
      while (i < coordinates.size())
      {
         //On récupère les coordonnées adjacentes dans une liste
         adjacentCoordinate = getAdjacentCoordinate(coordinates.get(i));

         //Si la liste des coordonnées adjacente est vide, il y a eu une erreur. On retourne false par sécurité !
         if (adjacentCoordinate.isEmpty())
            return false;

         for (int j = 0; j < adjacentCoordinate.size(); j++)
         {
            //Si la coordonnée adjacente est de la bonne couleur et n'est pas déjà dans la liste
            if (!isCoordinateInCollection(adjacentCoordinate.get(j), coordinates)
               && board[adjacentCoordinate.get(j).getX()][adjacentCoordinate.get(j).getY()] == player.getPlayerId())
            {
               //On ajoute la coordonnée en cours de traitement à la liste
               coordinates.add(adjacentCoordinate.get(j));

               //Si la coordonnée adjacente est bien de la bonne couleur et à la ligne 0, alors le joueur a gagné
               if (board[adjacentCoordinate.get(j).getX()][adjacentCoordinate.get(j).getY()] == player.getPlayerId()
                  && adjacentCoordinate.get(j).getY() == 0)
                  return true;
            }
         }
         i++;
      }
      //Le joueur n'a pas gagné
      return false;
   }

   /**
    * Cherche si un joueur a gagné -- version serveur
    *
    * @param playerList La liste des joueurs à vérifier
    * @return L'objet Player du gagnant ou un Player avec id = -1 si personne n'a gagné
    */
   public PlayerOnline searchWinner(ArrayList<PlayerOnline> playerList)
   {
      PlayerOnline currentPlayer;
      //On cherche si un joueur a gagné
      for (int i = 0; i < playerList.size(); i++)
      {
         currentPlayer = playerList.get(i);
         if (hasWon(currentPlayer))
            return currentPlayer;
      }
      //Sinon on renvoi un joueur avec id = -1
      return new PlayerOnline("not_found");
   }


   /**
    * Donne la liste des coordonnées DANS LE TABLEAU adjacente à la coordonée passée en paramètre
    *
    * @param coordinate une coordonnée de la grille
    * @return une collection de coordonnées adjacentes valides
    */
   private ArrayList<Coordinate> getAdjacentCoordinate(Coordinate coordinate)
   {
      ArrayList<Coordinate> adjacent = new ArrayList<>();
      int x = coordinate.getX();
      int y = coordinate.getY();

      //Si la coordonée en paramètre est hors du tableau, on retourn une collection vide
      if (!isInBoud(x, y))
         return adjacent;

      //Coordonnée à gauche
      if (isInBoud(x - 1, y))
         adjacent.add(new Coordinate(x - 1, y));
      //Coordonnée en haut
      if (isInBoud(x, y + 1))
         adjacent.add(new Coordinate(x, y + 1));
      //Coordonnée à droite
      if (isInBoud(x + 1, y))
         adjacent.add(new Coordinate(x + 1, y));
      //Coordonnée en bas
      if (isInBoud(x, y - 1))
         adjacent.add(new Coordinate(x, y - 1));

      return adjacent;
   }

   /**
    * Indique si une coordonnée se trouve déjà dans la collection.
    * La méthode .contains() de la collection Java teste si la référence d'un objet est présente dans la collection,
    * pas son contenu.
    *
    * @param coordinate Coordonnée à tester
    * @param collection Collection à tester
    * @return true = Coordonnée dans la collection, false = Coordonnée absente de la collection
    */
   private boolean isCoordinateInCollection(Coordinate coordinate, ArrayList<Coordinate> collection)
   {
      boolean found = false;
      int i = 0;
      while (!found && i < collection.size())
      {
         if (collection.get(i).equals(coordinate))
            found = true;
         i++;
      }
      return found;
   }
}
