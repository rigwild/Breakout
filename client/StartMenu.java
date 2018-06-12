import java.util.InputMismatchException;
import java.util.Scanner;

public class StartMenu
{
    public StartMenu()
    {
        System.out.println("\n################################");
        System.out.println("  Bienvenue sur le Breakwild !");
        System.out.println("################################\n\n");

        menu();
    }

    /**
     * Affiche le menu de sélection du mode
     */
    public void menu()
    {
        String msg = "Choisissez un mode de jeu !\n"
                + "1. Partie locale\n"
                + "2. Partie en ligne\n\n->";

        String errorMsg = "Entrée non reconnue. Veuillez réessayer.\n";

        System.out.println(msg);
        Scanner sc = new Scanner(System.in);
        int choice;
        try
        {
            choice = sc.nextInt();
        }
        catch (InputMismatchException e)
        {
            choice = -1;
            sc.nextLine();
        }
        while (!(choice <= 2 && choice > 1))
        {
            System.out.print(errorMsg + msg);
            try
            {
                choice = sc.nextInt();
            }
            catch (InputMismatchException e)
            {
                choice = -1;
                sc.nextLine();
            }
        }

        switch (choice)
        {
            case 1:
                startLocalGame();
                break;
            case 2:
                startOnlineGame();
                break;
        }
    }

    private void startLocalGame()
    {
        GameLocal launcher = new GameLocal();
    }

    private void startOnlineGame()
    {
        //Récupérer l'adresse IP à utiliser
        System.out.print("Entrez l'adresse IP du serveur [127.0.0.1] : ");

        Scanner sc = new Scanner(System.in);
        String IP = sc.nextLine();
        if (IP.equals(""))
            IP = "127.0.0.1";


        //Récupérer le port à utiliser
        System.out.print("Entrez le port du serveur [8887] :");
        String temp = sc.nextLine();
        int port;
        if (temp.equals(""))
            port = 8887;
        try {
            port = Integer.parseInt(temp);
        }
        catch (NumberFormatException e)
        {
            port = 8887;
        }

        System.out.println("Connexion au serveur "+IP + ":" + String.valueOf(port)+" ...");
        GameOnlineClient launcher = new GameOnlineClient(IP, port);
    }
}