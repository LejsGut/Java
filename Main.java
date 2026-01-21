import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Spieler p1 = new Spieler("Spieler 1");
        Spieler p2 = new Spieler("Spieler 2");

        System.out.println("Spieler 1: Schiff setzen (Zeile 0-4, Spalte 0-3)");
        p1.schiffSetzen(sc.nextInt(), sc.nextInt());

        System.out.println("Spieler 2: Schiff setzen (Zeile 0-4, Spalte 0-3)");
        p2.schiffSetzen(sc.nextInt(), sc.nextInt());

        Spieler angreifer = p1;
        Spieler verteidiger = p2;

        while (true) {

            verteidiger.spielfeldAnzeigen();

            System.out.println(angreifer.name + " schießt! (Zeile Spalte)");
            int z = sc.nextInt();
            int s = sc.nextInt();

            int ergebnis = verteidiger.schuss(z, s);

            if (ergebnis == 2) {
                if (verteidiger.istVersenkt()) {
                    System.out.println("Versenkt");
                    break;
                } else {
                    System.out.println("Treffer");
                }
            } else if (ergebnis == 3) {
                System.out.println("Daneben");
            } else {
                System.out.println("Hier wurde schon geschossen!");
            }

            // Spieler wechseln
            Spieler temp = angreifer;
            angreifer = verteidiger;
            verteidiger = temp;
        }

        sc.close();
    }
}
