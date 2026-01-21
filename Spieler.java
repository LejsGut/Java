public class Spieler {
    String name;
    int[][] feld;      // 0 = leer, 1 = Schiff, 2 = Treffer, 3 = Daneben
    int schiffTeile;

    public Spieler(String n) {
        name = n;
        feld = new int[5][5];
        schiffTeile = 2;
    }

    public void schiffSetzen(int zeile, int spalte) {
        feld[zeile][spalte] = 1;
        feld[zeile][spalte + 1] = 1;
    }

    public int schuss(int zeile, int spalte) {
        if (feld[zeile][spalte] == 1) {
            feld[zeile][spalte] = 2;
            schiffTeile--;
            return 2; // Treffer
        }
        if (feld[zeile][spalte] == 0) {
            feld[zeile][spalte] = 3;
            return 3; // Daneben
        }
        return feld[zeile][spalte]; // schon beschossen
    }

    public boolean istVersenkt() {
        return schiffTeile == 0;
    }

    public void spielfeldAnzeigen() {
        System.out.println("Spielfeld von " + name);
        System.out.println("  0 1 2 3 4");

        for (int i = 0; i < 5; i++) {
            System.out.print(i + " ");
            for (int j = 0; j < 5; j++) {
                if (feld[i][j] == 2) {
                    System.out.print("X ");
                } else if (feld[i][j] == 3) {
                    System.out.print("O ");
                } else {
                    System.out.print("~ ");
                }
            }
            System.out.println();
        }
    }
}
