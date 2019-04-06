package MVC;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

public class View extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    /* location data *********************************************************/
    
    private final int padding = 10;
    private final int fontSize = 20;
    private final Font font = new Font(20);

    private final Size tile = new Size(72, 72);
    private final Coord boardStart = new Coord(padding, padding);
    private final Coord boardEnd = new Coord(
            padding + (tile.width * Control.boardWidth()),
            padding + (tile.height * Control.boardHeight())
    );
    private final Coord sideBar = new Coord((tile.width * Control.boardWidth()) + (padding * 2), padding);
    private final Coord guyOffset = new Coord(20, 20);
    private final Coord[][] coinOffset = new Coord[][]{
            {new Coord(5, 5), new Coord(5, 57)},
            {new Coord(57, 5), new Coord(57, 57)}
    };

    /* subview variables *****************************************************/
    
    private Gif guy;
    private Text redsLabel = new Text(
            sideBar.x, sideBar.y + fontSize,
            "Reds: " + Model.getReds()
    );
    private Text bluesLabel = new Text(
            sideBar.x, sideBar.y + (fontSize * 2) + padding,
            "Blues: " + Model.getBlues()
    );
    private Text yellowsLabel = new Text(
            sideBar.x, sideBar.y + (fontSize * 3) + (padding * 2),
            "Yellows: " + Model.getYellows()
    );
    private Text gameOverAlert = new Text(
            sideBar.x, sideBar.y + (fontSize * 4) + (padding * 3),
            ""
    );
    { // setting color and size of each label
        redsLabel.setFont(font);
        bluesLabel.setFont(font);
        yellowsLabel.setFont(font);
        gameOverAlert.setFont(font);
        redsLabel.setFill(javafx.scene.paint.Color.RED);
        bluesLabel.setFill(javafx.scene.paint.Color.BLUE);
        yellowsLabel.setFill(javafx.scene.paint.Color.YELLOW);
        gameOverAlert.setFill(javafx.scene.paint.Color.RED);
    }

    // list of valid key inputs
    private HashMap<KeyCode, Character> keyChars = new HashMap<>();
    {
        keyChars.put(KeyCode.SPACE, ' ');
        keyChars.put(KeyCode.ENTER, '\'');
        keyChars.put(KeyCode.W, 'w');
        keyChars.put(KeyCode.A, 'a');
        keyChars.put(KeyCode.S, 's');
        keyChars.put(KeyCode.D, 'd');
    }

    private Pane map;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Game");
        makeMap();

        Control.setView(this);
        redCoin = new Image(new FileInputStream("gifs/red-coin.gif"));
        blueCoin = new Image(new FileInputStream("gifs/blue-coin.gif"));
        yellowCoin = new Image(new FileInputStream("gifs/yellow-coin.gif"));
        for (int i = 0; i < Control.getInitialCoins(); i++)
            Control.addRandomCoin();

        Scene scene = new Scene(map);
        scene.setOnKeyPressed((e) -> {
            if (keyChars.containsKey(e.getCode())) {
                try {
                    if (e.isShiftDown())
                        Control.keyPressed(Character.toUpperCase(keyChars.get(e.getCode())));
                    else {
                        char key = keyChars.get(e.getCode());
                        Control.keyPressed(key);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /* private methods *******************************************************/

    private void makeMap() {
        map = new Pane();
        map.setMinSize(
                ((Control.boardWidth() + 2) * tile.width) + (padding * 2),
                (Control.boardHeight() * tile.height) + (padding * 2)
        );
        ObservableList<Node> kids = map.getChildren();
        int guyX = (Control.guyX() * tile.width) + guyOffset.x + padding;
        int guyY = (Control.guyY() * tile.height) + guyOffset.y + padding;
        guy = new Gif("gifs/eating.gif", guyX, guyY);
        kids.addAll(redsLabel, bluesLabel, yellowsLabel, gameOverAlert, guy.gif);
        for (int i = 0; i <= Control.boardWidth(); i++) // add the vertical lines
            kids.add(new Line(
                    boardStart.x + (i * tile.width), boardStart.y,
                    boardStart.x + (i * tile.width), boardEnd.y)
            );
        for (int i = 0; i <= Control.boardHeight(); i++) // add the horizontal lines
            kids.add(new Line(
                    boardStart.x, boardStart.y + (i * tile.width),
                    boardEnd.x, boardStart.y + (i * tile.width))
            );
    }

    private class Gif {
        private ImageView gif;
        private Color color;

        private Gif(Color color, double x, double y) {
            gif = new ImageView(getImage(color));
            gif.relocate(x, y);
            this.color = color;
        }

        private Gif(String fileName, double x, double y) {
            try {
                gif = new ImageView(new Image(new FileInputStream(fileName)));
                gif.relocate(x, y);
                color = Color.None;
            } catch (FileNotFoundException e) {
                catchFileNotFound(e);
            }
        }

        private Color getColor() {
            return color;
        }

        private void setImage(String fileName) {
            try {
                gif.setImage(new Image(new FileInputStream(fileName)));
            } catch (FileNotFoundException e) {
                catchFileNotFound(e);
            }
        }

        private void catchFileNotFound(FileNotFoundException e) {
            Control.errorCaught();
            gameOverAlert.setText("404");
            gameOverAlert.setText("Error");
            System.out.println(e.getMessage());
        }
    }

    /* public methods ********************************************************/

    /* *** data and methods for moving and updating the player icon **********/

    /**
     * Method moves the player icon around the board.
     * 
     * @param direction the direction in which the player icon should move
     */
    public void move(Dir direction) {
        switch (direction) {
            case Up: guy.gif.setY(guy.gif.getY() - tile.height); break;
            case Down: guy.gif.setY(guy.gif.getY() + tile.height); break;
            case Left: guy.gif.setX(guy.gif.getX() - tile.width); break;
            case Right: guy.gif.setX(guy.gif.getX() + tile.width); break;
        }
    }

    public void makeStarving() {
        guy.setImage("gifs/hungry.gif");
    }

    public void notStarving() {
        guy.setImage("gifs/eating.gif");
    }

    /* *** data and methods for adding and removing coins ********************/
    
    // Only instantiate the Image object for each coin once, and have each
    // instance of a coin have its own ImageView.
    // These three variables will be instantiated in the start() method.
    private Image redCoin;
    private Image blueCoin;
    private Image yellowCoin;

    // represents all the possible locations of coins on the board
    private Gif[][] coinGrid = new Gif[Control.coinGridWidth()][Control.coinGridHeight()];
    
    /**
     * Method adds a coin of a given color to a given location on the coinGrid.
     * 
     * @param coin the x and y coordinates and the color of the new coin
     */
    public void addCoin(Coin coin) {
        if (coinGrid[coin.x][coin.y] != null) {
            map.getChildren().remove(coinGrid[coin.x][coin.y].gif);
            coinGrid[coin.x][coin.y] = null;
        }
        Coord c = convertCoinCoords(coin.x, coin.y);
        coinGrid[coin.x][coin.y] = new Gif(coin.color, c.x, c.y);
        map.getChildren().add(coinGrid[coin.x][coin.y].gif);
    }

    public boolean removeCoin(int x, int y) {
        if (coinGrid[x][y] == null)
            return false;
        map.getChildren().remove(coinGrid[x][y].gif);
        coinGrid[x][y] = null;
        return true;
    }
    
    private Image getImage(Color color) {
        switch (color) {
            case Red: return redCoin;
            case Blue: return blueCoin;
            case Yellow: return yellowCoin;
            default: return null;
        }
    }

    private Coord convertCoinCoords(int x, int y) {
        int newX = padding + ((x / 2) * tile.width) + coinOffset[x % 2][y % 2].x;
        int newY = padding + ((y / 2) * tile.height) + coinOffset[x % 2][y % 2].y;
        return new Coord(newX, newY);
    }

    /* *** methods for updating labels ***************************************/

    public void updateHunger(int[] hungers) {
        redsLabel.setText("Reds: " + hungers[0]);
        bluesLabel.setText("Blues: " + hungers[1]);
        yellowsLabel.setText("Yellows: " + hungers[2]);
    }

    public void setGameOver(boolean win) {
        if (win) {
            gameOverAlert.setText("You won!");
            gameOverAlert.setFill(javafx.scene.paint.Color.GREEN);
            guy.setImage("gifs/win.png");
        } else {
            gameOverAlert.setText("You lose");
            guy.setImage("gifs/death.gif");
        }
    }
}
