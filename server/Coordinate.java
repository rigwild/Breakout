public class Coordinate
{
   private int x;
   private int y;

   public Coordinate()
   {
      x = 0;
      y = 0;
   }

   public Coordinate(int x, int y)
   {
      this.x = x;
      this.y = y;
   }

   public Coordinate(Coordinate toCopy)
   {
      x = toCopy.x;
      y = toCopy.y;
   }

   public String toString()
   {
      return "(" + x + ", " + y + ")";
   }

   /**
    * Indique si la coordonnée passée en paramètre est égale
    *
    * @param toTest La coordonnée à comparer
    * @return true = Coordonnées égales, false = Coordonnées non égales
    */
   public boolean equals(Coordinate toTest)
   {
      return (x == toTest.x && y == toTest.y);
   }

   public int getX()
   {
      return x;
   }

   public void setX(int x)
   {
      this.x = x;
   }

   public int getY()
   {
      return y;
   }

   public void setY(int y)
   {
      this.y = y;
   }


   /* Getters and Setters */

}
