import java.util.ArrayList;

public abstract class Game
{
   public ArrayList<Player> playerList;
   public Grid grid;
   public GraphicalInterface gui;
   public int nbGameTurn;
   public int gameTurnPlayer;

   public Game()
   {

   }

   abstract void initPlayer();

   abstract void gameTurn(Player player);
}
