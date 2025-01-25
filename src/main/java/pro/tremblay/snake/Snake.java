package pro.tremblay.snake;

import pro.tremblay.framework.CircularQueue;
import pro.tremblay.framework.Game;
import pro.tremblay.framework.Geometry;
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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.random.RandomGenerator;

class SnakeSprite extends Sprite<Snake> {
    static final double DIAMETER = 50;
    static final double INITIAL_SPEED = 15;
    static final double BOOST_SPEED = 20;
    static final double ENEMY_SPEED = 10;

    private final boolean enemy;
    private Color color;

    private boolean boosted = false;
    private int length = 0;
    private final CircularQueue<Point2D.Double> positions = new CircularQueue<>(100);

    public SnakeSprite(Snake game) {
        this(game, false);
    }

    public SnakeSprite(Snake game, boolean enemy) {
        super(game);
        this.enemy = enemy;
    }

    @Override
    public void speed(double vx, double vy) {
        super.speed(vx, vy);
        this.color = enemy ? enemyColor() : Color.YELLOW;
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
            vy = -vy;
        } else if (y + DIAMETER > game.screenHeight()) {
            y = game.screenHeight() - DIAMETER;
            vy = -vy;
        }

        if (x < 0) {
            x  = 0;
            vx = -vx;
        } else if (x + DIAMETER > game.screenWidth()) {
            x = game.screenWidth() - DIAMETER;
            vx = -vx;
        }

        positions.add(new Point2D.Double(x, y));
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(color);
        // Draw face circle
        g.fillOval((int) x, (int) y, (int) DIAMETER, (int) DIAMETER);

        positions.getFirsts(length).forEach(p -> g.fillOval((int) p.x, (int) p.y, (int) DIAMETER, (int) DIAMETER));

        g.setColor(Color.BLACK);
        // Draw eyes
        g.fillOval((int) (x + 10), (int) (y + 10), 10, 10);
        g.fillOval((int) (x + 30), (int) (y + 10), 10, 10);
        // Draw mouth
        if (enemy) {
            g.drawLine((int) (x + 15), (int) (y + 30), (int) (x + 35), (int) (y + 30));
        } else {
            g.drawArc((int) (x + 10), (int) (y + 20), 30, 20, 180, 180);
        }
    }

    private Color enemyColor() {
        double speed = vx * vx + vy * vy;
        if (speed > 81) {
            return Color.GRAY;
        } else if (speed > 49) {
            return Color.RED;
        } else if (speed > 25) {
            return Color.ORANGE;
        }
        return Color.BLUE;
    }

    public void setInitialSpeed() {
        vx = 0;
        vy = 0;
        boosted = false;
    }

    public void speed(double angle) {
        double speed = enemy ? ENEMY_SPEED : (boosted ? BOOST_SPEED : INITIAL_SPEED);
        speed(Math.cos(angle) * speed, Math.sin(angle) * speed);
    }

    public void boost() {
        if (length == 0) {
            return;
        }
        if (!boosted) {
            vx = vx / INITIAL_SPEED * BOOST_SPEED;
            vy = vy / INITIAL_SPEED * BOOST_SPEED;
            boosted = true;
        }
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
        if (positions.size() < length) {
            positions.add(new Point2D.Double(x, y));
        }
    }

    public void removeRing() {
        if (length > 0) {
            length--;
        }
    }

    public void clearRings() {
        length = 0;
    }

    /**
     * Check if an enemy touch the snake. "this" is the enemy
     *
     * @param snake the snake
     * @return if touched
     */
    public boolean touch(SnakeSprite snake) {
        // If any part of the enemy "This" touches the snake
        double radius = SnakeSprite.DIAMETER / 2;
        double x = snake.x() + radius;
        double y = snake.y() + radius;

        List<Point2D.Double> all = positions.getFirsts(length)
                .toList();
        for (Point2D.Double p : all) {
            double px = p.x + radius;
            double py = p.y + radius;
            if (Geometry.circleIntersect(x, y, radius, px, py, radius)) {
                Snake.snakeTouched = new Point2D.Double(snake.x(), snake.y());
                Snake.enemyTouched = new Point2D.Double(p.x, p.y);
                return true;
            }
        }
        return false;

//        return all.stream().anyMatch(p -> {
//           double px = p.x + radius;
//           double py = p.y + radius;
//           return Geometry.circleIntersect(x, y, radius, px, py, radius);
//        });
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
        double snakeR = SnakeSprite.DIAMETER / 2;
        double ballR = SnakeSprite.DIAMETER / 4;
        return Geometry.circleIntersect(
                snakeX, snakeY, snakeR,
                ballX, ballY, ballR
        );
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

class GameOver extends Sprite<Snake> {

    public GameOver(Snake game) {
        super(game);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.drawString("Appuyez sur Entrée pour rejouer", 280, 250);

        Font currentFont = g.getFont();
        Font newFont = currentFont.deriveFont(currentFont.getSize() * 5F);
        g.setFont(newFont);

        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawString("Fin de la partie", 280, 200);
    }
}

public class Snake extends Game {

    private static final RandomGenerator RANDOM = RandomGenerator.getDefault();
    public static Point2D.Double snakeTouched;
    public static Point2D.Double enemyTouched;

    public static void main(String[] args) {
        Snake app = new Snake();
        app.start();
    }

    private final SnakeSprite snake = new SnakeSprite(this);
    private final List<SnakeSprite> enemies = new ArrayList<>();
    private final Score score = new Score(this, snake);
    private final List<Ball> balls = new CopyOnWriteArrayList<>();
    private final GameOver gameOver = new GameOver(this);
    private boolean touched = false;

    @Override
    protected void play(BufferStrategy bufferStrategy) {
        AtomicInteger boostTime = new AtomicInteger(0);

        new Timer(100, e -> {
            if (snake.boosted()) {
                int time = boostTime.incrementAndGet();
                if (time > 8) {
                    snake.removeRing();
                    boostTime.set(0);
                }
            }

            if (!touched) {
                snake.move();
//                enemies.forEach(this::enemiesFollowSnake);
                enemies.forEach(SnakeSprite::move);
            }

            Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();

            try {
                fondEcran(g, Color.BLACK);

                balls.forEach(b -> b.draw(g));
                snake.draw(g);
                enemies.forEach(enemy -> enemy.draw(g));
                score.draw(g);
                if (touched) {
                    gameOver.draw(g);
                }
            } finally {
                g.dispose();
            }

            swallowBall();

            if (enemies.stream().anyMatch(enemy -> enemy.touch(snake))) {
                touched = true;
            }
            
            bufferStrategy.show();
        }).start();
    }

    private void enemiesFollowSnake(SnakeSprite enemy) {
        double enemyX = enemy.x();
        double enemyY = enemy.y();
        double x = snake.x();
        double y = snake.y();
        double lengthX = x - enemyX;
        double lengthY = y - enemyY;
        double angle = Math.atan2(lengthY, lengthX);
        enemy.speed(angle);
    }

    private void swallowBall() {
        for (int i = 0; i < balls.size(); i++) {
            Ball ball = balls.get(i);
            if (ball.touch(snake)) {
                ballEaten(i, snake);
                if (snake.length() % 5 == 0) {
                    SnakeSprite enemy = createEnemy();
                    enemies.add(enemy);
                }
                break;
            }
            for (SnakeSprite enemy : enemies) {
                if (ball.touch(enemy)) {
                    ballEaten(i, enemy);
                    break;
                }
            }
        }
    }

    private void ballEaten(int index, SnakeSprite snake) {
        balls.remove(index);
        snake.addRing();
        addBallIn5seconds();
    }

    private void addBallIn5seconds() {
        Timer timer = new Timer(3_000, e -> {
            Ball ball = findFreeSpot();
            balls.add(ball);
        });
        timer.setRepeats(false);
        timer.start();
    }

    @Override
    protected void init(JFrame frame) {
        init();

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
                    case KeyEvent.VK_M -> {
                        enemies.forEach(Snake.this::enemiesFollowSnake);
                        enemies.forEach(SnakeSprite::move);
                    }
                    case KeyEvent.VK_ENTER -> {
                        if (touched) {
                            init();
                        }
                    }
                }
            }
        });
    }

    private void init() {
        initSnake();
        initBalls();
        initScore();
        initEnemies();
        touched = false;
    }

    private void initEnemies() {
        enemies.clear();
    }

    private void initBalls() {
        balls.clear();
        for (int i = 0; i < 25; i++) {
            Ball ball = findFreeSpot();
            balls.add(ball);
        }
    }

    private Ball findFreeSpot() {
        int x = RANDOM.nextInt((int) SnakeSprite.DIAMETER / 2, screenWidth() - (int) SnakeSprite.DIAMETER / 2);
        int y = RANDOM.nextInt((int) SnakeSprite.DIAMETER / 2, screenHeight() - (int) SnakeSprite.DIAMETER / 2);
        Ball ball = new Ball(this);
        ball.position(x, y);
        return ball;
    }

    private SnakeSprite createEnemy() {
        SnakeSprite enemy = new SnakeSprite(this, true);
        int x = RANDOM.nextInt((int) SnakeSprite.DIAMETER / 2, screenWidth() - (int) SnakeSprite.DIAMETER / 2);
        int y = RANDOM.nextInt((int) SnakeSprite.DIAMETER / 2, screenHeight() - (int) SnakeSprite.DIAMETER / 2);
        enemy.position(x, y);
        int vx = gaussianSpeed();
        int vy = gaussianSpeed();
        enemy.speed(vx, vy);
        for (int i = 0; i < 10; i++) {
            enemy.addRing();
        }
        return enemy;
    }

    private int gaussianSpeed() {
        double speed = RANDOM.nextGaussian(5, 2);
        if (speed < 3) {
            speed = 3;
        } else if (speed > 10) {
            speed = 10;
        }
        int side = RANDOM.nextDouble() < 0.5 ? -1 : 1;
        return side * (int) speed;
    }

    @Override
    protected String frameTitle() {
        return "Snake Édouard";
    }

    private void initSnake() {
        snake.clearRings();
        snake.position((screenWidth() - SnakeSprite.DIAMETER) / 2.0, (screenHeight() - SnakeSprite.DIAMETER) / 2.0);
        snake.position(100, 100);
        snake.setInitialSpeed();
    }

    private void initScore() {
        score.position(10, 30);
    }
}
