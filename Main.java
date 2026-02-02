import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;

public class Main {

    static final int N = 5;

    static class Player {
        String name;
        int[][] grid = new int[N][N];
        int shipParts = 2;
        boolean shipPlaced = false;

        Player(String name) { this.name = name; }

        boolean placeShip(int r, int c, Orientation o) {
            if (r < 0 || r >= N || c < 0 || c >= N) return false;

            if (o == Orientation.HORIZONTAL) {
                // (r,c) und (r,c+1)
                if (c >= N - 1) return false;
                if (grid[r][c] != 0) return false;
                if (grid[r][c + 1] != 0) return false;

                grid[r][c] = 1;
                grid[r][c + 1] = 1;
            } else {
                // VERTICAL: (r,c) und (r+1,c)
                if (r >= N - 1) return false;
                if (grid[r][c] != 0) return false;
                if (grid[r + 1][c] != 0) return false;

                grid[r][c] = 1;
                grid[r + 1][c] = 1;
            }

            shipPlaced = true;
            return true;
        }

        // returns: 2 hit, 3 miss, 9 already shot
        int shoot(int r, int c) {
            if (grid[r][c] == 2 || grid[r][c] == 3) return 9;
            if (grid[r][c] == 1) {
                grid[r][c] = 2;
                shipParts--;
                return 2;
            }
            grid[r][c] = 3;
            return 3;
        }

        boolean isSunk() { return shipParts == 0; }
    }

    enum State { TITLE, PLACE_P1, PLACE_P2, PLAY, GAME_OVER }
    enum Orientation { HORIZONTAL, VERTICAL }

    public static void main(String[] args) throws IOException {
        DefaultTerminalFactory factory = new DefaultTerminalFactory();
        Screen screen = factory.createScreen();
        screen.startScreen();
        screen.setCursorPosition(null);

        try {
            run(screen);
        } finally {
            screen.stopScreen();
        }
    }

    static void run(Screen screen) throws IOException {
        Player p1 = new Player("Spieler 1");
        Player p2 = new Player("Spieler 2");

        Player attacker = p1;
        Player defender = p2;


        State state = State.TITLE;

        int selR = 0, selC = 0;
        String message = "";
        Player winner = null;

        Orientation orientation = Orientation.HORIZONTAL; // default

        String[] titleArt = new String[] {
                " __      __ ______  _____   _____  ______ _   _ _  __ _______ ",
                " \\ \\    / /|  ____||  __ \\ / ____||  ____| \\ | | |/ /|__   __|",
                "  \\ \\  / / | |__   | |__) | (___  | |__  |  \\| | ' /    | |   ",
                "   \\ \\/ /  |  __|  |  _  / \\___ \\ |  __| | . ` |  <     | |   ",
                "    \\  /   | |____ | | \\ \\ ____) || |____| |\\  | . \\    | |   ",
                "     \\/    |______||_|  \\_\\_____/ |______|_| \\_|_|\\_\\   |_|   "
        };

        while (true) {
            screen.clear();
            TextGraphics g = screen.newTextGraphics();
            TerminalSize size = screen.getTerminalSize();

            switch (state) {
                case TITLE -> {
                    drawCenteredAscii(g, size, titleArt, 2);
                    drawCenteredText(g, size, "ENTER = Start", 10);
                    drawCenteredText(g, size, "ESC = Beenden", 11);
                }

                case PLACE_P1, PLACE_P2 -> {
                    Player current = (state == State.PLACE_P1) ? p1 : p2;

                    String oText = (orientation == Orientation.HORIZONTAL) ? "HORIZONTAL" : "VERTIKAL";
                    drawHeader(g, size,
                            current.name + ": Schiff setzen (Länge 2)",
                            "ENTER=Setzen | Pfeile=Bewegen | H=Horizontal | V=Vertikal | ESC=Quit");

                    drawBoard(g, 2, 3, current, true, selR, selC);

                    g.setForegroundColor(TextColor.ANSI.CYAN);
                    g.putString(2, 3 + N * 2 + 3, "Ausrichtung: " + oText);
                    g.setForegroundColor(TextColor.ANSI.DEFAULT);

                    g.putString(2, 3 + N * 2 + 5, message);
                }

                case PLAY -> {
                    drawHeader(g, size,
                            attacker.name + " schießt auf " + defender.name,
                            "ENTER=Schuss | Pfeile=Bewegen | ESC=Quit");

                    drawBoard(g, 2, 3, defender, false, selR, selC);

                    g.putString(2, 3 + N * 2 + 3, message);
                }

                case GAME_OVER -> {
                    drawCenteredText(g, size, "GAME OVER", 2);
                    drawCenteredText(g, size, "Gewinner: " + (winner != null ? winner.name : "?"), 4);
                    drawCenteredText(g, size, "ENTER = Beenden", 6);
                }
            }

            screen.refresh();

            KeyStroke k = screen.readInput();
            if (k == null) continue;

            // Global quit
            if (k.getKeyType() == KeyType.Escape) break;

            if (state == State.GAME_OVER) {
                if (k.getKeyType() == KeyType.Enter) break;
                continue;
            }

            if (state == State.TITLE) {
                if (k.getKeyType() == KeyType.Enter) {
                    state = State.PLACE_P1;
                    selR = selC = 0;
                    orientation = Orientation.HORIZONTAL;
                    message = "Wähle Start-Zelle und drücke ENTER.";
                }
                continue;
            }

            // Set orientation only during placement
            if ((state == State.PLACE_P1 || state == State.PLACE_P2) && k.getKeyType() == KeyType.Character) {
                char ch = Character.toUpperCase(k.getCharacter());
                if (ch == 'H') {
                    orientation = Orientation.HORIZONTAL;
                    message = "Ausrichtung: HORIZONTAL";
                } else if (ch == 'V') {
                    orientation = Orientation.VERTICAL;
                    message = "Ausrichtung: VERTIKAL";
                }
            }

            // Movement
            if (k.getKeyType() == KeyType.ArrowUp) selR = (selR + N - 1) % N;
            if (k.getKeyType() == KeyType.ArrowDown) selR = (selR + 1) % N;
            if (k.getKeyType() == KeyType.ArrowLeft) selC = (selC + N - 1) % N;
            if (k.getKeyType() == KeyType.ArrowRight) selC = (selC + 1) % N;

            // ENTER actions
            if (k.getKeyType() == KeyType.Enter) {

                if (state == State.PLACE_P1 || state == State.PLACE_P2) {
                    Player current = (state == State.PLACE_P1) ? p1 : p2;

                    if (current.shipPlaced) {
                        message = "Du hast schon gesetzt.";
                        continue;
                    }

                    boolean ok = current.placeShip(selR, selC, orientation);
                    if (!ok) {
                        if (orientation == Orientation.HORIZONTAL) {
                            message = "Ungültig: Horizontal braucht Start-Spalte 0-3 und Felder frei.";
                        } else {
                            message = "Ungültig: Vertikal braucht Start-Zeile 0-3 und Felder frei.";
                        }
                        continue;
                    }

                    if (state == State.PLACE_P1) {
                        state = State.PLACE_P2;
                        selR = selC = 0;
                        orientation = Orientation.HORIZONTAL;
                        message = "Spieler 2: Setze dein Schiff (H/V möglich).";
                    } else {
                        state = State.PLAY;
                        attacker = p1;
                        defender = p2;
                        advantage = null;
                        selR = selC = 0;
                        message = "Spieler 1 startet. Wähle Feld + ENTER.";
                    }
                    continue;
                }

                if (state == State.PLAY) {
                    int res = defender.shoot(selR, selC);

                    if (res == 9) {
                        message = "Da wurde schon geschossen!";
                        continue;
                    }

                    if (res == 2) {
                        message = "TREFFER!";
                        if (defender.isSunk()) {
                            winner = attacker;
                            state = State.GAME_OVER;
                            continue;
                        }
                    } else if (res == 3) {
                        message = "DANEBEN.";
                    }

                    // Switch players
                    Player tmp = attacker;
                    attacker = defender;
                    defender = tmp;
                }
            }
        }
    }

    static void drawHeader(TextGraphics g, TerminalSize size, String title, String hint) {
        g.setForegroundColor(TextColor.ANSI.WHITE);
        g.putString(2, 1, title);
        g.setForegroundColor(TextColor.ANSI.GREEN);
        g.putString(2, 2, hint);
        g.setForegroundColor(TextColor.ANSI.DEFAULT);
    }

    static void drawBoard(TextGraphics g, int x, int y, Player view, boolean showShip, int selR, int selC) {
        g.setForegroundColor(TextColor.ANSI.WHITE);
        g.putString(x, y, "  0 1 2 3 4");

        for (int r = 0; r < N; r++) {
            g.putString(x, y + 1 + r, r + " ");
            for (int c = 0; c < N; c++) {
                boolean selected = (r == selR && c == selC);

                if (selected) {
                    g.setBackgroundColor(TextColor.ANSI.WHITE);
                    g.setForegroundColor(TextColor.ANSI.BLACK);
                } else {
                    g.setBackgroundColor(TextColor.ANSI.DEFAULT);
                    g.setForegroundColor(TextColor.ANSI.DEFAULT);
                }

                char ch = '~';
                int v = view.grid[r][c];
                if (v == 2) ch = 'X';
                else if (v == 3) ch = 'O';
                else if (showShip && v == 1) ch = 'S';

                g.putString(x + 2 + c * 2, y + 1 + r, String.valueOf(ch));
            }

            g.setBackgroundColor(TextColor.ANSI.DEFAULT);
            g.setForegroundColor(TextColor.ANSI.DEFAULT);
        }
    }

    static void drawCenteredText(TextGraphics g, TerminalSize size, String text, int row) {
        int col = Math.max(0, (size.getColumns() - text.length()) / 2);
        g.putString(col, row, text);
    }

    static void drawCenteredAscii(TextGraphics g, TerminalSize size, String[] lines, int startRow) {
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            int col = Math.max(0, (size.getColumns() - line.length()) / 2);
            int row = startRow + i;
            if (row >= 0 && row < size.getRows()) {
                g.putString(col, row, line);
            }
        }
    }
}