package projekt;

    import java.util.Random;
	import java.util.Scanner;
	public static void main(String[] args) {
	// Scanner für Benutzereingaben
	Scanner scanner = new Scanner(System.in);

	// Zufallszahlen für die Schiffsposition
	Random random = new Random();

	// Größe des Spielfelds (5x5)
	int SPIELFELD_GROESSE = 5;

	// Anzahl der Versuche
	int MAX_VERSUCHE = 5;

	// Spielfeld erstellen (2D-Array)
	// '~' bedeutet Wasser
	char[][] spielfeld = new char[SPIELFELD_GROESSE][SPIELFELD_GROESSE] 

	// Spielfeld mit Wasser füllen
	for (int i = 0;i<SPIELFELD_GROESSE;i++) {
		for (int j = 0; j < SPIELFELD_GROESSE; j++) {
			spielfeld[i][j] = '~';
		}
	}

	// Schiff zufällig platzieren
	int schiffZeile = random.nextInt(SPIELFELD_GROESSE);
	int schiffSpalte = random.nextInt(SPIELFELD_GROESSE);

	// Begrüßung
	System.out.println("Willkommen bei Schiffe versenken!");System.out.println("Es gibt ein Schiff auf einem 5x5 Spielfeld.");System.out.println("Du hast "+MAX_VERSUCHE+" Versuche.\n");

	// Haupt-Spiel-Schleife
	for (int versuch = 1;versuch<=MAX_VERSUCHE;versuch++) {

		// Spielfeld anzeigen
		System.out.println("Aktuelles Spielfeld:");
		for (int i = 0; i < SPIELFELD_GROESSE; i++) {
			for (int j = 0; j < SPIELFELD_GROESSE; j++) {
				System.out.print(spielfeld[i][j] + " ");
			}
			System.out.println();
		}

		// Benutzereingabe für Zeile
		System.out.print("\nRate die Zeile (0-4): ");
		int geratenZeile = scanner.nextInt();

		// Benutzereingabe für Spalte
		System.out.print("Rate die Spalte (0-4): ");
		int geratenSpalte = scanner.nextInt();

		// Prüfen, ob Eingabe im Spielfeld liegt
		if (geratenZeile < 0 || geratenZeile >= SPIELFELD_GROESSE || geratenSpalte < 0
				|| geratenSpalte >= SPIELFELD_GROESSE) {

			System.out.println("Das liegt außerhalb des Spielfelds!");
			versuch--;
			continue;
		}

		// Prüfen, ob das Schiff getroffen wurde
		if (geratenZeile == schiffZeile && geratenSpalte == schiffSpalte) {

			// Treffer markieren
			spielfeld[geratenZeile][geratenSpalte] = 'X';

			System.out.println("\n TREFFER! Du hast das Schiff versenkt!");
		    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~"); // Wasseroberfläche
	        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	        System.out.println("~~~~~~~   .____            ");
	        System.out.println("~~~~~~~  /____/\\          ");
	        System.out.println("~~~~~~~  \\    \\ \\        ");
	        System.out.println("~~~~~~~   \\____\\_\\       ");
	        System.out.println("~~~~~~~     |_____|       ");
	        System.out.println("~~~~~~~      \\___/        ");
	        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	
	        System.out.println("Du hast alle Schiffe versunken herzlichen glückwunsch! ");
			break;

		} else {

			// Prüfen, ob Stelle schon geraten wurde
			if (spielfeld[geratenZeile][geratenSpalte] == 'O') {
				System.out.println("Diese Stelle hast du schon versucht!");
				versuch--;
			} else {
				// Fehlschuss markieren
				spielfeld[geratenZeile][geratenSpalte] = 'O';
				System.out.println(" Daneben!");
			
			    System.out.println("           |\\");
		        System.out.println("           | \\");
		        System.out.println("           |  \\");
		        System.out.println("           |___\\");
		        System.out.println("          /_____|");
		        System.out.println("  ~~~~~~~/______\\~~~~~~~");
		        System.out.println("    ~~~~/________\\~~~~");
		        System.out.println("      ~~\\________/~~");
		        System.out.println("        ~~~~~~~~~~");
		
			
			
			}
		}

		// Letzter Versuch?
		if (versuch == MAX_VERSUCHE) {
			System.out.println("\n Game Over!");
			System.out.println("Das Schiff war bei Zeile " + schiffZeile + ", Spalte " + schiffSpalte);
		}

		System.out.println();
	}

	// Scanner schließen
	scanner.close();

// schiffe versenken mit switch case 




}
	}

