package MVC;

public class Control {
    public static void main(String[] args) {

    }

    private static View view;
    public static void setView(View _view) {
        if (view == null)
            view = _view;
    }

    /* methods moving data from Model to View ********************************/

    public static int boardWidth() {
        return Model.board.width;
    }

    public static int boardHeight() {
        return Model.board.height;
    }

    public static int coinGridWidth() {
        return Model.coinGrid.width;
    }

    public static int coinGridHeight() {
        return Model.coinGrid.height;
    }

    public static void errorCaught() {
        Model.setAllZero();
    }

    public static int guyX() {
        return Model.guy.x;
    }

    public static int guyY() {
        return Model.guy.y;
    }

    public static int getInitialCoins() {
        return Model.initialCoins;
    }

    /* methods moving data from View to Model ********************************/

    private static boolean gameOver = false;

    public static void keyPressed(char key) throws Exception {
        if (!gameOver && moveGuy(key)) {
            Model.decrement();
            for (Coin c : Model.lookForCoins()) {
                if (!view.removeCoin(c.x, c.y))
                    throw new Exception("Coin found at (" + c.x + ", " + c.y + ") in Model but not in View");
                Model.removeCoin(c.x, c.y);
                Model.increase(c.color);
            }
            maybeAddRandomCoin();
            if (Model.isDead()) {
                view.setGameOver(false);
                gameOver = true;
            } else if (Model.hasWon()) {
                view.setGameOver(true);
                gameOver = true;
            } else if (Model.isStarving())
                view.makeStarving();
            else if (Model.wasStarving) {
                view.notStarving();
                Model.notStarving();
            }
            view.updateHunger(Model.getHungers());
        }
    }

    private static boolean moveGuy(char key) {
        boolean validMove = false;
        switch (key) {
            case 'w':
                if (Model.canMove(Dir.Up)) {
                    Model.move(Dir.Up);
                    view.move(Dir.Up);
                    validMove = true;
                }
                break;
            case 's':
                if (Model.canMove(Dir.Down)) {
                    Model.move(Dir.Down);
                    view.move(Dir.Down);
                    validMove = true;
                }
                break;
            case 'a':
                if (Model.canMove(Dir.Left)) {
                    Model.move(Dir.Left);
                    view.move(Dir.Left);
                    validMove = true;
                }
                break;
            case 'd':
                if (Model.canMove(Dir.Right)) {
                    Model.move(Dir.Right);
                    view.move(Dir.Right);
                    validMove = true;
                }
                break;
        }
        return validMove;
    }

    public static void maybeAddRandomCoin() {
        if (Math.random() < Model.coinSpawnRate) {
            Coord c = randomCoinCoord();
            Coin coin;
            switch ((int) (Math.random() * 3)) {
                case 0:
                    coin = new Coin(c.x, c.y, Color.Red);
                    break;
                case 1:
                    coin = new Coin(c.x, c.y, Color.Blue);
                    break;
                default:
                    coin = new Coin(c.x, c.y, Color.Yellow);
                    break;
            }
            view.addCoin(coin);
            Model.addCoin(coin);
        }
    }

    public static Coord randomCoinCoord() {
        int x = (int) (Math.random() * Model.coinGrid.width);
        int y = (int) (Math.random() * Model.coinGrid.height);
        if (x != Model.guy.x * 2 && x != (Model.guy.x * 2) + 1 &&
                y != Model.guy.y * 2 && y != (Model.guy.y * 2) + 1)
            return new Coord(x, y);
        return randomCoinCoord();
    }
}
