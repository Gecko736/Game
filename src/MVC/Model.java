package MVC;

import java.util.ArrayList;

public class Model {
    public static final double coinSpawnRate = 0.9;
    public static final int initialCoins = 10;
    public static final int runningLow = 30;
    public static final Size board = new Size(7, 7);
    public static final Size coinGrid = new Size(board.width * 2, board.height * 2);

    /* Color Grid data and methods ********************************************/

    private static Color[][] coins = new Color[coinGrid.width][coinGrid.height];
    static {
        for (int i = 0; i < coins.length; i++)
            for (int j = 0; j < coins[i].length; j++)
                coins[j][i] = Color.None;
    }

    public static void addCoin(Coin coin) {
        if (coin.x < 0 || coin.x >= coinGrid.width)
            throw new IllegalArgumentException("X coordinate for new coin out of range: " + coin.x);
        if (coin.y < 0 || coin.y >= coinGrid.height)
            throw new IllegalArgumentException("Y coordinate for new coin out of range: " + coin.y);
        coins[coin.x][coin.y] = coin.color;
    }

    public static void removeCoin(int x, int y) {
        if (x < 0 || x >= coinGrid.width)
            throw new IllegalArgumentException("X coordinate out of range: " + x);
        if (y < 0 || y >= coinGrid.height)
            throw new IllegalArgumentException("Y coordinate out of range: " + y);
        if (coins[x][y] == Color.None)
            throw new IllegalStateException("No coin to remove at: " + x + ", " + y);
        coins[x][y] = Color.None;
    }

    public static Coin[] lookForCoins() {
        ArrayList<Coin> coins = new ArrayList<>();
        Coord[] coords = new Coord[]{
                new Coord(guy.x * 2, guy.y * 2),
                new Coord((guy.x * 2) + 1, guy.y * 2),
                new Coord(guy.x * 2, (guy.y * 2) + 1),
                new Coord((guy.x * 2) + 1, (guy.y * 2) + 1)
        };
        for (Coord c : coords) {
            if (Model.coins[c.x][c.y] != Color.None)
                coins.add(new Coin(c.x, c.y, Model.coins[c.x][c.y]));
        }
        return coins.toArray(new Coin[0]);
    }

    /* Guy coordinates data and methods **************************************/

    public static Coord guy = new Coord(board.width / 2, board.height / 2);

    public static void move(Dir direction) {
        switch (direction) {
            case Up: guy.y--; break;
            case Down: guy.y++; break;
            case Left: guy.x--; break;
            case Right: guy.x++; break;
        }
    }

    public static boolean canMove(Dir direction) {
        switch (direction) {
            case Up: return guy.y - 1 >= 0;
            case Down: return guy.y + 1 < board.height;
            case Left: return guy.x - 1 >= 0;
            case Right: return guy.x + 1 < board.width;
            default: return false;
        }
    }

    /* Hunger data and methods ***********************************************/

    private static final int eatValue = 5;
    private static final int moveValue = 1;
    private static final int hungerLimit = 125;

    private static int reds = 50;
    private static int blues = 50;
    private static int yellows = 50;

    public static int getReds() {
        return reds;
    }

    public static int getBlues() {
        return blues;
    }

    public static int getYellows() {
        return yellows;
    }

    public static void increase(Color color) throws Exception {
        switch (color) {
            case Red: incReds(); break;
            case Blue: incBlues(); break;
            case Yellow: incYellows(); break;
            default: throw new Exception("Attempt to consume a \"None\" type coin.");
        }
    }

    public static int[] getHungers() {
        return new int[]{reds, blues, yellows};
    }

    public static void incReds() {
        reds += eatValue;
        reds %= hungerLimit;
    }

    public static void incBlues() {
        blues += eatValue;
        blues %= hungerLimit;
    }

    public static void incYellows() {
        yellows += eatValue;
        yellows %= hungerLimit;
    }

    public static void decrement() {
        reds -= moveValue;
        blues -= moveValue;
        yellows -= moveValue;
    }

    public static void setAllZero() {
        reds = 0;
        blues = 0;
        yellows = 0;
    }

    public static boolean isDead() {
        return reds == 0 || blues == 0 || yellows == 0;
    }

    public static boolean wasStarving = false;

    public static boolean isStarving() {
        boolean starving = reds < runningLow || blues < runningLow || yellows < runningLow;
        if (!wasStarving)
            wasStarving = starving;
        return starving;
    }

    public static void notStarving() {
        wasStarving = false;
    }

    public static boolean hasWon() {
        return reds >= 100 && blues >= 100 && yellows >= 100;
    }
}
