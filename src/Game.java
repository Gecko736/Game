import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Game extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    private boolean gameOver = false;

    private int guyX = 0;
    private int guyY = 0;

    private int reds = 50;
    private int blues = 50;
    private int yellows = 50;

    private Text redsLabel = new Text(630, 30, "Reds: " + reds + "/100");
    private Text bluesLabel = new Text(630, 60, "Blues: " + blues + "/100");
    private Text yellowsLabel = new Text(630, 90, "Yellows: " + yellows + "/100");
    private Text gameOverAlert = new Text(630, 120, "Game Over");

    {
        redsLabel.setFont(new Font(20));
        bluesLabel.setFont(new Font(20));
        yellowsLabel.setFont(new Font(20));
        gameOverAlert.setFont(new Font(20));
        redsLabel.setFill(Color.RED);
        bluesLabel.setFill(Color.BLUE);
        yellowsLabel.setFill(Color.YELLOW);
        gameOverAlert.setFill(Color.RED);
    }

    private Pane map = makeMap();

    private Gif guy = new Gif("gifs/eating.gif", 34, 34);

    private Gif[][] coins = new Gif[12][12];

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Game");

        map.getChildren().addAll(guy.gif, redsLabel, bluesLabel, yellowsLabel);

        Scene scene = new Scene(map);

        scene.setOnKeyPressed(e -> {
            char c = getKeyChar(e);
            if (c == 'S')
                eatThings();
            else if (c == 'P') {
                reds += 50;
                blues += 50;
                yellows += 50;
                updateHunger();
            } else if (c != 'n' && !gameOver) {
                reds--;
                blues--;
                yellows--;
                updateHunger();

                if (c == 'w') {
                    guyY--;
                    guy.gif.setY((guyY * 100));
                } else if (c == 's') {
                    guyY++;
                    guy.gif.setY((guyY * 100));
                } else if (c == 'a') {
                    guyX--;
                    guy.gif.setX((guyX * 100));
                } else if (c == 'd') {
                    guyX++;
                    guy.gif.setX((guyX * 100));
                }

                if (Math.random() > 0.30)
                    addRandomCoin();
            }
        });
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private char getKeyChar(KeyEvent e) {
        if (e.getCode() == KeyCode.W && guyY > 0)
            return 'w';
        else if (e.getCode() == KeyCode.S && guyY < 5)
            return 's';
        else if (e.getCode() == KeyCode.A && guyX > 0)
            return 'a';
        else if (e.getCode() == KeyCode.D && guyX < 5)
            return 'd';
        else if (e.getCode() == KeyCode.SPACE)
            return 'S';
        else if (e.getCode() == KeyCode.P && e.isShiftDown())
            return 'P';
        else
            return 'n';
    }

    private void updateHunger() {
        if (!gameOver) {
            if (reds > 125)
                reds = 125;
            if (blues > 125)
                blues = 125;
            if (yellows > 125)
                yellows = 125;

            redsLabel.setText("Reds: " + reds + "/100");
            bluesLabel.setText("Blues: " + blues + "/100");
            yellowsLabel.setText("Yellow: " + yellows + "/100");

            if (reds <= 0 || blues <= 0 || yellows <= 0) {
                gameOver = true;
                map.getChildren().add(gameOverAlert);
                guy.setImage("gifs/death.gif");
            } else if (reds <= 20 || blues <= 20 || yellows <= 20) {
                guy.setImage("gifs/hungry.gif");
            } else if (reds >= 100 && blues >= 100 && yellows >= 100) {
                gameOver = true;
                gameOverAlert.setText("You Won");
                gameOverAlert.setFill(Color.GREEN);
                guy.setImage("gifs/win.png");
                map.getChildren().add(gameOverAlert);
            } else if (guy.getColor() != 'e') {
                guy.setImage("gifs/eating.gif");
            }
        }
    }

    private void eatThings() {
        int x = guyX * 2;
        int y = guyY * 2;
        if (coins[x][y] != null) {
            eat(x, y);
        }
        if (coins[x + 1][y] != null) {
            eat(x + 1, y);
        }
        if (coins[x][y + 1] != null) {
            eat(x, y + 1);
        }
        if (coins[x + 1][y + 1] != null) {
            eat(x + 1, y + 1);
        }
    }

    private void eat(int x, int y) {
        char color = coins[x][y].getColor();
        if (color == 'r')
            reds += 5;
        else if (color == 'b')
            blues += 5;
        else
            yellows += 5;
        map.getChildren().remove(coins[x][y].gif);
        coins[x][y] = null;
        updateHunger();
    }

    private Pane makeMap() {
//        Line c0 = new Line(0, 0, 0, 600);
//        Line c1 = new Line(100, 0, 100, 600);
//        Line c2 = new Line(200, 0, 200, 600);
//        Line c3 = new Line(300, 0, 300, 600);
//        Line c4 = new Line(400, 0, 400, 600);
//        Line c5 = new Line(500, 0, 500, 600);
//        Line c6 = new Line(600, 0, 600, 600);
//
//        Line r0 = new Line(0, 0, 600, 0);
//        Line r1 = new Line(0, 100, 600, 100);
//        Line r2 = new Line(0, 200, 600, 200);
//        Line r3 = new Line(0, 300, 600, 300);
//        Line r4 = new Line(0, 400, 600, 400);
//        Line r5 = new Line(0, 500, 600, 500);
//        Line r6 = new Line(0, 600, 600, 600);
//
//        return new Pane(c0, c1, c2, c3, c4, c5, c6, r0, r1, r2, r3, r4, r5, r6);

        int size = 6;

        Pane pane = new Pane();
        ObservableList<Node> kids = pane.getChildren();

        for (int i = 0; i <= size; i++)
            kids.add(new Line(i * 100, 0, i * 100, size * 100));

        for (int i = 0; i <= size; i++)
            kids.add(new Line(0, i * 100, size * 100, i * 100));

        return pane;
    }

    private void addRandomCoin() {
        int x = (int) (Math.random() * 12);
        int y = (int) (Math.random() * 12);

        double c = Math.random() * 3;
        String color;
        if (c > 2)
            color = "red";
        else if (c > 1)
            color = "blue";
        else
            color = "yellow";

        Gif coin = new Gif("gifs/" + color + "-coin.gif", (x * 50) + 20, (y * 50) + 20);

        if (coins[x][y] != null)
            map.getChildren().remove(coins[x][y].gif);
        coins[x][y] = coin;
        map.getChildren().add(coin.gif);
    }

    private class Gif {
        private ImageView gif;
        private char color;

        private Gif(String fileName, double x, double y) {
            try {
                gif = new ImageView(new Image(new FileInputStream(fileName)));
                gif.relocate(x, y);
                color = fileName.charAt(5);
            } catch (FileNotFoundException e) {
                reds = 0;
                blues = 0;
                yellows = 0;
                updateHunger();
                gameOverAlert.setText("404");
                map.getChildren().add(gameOverAlert);
                System.out.println(e.getMessage());
            }
        }

        private char getColor() {
            return color;
        }

        private void setImage(String fileName) {
            try {
                gif.setImage(new Image(new FileInputStream(fileName)));
                color = fileName.charAt(5);
            } catch (FileNotFoundException e) {
                reds = 0;
                blues = 0;
                yellows = 0;
                updateHunger();
                gameOverAlert.setText("404");
                map.getChildren().add(gameOverAlert);
                System.out.println(e.getMessage());
            }
        }
    }
}
