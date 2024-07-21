package pro.tremblay.snake;

import pro.tremblay.framework.CircularQueue;
import pro.tremblay.framework.Game;
import pro.tremblay.framework.Sprite;

import javax.swing.JFrame;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.random.RandomGenerator;

class SnakeSprite extends Sprite<Snake> {
    static final double DIAMETER = 50;
    static final double INITIAL_SPEED = 15;
    static final double BOOST_SPEED = 20;

    private boolean boosted = false;
    private int length = 0;
    private final CircularQueue<Point2D.Double> positions = new CircularQueue<>(100);

    public SnakeSprite(Snake game) {
        super(game);
    }

    public int length() {
        return length;
    }

    public boolean boosted() {
        return boosted;
    }

    @Override
    public void move() {
        super.move();

        if (y < 0) {
            y  = 0;
        } else if (y + DIAMETER > game.screenHeight()) {
            y = game.screenHeight() - DIAMETER;
        }

        if (x < 0) {
            x  = 0;
        } else if (x + DIAMETER > game.screenWidth()) {
            x = game.screenWidth() - DIAMETER;
        }

        positions.add(new Point2D.Double(x, y));
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        // Draw face circle
        g.fillOval((int) x, (int) y, (int) DIAMETER, (int) DIAMETER);

        positions.getFirsts(length).forEach(p -> g.fillOval((int) p.x, (int) p.y, (int) DIAMETER, (int) DIAMETER));

        g.setColor(Color.BLACK);
        // Draw eyes
        g.fillOval((int) (x + 10), (int) (y + 10), 10, 10);
        g.fillOval((int) (x + 30), (int) (y + 10), 10, 10);
        // Draw mouth
        g.drawArc((int) (x + 10), (int) (y + 20), 30, 20, 180, 180);
    }

    public void setInitialSpeed() {
        vx = 0; //INITIAL_SPEED;
        vy = 0;
        boosted = false;
    }

    public void speed(double angle) {
        double speed = boosted ? BOOST_SPEED : INITIAL_SPEED;
        speed(Math.cos(angle) * speed, Math.sin(angle) * speed);
    }

    public void boost() {
        if (length == 0) {
            return;
        }
        vx = vx / INITIAL_SPEED * BOOST_SPEED;
        vy = vy / INITIAL_SPEED * BOOST_SPEED;
        boosted = true;
    }

    public void normal() {
        if (!boosted) {
            return;
        }
        vx = vx / BOOST_SPEED * INITIAL_SPEED;
        vy = vy / BOOST_SPEED * INITIAL_SPEED;
        boosted = false;
    }

    public void addRing() {
        length++;
    }

    public void removeRing() {
        if (length > 0) {
            length--;
        }
    }
}

class Ball extends Sprite<Snake> {

    protected Ball(Snake game) {
        super(game);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.RED);
        // Draw face circle
        g.fillOval((int) x, (int) y, (int) SnakeSprite.DIAMETER / 2, (int) SnakeSprite.DIAMETER / 2);
    }

    @Override
    public boolean touch(Sprite<Snake> sprite) {
        double snakeX = sprite.x() + SnakeSprite.DIAMETER / 2;
        double snakeY = sprite.y() + SnakeSprite.DIAMETER / 2;
        double ballX = x() + SnakeSprite.DIAMETER / 4;
        double ballY = y() + SnakeSprite.DIAMETER / 4;
        double distance = Math.sqrt((snakeX-ballX)*(snakeX-ballX) + (snakeY-ballY)*(snakeY-ballY));
        return distance <= SnakeSprite.DIAMETER / 2 + SnakeSprite.DIAMETER / 4;
    }
}

class Score extends Sprite<Snake> {
    private final SnakeSprite snake;

    public Score(Snake game, SnakeSprite snake) {
        super(game);
        this.snake = snake;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        Font currentFont = g.getFont();
        Font newFont = currentFont.deriveFont(currentFont.getSize() * 1.4F);
        g.setFont(newFont);

        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawString("Longueur: " + snake.length(), (int) x, (int) y);
    }
}

public class Snake extends Game {

    public static void main(String[] args) {
        Snake app = new Snake();
        app.start();
    }

    private final SnakeSprite snake = new SnakeSprite(this);
    private final Score score = new Score(this, snake);
    private final List<Ball> balls = new ArrayList<>();

    @Override
    protected void play(BufferStrategy bufferStrategy) {
        AtomicInteger boostTime = new AtomicInteger(0);

        new Timer(100, e -> {
            if (snake.boosted()) {
                int time = boostTime.incrementAndGet();
                if (time > 10) {
                    snake.removeRing();
                    boostTime.set(0);
                }
            }
            snake.move();

            Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();

            try {
                fondEcran(g, Color.BLACK);

                balls.forEach(b -> b.draw(g));
                snake.draw(g);
                score.draw(g);
            } finally {
                g.dispose();
            }

            swallowBall();
            
            bufferStrategy.show();
        }).start();
    }

    private void swallowBall() {
        for (int i = 0; i < balls.size(); i++) {
            Ball ball = balls.get(i);
            if (ball.touch(snake)) {
                balls.remove(i);
                snake.addRing();
                break;
            }
        }
    }

    @Override
    protected void init(JFrame frame) {
        initSnake();
        initBalls();
        initScore();

        frame.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                double ballX = snake.x();
                double ballY = snake.y();
                double x = e.getX();
                double y = e.getY();
                double lengthX = x - ballX;
                double lengthY = y - ballY;
                double angle = Math.atan2(lengthY, lengthX);
                snake.speed(angle);
            }
        });

        frame.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP -> snake.boost();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP -> snake.normal();
                    case KeyEvent.VK_ESCAPE -> System.exit(0);
                }
            }
        });
    }

    private void initBalls() {
        RandomGenerator random = RandomGenerator.getDefault();
        for (int i = 0; i < 20; i++) {
            int x = random.nextInt((int) SnakeSprite.DIAMETER / 2, screenWidth() - (int) SnakeSprite.DIAMETER / 2);
            int y = random.nextInt((int) SnakeSprite.DIAMETER / 2, screenHeight() - (int) SnakeSprite.DIAMETER / 2);
            Ball ball = new Ball(this);
            ball.position(x, y);
            balls.add(ball);
        }
    }

    @Override
    protected String frameTitle() {
        return "Snake Édouard";
    }

    private void initSnake() {
        snake.position((screenWidth() - SnakeSprite.DIAMETER) / 2.0, (screenHeight() - SnakeSprite.DIAMETER) / 2.0);
        snake.setInitialSpeed();
    }

    private void initScore() {
        score.position(10, 30);
    }
}
