import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Config
{
   private static int gridSizeX = 15;
   private static int gridSizeY = 10;

   private static int gameAreaSizeX = 900;
   private static int gameAreaSizeY = 800;

   private static int windowSizeX = 1200;
   private static int windowSizeY = 1000;

   private static int fps = 10;


   /* Getters and Setters */

   public static int getGridSizeY()
   {
      return gridSizeY;
   }

   public static void setGridSizeY(int gridSizeY)
   {
      Config.gridSizeY = gridSizeY;
   }

   public static int getGridSizeX()
   {
      return gridSizeX;
   }

   public static void setGridSizeX(int gridSizeX)
   {
      Config.gridSizeX = gridSizeX;
   }

   public static int getWindowSizeX()
   {
      return windowSizeX;
   }

   public static void setWindowSizeX(int windowSizeX)
   {
      Config.windowSizeX = windowSizeX;
   }

   public static int getWindowSizeY()
   {
      return windowSizeY;
   }

   public static void setWindowSizeY(int windowSizeY)
   {
      Config.windowSizeY = windowSizeY;
   }

   public static int getGameAreaSizeX()
   {
      return gameAreaSizeX;
   }

   public static void setGameAreaSizeX(int gameAreaSizeX)
   {
      Config.gameAreaSizeX = gameAreaSizeX;
   }

   public static int getGameAreaSizeY()
   {
      return gameAreaSizeY;
   }

   public static void setGameAreaSizeY(int gameAreaSizeY)
   {
      Config.gameAreaSizeY = gameAreaSizeY;
   }

   public static int getFps()
   {
      return fps;
   }

   public static void setFps(int fps)
   {
      Config.fps = fps;
   }

   /*Méthodes utiles*/
   public static boolean matchesRegex(String regexp, String str)
   {
      try
      {
         Pattern p = Pattern.compile(regexp);
         Matcher m = p.matcher(str);
         int count = 0;
         while (m.find())
            count++;
         return (count > 0);
      }
      catch (Exception e)
      {
         return false;
      }
   }

   public static ArrayList<String> getRegexResult(String regexp, String str)
   {
      ArrayList<String> result = new ArrayList<>();
      try
      {
         Pattern p = Pattern.compile(regexp);
         Matcher m = p.matcher(str);
         m.matches();
         for (int i = 1; i <= m.groupCount(); i++)
         {
            result.add(m.group(i));
         }
         return result;
      }
      catch (Exception e)
      {
         return result;
      }
   }

   public static int rand(int min, int max)
   {
      Random rand = new Random();
      return min + rand.nextInt((max - min) + 1);
   }
}
