package pro.tremblay.learn;

import pro.tremblay.framework.CircularQueue;
import pro.tremblay.framework.Game;
import pro.tremblay.framework.Sprite;

import javax.swing.JFrame;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferStrategy;
import java.util.random.RandomGenerator;

class Apple extends Sprite<Learn> {

    protected Apple(Learn game) {
        super(game);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.red);
        g.fillRect((int) x, (int) y, Learn.CELL_SIZE, Learn.CELL_SIZE);
    }
}

class Snake extends Sprite<Learn> {
    private int length =  1;
    private final CircularQueue<Point2D> positions = new CircularQueue<>(Learn.CELL_NUMBER * Learn.CELL_NUMBER);

    protected Snake(Learn game) {
        super(game);
    }

    @Override
    public void move() {
        super.move();
        positions.add(new Point2D.Double(x, y));
    }

    @Override
    public void position(double x, double y) {
        super.position(x, y);
        positions.add(new Point2D.Double(x, y));
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.GREEN);
        positions.getFirsts(length)
                .forEach(pos -> g.fillRect((int) pos.getX(), (int) pos.getY(), Learn.CELL_SIZE, Learn.CELL_SIZE));
    }

    public boolean touch(Apple apple) {
        return x == apple.x() && y == apple.y();
    }

    public boolean outOfScreen() {
        if ( x >= game.screenWidth()) {
            return true;
        }
        if ( y >= game.screenHeight()) {
            return true;
        }
        if ( x < 0) {
            return true;
        }
        return y < 0;
    }

    public void grow() {
        length++;
    }

    public void reset() {
        positions.clear();
        position(0, 0);
        speed(Learn.SPEED, 0);
        length = 1;
    }
}

public class Learn extends Game {

    private static final RandomGenerator RANDOM = RandomGenerator.getDefault();
    static final int CELL_SIZE = 25;
    public static final int CELL_NUMBER = 32;
    public static final int SPEED = 25;
    private final Snake snake = new Snake(this);
    private Apple apple;
    private boolean gameOver = false;

    public static void main(String[] args) {
        Learn app = new Learn();
        app.start();
    }

    @Override
    public int screenHeight() {
        return 800;
    }

    @Override
    public int screenWidth() {
        return 800;
    }

    @Override
    protected void play(BufferStrategy bufferStrategy) {

        new Timer(100, e -> {

            Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
            try {
                fondEcran(g, Color.GRAY);

                apple.draw(g);
                snake.draw(g);

                snake.move();

                if (gameOver) {
                    drawGameOver(g);
                }
                else if (snake.outOfScreen()) {
                    gameOver = true;
                } else if (snake.touch(apple)) {
                    apple = placeApple();
                    snake.grow();
                }

            } finally {
                g.dispose();
            }

            bufferStrategy.show();
        }).start();

    }

    private void drawGameOver(Graphics2D g) {
        g.setFont(new Font("TimesRoman", Font.BOLD, 150));
        g.drawString("Game Over", 40, 400);
    }

    @Override
    protected void fondEcran(Graphics g, Color color) {
        super.fondEcran(g, color);

        g.setColor(Color.BLACK);

        // Draw vertical lines.
        for (int i = CELL_SIZE; i < screenWidth(); i += CELL_SIZE) {
            g.drawLine(i, 0, i, screenHeight());
        }

        // Draw horizontal lines.
        for (int i = CELL_SIZE; i < screenHeight(); i += CELL_SIZE) {
            g.drawLine(0, i, screenWidth(), i);
        }
    }

    @Override
    protected void init(JFrame frame) {
        startNewGame();

        frame.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP -> snake.speed(0, -SPEED);
                    case KeyEvent.VK_DOWN -> snake.speed(0, SPEED);
                    case KeyEvent.VK_RIGHT -> snake.speed(SPEED, 0);
                    case KeyEvent.VK_LEFT -> snake.speed(-SPEED, 0);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ESCAPE -> System.exit(0);
                    case KeyEvent.VK_ENTER -> startNewGame();
                }
            }
        });
    }

    private void startNewGame() {
        gameOver = false;
        snake.reset();
        apple = placeApple();
    }

    @Override
    protected String frameTitle() {
        return "Learn Charles";
    }

    private Apple placeApple() {
        int x = RANDOM.nextInt(CELL_NUMBER);
        int y = RANDOM.nextInt(CELL_NUMBER);
        Apple apple = new Apple(this);
        apple.position(x * CELL_SIZE, y * CELL_SIZE);
        return apple;
    }
}
