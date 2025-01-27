package pro.tremblay.snake;

import pro.tremblay.framework.Game;

import javax.swing.JFrame;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics2D;
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

            enemies.forEach(enemy -> enemy.targetBall(balls));

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
                    case KeyEvent.VK_SHIFT -> snake.boost();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SHIFT -> snake.normal();
                    case KeyEvent.VK_ESCAPE-> System.exit(0);
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
