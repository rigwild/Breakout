import MG2D.geometrie.Couleur;
import MG2D.geometrie.Point;
import MG2D.geometrie.Texture;

public class Player
{
   private int playerId = -1;
   private Couleur playerColor;
   private String playerPseudo;
   private Coordinate playerStartCoordinate;
   private Texture playerImage;

   public Player()
   {
   }

   public Player(int id)
   {
      playerId = id;
   }

   public Player(int id, Couleur color, String pseudo)
   {
      playerId = id;
      playerColor = color;
      playerPseudo = pseudo;
      playerStartCoordinate = new Coordinate(0, 0);
      setPlayerImage();
   }

   public Player(int id, Couleur color, String pseudo, Coordinate startCoordinate)
   {
      playerId = id;
      playerColor = color;
      playerPseudo = pseudo;
      playerStartCoordinate = startCoordinate;
      setPlayerImage();
   }

   public String toString()
   {
      return "Identifiant : " + playerId
         + ", Pseudo : " + playerPseudo
         + ", Couleur : " + playerColor
         + ", Coordonnées de départ : " + playerStartCoordinate;
   }


   /**
    * Initialise la bonne image pour le joueur
    */
   private void setPlayerImage()
   {
      int imgNum;
      if (playerId >= 0 && playerId <= 12)
         imgNum = playerId;
      else
         imgNum = 0;

      playerImage = new Texture(
         "img/" + imgNum + ".png",
         new Point(-500, -500)
      );
   }


   /* Getters and Setters */

   public int getPlayerId()
   {
      return playerId;
   }

   public void setPlayerId(int vid)
   {
      playerId = vid;
   }

   public Couleur getPlayerColor()
   {
      return playerColor;
   }

   public String getPlayerPseudo()
   {
      return playerPseudo;
   }

   public void setPlayerPseudo(String pseudo)
   {
      playerPseudo = pseudo;
   }

   public Coordinate getPlayerStartCoordinate()
   {
      return playerStartCoordinate;
   }

   public void setPlayerStartCoordinate(Coordinate coord)
   {
      playerStartCoordinate = coord;
   }

   public Texture getPlayerImage()
   {
      return playerImage;
   }
}
