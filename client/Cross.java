import MG2D.Fenetre;
import MG2D.geometrie.*;
import MG2D.geometrie.Point;
import MG2D.geometrie.Rectangle;

import java.awt.*;

/**
 * Class se basant sur MG2D pour générer une croix
 * Je n'ai pas bien compris comment créer la forme avec la classe dessin donc
 * j'ai fait en sorte de automatiquement l'ajouter à la fenêtre lors de l'instanciation au lieu de pouvoir
 * faire f.ajouter() comme avec les autres classes
 *
 * J'ai essayé d'imiter au maximum une classes de MG2D pour simplifier l'utilisation.
 * La croix est composé de 2 rectangles.
 */
class Cross
{
   private Fenetre window;
   private Point origin = new Point(0, 0);
   private Couleur color = Couleur.BLANC;
   private int size = 150;
   private int branchSize = 15;
   private boolean plein = false;
   private Rectangle rectangle1;
   private Rectangle rectangle2;


   /*Constructors*/

   public Cross(Fenetre window, int size)
   {
      this.size = size;
      this.window = window;
      initCross();
   }

   public Cross(Fenetre window, Point origin)
   {
      this.origin = origin;
      this.window = window;
      initCross();
   }

   public Cross(Fenetre window, Point origin, int size)
   {
      this.origin = origin;
      this.size = size;
      this.window = window;
      initCross();
   }

   public Cross(Fenetre window, Point origin, int size, int branchSize)
   {
      this.origin = origin;
      this.size = size;
      this.branchSize = branchSize;
      this.window = window;
      initCross();
   }

   public Cross(Fenetre window, Point origin, int size, boolean plein)
   {
      this.origin = origin;
      this.size = size;
      this.plein = plein;
      this.window = window;
      initCross();
   }

   public Cross(Fenetre window, Point origin, int size, int branchSize, boolean plein)
   {
      this.origin = origin;
      this.size = size;
      this.branchSize = branchSize;
      this.plein = plein;
      this.window = window;
      initCross();
   }

   public Cross(Fenetre window, Point origin, int size, Couleur color)
   {
      this.origin = origin;
      this.size = size;
      this.color = color;
      this.window = window;
      initCross();
   }

   public Cross(Fenetre window, Point origin, int size, Couleur color, int branchSize)
   {
      this.origin = origin;
      this.size = size;
      this.color = color;
      this.branchSize = branchSize;
      this.window = window;
      initCross();
   }

   public Cross(Fenetre window, Point origin, int size, Couleur color, int branchSize, boolean plein)
   {
      this.origin = origin;
      this.size = size;
      this.color = color;
      this.branchSize = branchSize;
      this.plein = plein;
      this.window = window;
      initCross();
   }

   /*Methods*/

   /**
    * Initialiser la croix et l'ajoute à la fenêtre
    */
   private void initCross()
   {
      //Rectangle horizontal
      rectangle1 = new Rectangle(
         color,
         new Point(origin.getX(), origin.getY() + size / 2 - branchSize / 2),
         new Point(origin.getX() + size, origin.getY() + size / 2 + branchSize / 2),
         plein
      );

      //Rectangle vertical
      rectangle2 = new Rectangle(
         color,
         new Point(origin.getX() + size / 2 - branchSize / 2, origin.getY()),
         new Point(origin.getX() + size / 2 + branchSize / 2, origin.getY() + size),
         plein
      );
      window.ajouter(rectangle1);
      window.ajouter(rectangle2);
   }


   /**
    * /!\ La méthode rectangle.setAB() n'existe pas /!\
    * Si déplacement trop important de la croix, le point A passe au dessus du point B, erreur !
    *   Met à jour la croix
    *   Ne rafraichit pas la fenêtre -> pour cas où mise à jour d'un grand nombre de croix
    */
   @Deprecated
   private void updateCross()
   {
      //Rectangle horizontal
      rectangle1.setA(new Point(
         origin.getX(),
         origin.getY() + size / 2 - branchSize / 2)
      );
      rectangle1.setB(new Point(
         origin.getX() + size,
         origin.getY() + size / 2 + branchSize / 2)
      );
      rectangle1.setCouleur(color);
      rectangle1.setPlein(plein);


      //Rectangle vertical
      rectangle2.setA(new Point(
         origin.getX() + size / 2 - branchSize / 2,
         origin.getY())
      );
      rectangle2.setB(new Point(
         origin.getX() + size / 2 + branchSize / 2,
         origin.getY() + size)
      );
      rectangle2.setCouleur(color);
      rectangle2.setPlein(plein);
   }

   /**
    * Supprime l'ancienne croix, en crée une nouvelle mise à jour et l'ajoute à la fenêtre.
    * Est moins efficace que updateCross mais évite toutes erreurs !
    */
   private void updateNewCross()
   {
      window.supprimer(rectangle1);
      window.supprimer(rectangle2);
      //Rectangle horizontal
      rectangle1 = new Rectangle(
         color,
         new Point(origin.getX(), origin.getY() + size / 2 - branchSize / 2),
         new Point(origin.getX() + size, origin.getY() + size / 2 + branchSize / 2),
         plein
      );

      //Rectangle vertical
      rectangle2 = new Rectangle(
         color,
         new Point(origin.getX() + size / 2 - branchSize / 2, origin.getY()),
         new Point(origin.getX() + size / 2 + branchSize / 2, origin.getY() + size),
         plein
      );
      window.ajouter(rectangle1);
      window.ajouter(rectangle2);
   }


   /*Getters and Setters*/

   public boolean isPlein()
   {
      return plein;
   }

   public void setPlein(boolean plein)
   {
      this.plein = plein;
      updateNewCross();
   }

   public int getSize()
   {
      return size;
   }

   public void setSize(int size)
   {
      this.size = size;
      updateNewCross();
   }

   public Couleur getColor()
   {
      return color;
   }

   public void setColor(Couleur color)
   {
      this.color = color;
      updateNewCross();
   }

   public Point getOrigin()
   {
      return origin;
   }

   public void setOrigin(Point origin)
   {
      this.origin = origin;
      updateNewCross();
   }
}
