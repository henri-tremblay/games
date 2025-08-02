package pro.tremblay.snake;

import com.github.javafaker.Faker;
import pro.tremblay.framework.Game;
import pro.tremblay.framework.Geometry;

import javax.swing.JFrame;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.random.RandomGenerator;

public class Snake extends Game {

    private static final RandomGenerator RANDOM = RandomGenerator.getDefault();

    public static void main(String[] args) {
        Snake app = new Snake();
        app.start();
    }

    private final Faker faker = new Faker();
    private final PlayerSnake snake = new PlayerSnake(this);
    private final List<EnemySnake> enemies = new ArrayList<>();
    private final Score score = new Score(this, snake);
    private final LeaderBoard leaderBoard = new LeaderBoard(this, enemies);
    private final List<Ball> balls = new CopyOnWriteArrayList<>();
    private final GameOver gameOver = new GameOver(this);
    private boolean touched = false;

    // Add viewport tracking
    private double viewportX = 0;
    private double viewportY = 0;

    @Override
    public int worldWidth() {
        return super.worldWidth() * 5;
    }

    @Override
    public int worldHeight() {
        return super.worldHeight() * 5;
    }

    public int numberOfEnnemies() {
        return enemies.size();
    }

    @Override
    protected void play(BufferStrategy bufferStrategy) {
        AtomicInteger boostTime = new AtomicInteger(0);

        new Timer(100, e -> {
            updateViewport();

            if (snake.boosted()) {
                int time = boostTime.incrementAndGet();
                if (time > 8) {
                    snake.removeRing();
                    boostTime.set(0);
                }
            }

            enemies.forEach(enemy -> {
                Ball ball = enemy.targetBall(balls);
                if (ball != null) {
                    enemy.follow(ball);
                }
            });

            if (!touched) {
                snake.move();
                enemies.forEach(SnakeSprite::move);
            }

            Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();

            try {
                background(g, Color.BLACK);

                // Translate all drawing by viewport offset
                g.translate(-viewportX, -viewportY);

                balls.forEach(b -> b.draw(g));
                snake.draw(g);
                enemies.forEach(enemy -> enemy.draw(g));

                g.translate(viewportX, viewportY);

                score.draw(g);
                leaderBoard.draw(g);
                if (touched) {
                    gameOver.draw(g);
                }
            } finally {
                g.dispose();
            }

            swallowBall();

            if (enemies.stream().anyMatch(snake::touch)) {
                touched = true;
            } else {
                ListIterator<EnemySnake> it = enemies.listIterator();
                while(it.hasNext()) {
                    EnemySnake enemy = it.next();
                    if (enemy.touch(snake)) {
                        it.remove();
                        enemy.unfollow();
                        AtomicInteger take = new AtomicInteger(0);
                        enemy.rings().stream()
                                .filter(_ -> take.getAndIncrement() % 4 == 0)
                                .map(p -> createBall(p, Ball.Type.ENEMY))
                                .forEach(balls::add);
                    }
                }
            }
            
            bufferStrategy.show();
        }).start();
    }

    /**
     * Update the viewport to follow the snake
     */
    private void updateViewport() {
        // Center viewport on snake
        viewportX = snake.x() - (screenWidth() / 2);
        viewportY = snake.y() - (screenHeight() / 2);

        // Clamp viewport to world bounds
        viewportX = Math.max(0, Math.min(viewportX, worldWidth() - screenWidth()));
        viewportY = Math.max(0, Math.min(viewportY, worldHeight() - screenHeight()));
    }

    private void enemiesFollowSnake(SnakeSprite enemy) {
        double enemyX = enemy.x();
        double enemyY = enemy.y();
        double x = snake.x();
        double y = snake.y();
        double lengthX = x - enemyX;
        double lengthY = y - enemyY;
        double angle = Math.atan2(lengthY, lengthX);
        enemy.changeDirection(angle);
    }

    private void swallowBall() {
        for (int i = 0; i < balls.size(); i++) {
            Ball ball = balls.get(i);
            if (ball.touch(snake)) {
                ballEaten(i, snake);
                if (snake.length() % 2 == 0) {
                    EnemySnake enemy = createEnemy();
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
        Ball ball = balls.remove(index);
        snake.addRing();
        if (ball.type() == Ball.Type.NORMAL) {
            addBallIn5seconds();
        }
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
                double x = e.getX() + viewportX;
                double y = e.getY() + viewportY;
                double lengthX = x - ballX;
                double lengthY = y - ballY;
                double angle = Math.atan2(lengthY, lengthX);
                snake.changeDirection(angle);
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
        initLeaderBoard();
        initEnemies();
        touched = false;
    }

    private void initEnemies() {
        enemies.clear();
    }

    private void initBalls() {
        balls.clear();
        for (int i = 0; i < 1500; i++) {
            Ball ball = findFreeSpot();
            balls.add(ball);
        }
    }

    private Ball findFreeSpot() {
        Point p = randomPoint();
        return createBall(p, Ball.Type.NORMAL);
    }

    private Ball createBall(Point2D p, Ball.Type type) {
        Ball ball = new Ball(this, type);
        ball.position(p.getX(), p.getY());
        return ball;
    }

    private EnemySnake createEnemy() {
        String firstName = faker.name().firstName(); // Emory
        EnemySnake enemy = new EnemySnake(this, firstName);
        Point p = randomPoint();
        while(tooCloseToSnake(p.x, p.y)) {
            p = randomPoint();
        }

        enemy.position(p.x, p.y);
        int vx = gaussianSpeed();
        int vy = gaussianSpeed();
        enemy.speed(vx, vy);
        for (int i = 0; i < 10; i++) {
            enemy.addRing();
        }
        return enemy;
    }

    private Point randomPoint() {
        int x = RANDOM.nextInt((int) SnakeSprite.DIAMETER / 2, worldWidth() - (int) SnakeSprite.DIAMETER / 2);
        int y = RANDOM.nextInt((int) SnakeSprite.DIAMETER / 2, worldHeight() - (int) SnakeSprite.DIAMETER / 2);
        return new Point(x, y);
    }

    private boolean tooCloseToSnake(int x, int y) {
        double snakeX = snake.x() + SnakeSprite.DIAMETER / 2;
        double snakeY = snake.y() + SnakeSprite.DIAMETER / 2;
        double enemyX = x + SnakeSprite.DIAMETER / 2;
        double enemyY = y + SnakeSprite.DIAMETER / 2;
        double safeZoneR = SnakeSprite.DIAMETER / 2 * 3;
        double enemyR = SnakeSprite.DIAMETER / 2;
        return Geometry.circleIntersect(
                snakeX, snakeY, safeZoneR,
                enemyX, enemyY, enemyR
        );
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
        snake.position((worldWidth() - SnakeSprite.DIAMETER) / 2.0, (worldHeight() - SnakeSprite.DIAMETER) / 2.0);
        snake.position(100, 100);
        snake.setInitialSpeed();
    }

    private void initScore() {
        score.position(10, 30);
    }

    private void initLeaderBoard() {
        leaderBoard.position(screenWidth() - 220, 30);
    }
}
