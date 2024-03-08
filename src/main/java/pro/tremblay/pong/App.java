package pro.tremblay.pong;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.random.RandomGenerator;

// Collision balle avec bas à corriger
// Rebondir sur les palettes
// Augmenter la balle de vitesse à chaque 10 secondes xxxx
// Écrire le pointage xxx
// Utiliser un tampon d'affichage xxx

abstract class Sprite {
    protected double x, y;
    protected double vx, vy;

    public void position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void speed(double vx, double vy) {
        this.vx = vx;
        this.vy = vy;
    }

    public void move() {
        x += vx;
        y += vy;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Sprite.class.getSimpleName() + "[", "]")
                .add("x=" + x)
                .add("y=" + y)
                .add("vx=" + vx)
                .add("vy=" + vy)
                .toString();
    }

    public abstract void draw(Graphics g);
}

class Pad extends Sprite {
    static final int WIDTH = 30;
    static final int HEIGHT = 150;
    static final int INITIAL_SPEED = 15;

    @Override
    public void move() {
        super.move();
        if (y < 0) {
            y  = 0;
        } else if (y + HEIGHT > App.SCREEN_HEIGHT) {
            y = App.SCREEN_HEIGHT - HEIGHT;
        }
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect((int) x, (int) y, WIDTH, HEIGHT);

    }

}

class Ball extends Sprite {
    static final int DIAMETER = 50;

    @Override
    public void move() {
        super.move();
        if (y < 0) {
            y  = 0;
            vy = -vy;
        } else if (y + DIAMETER > App.SCREEN_HEIGHT) {
            y = App.SCREEN_HEIGHT - DIAMETER;
            vy = -vy;
        }
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillOval((int) x, (int) y, DIAMETER, DIAMETER);
    }
}

class Score extends Sprite {
    int left, right;

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        Font currentFont = g.getFont();
        Font newFont = currentFont.deriveFont(currentFont.getSize() * 1.4F);
        g.setFont(newFont);

        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawString(left + " " + right, (int) x, (int) y);
    }
}

public class App {
    static final int SCREEN_WIDTH = 1024;
    static final int SCREEN_HEIGHT = 768;
    static double ballSpeed = 15;

    public static void main(String[] args) {
        App app = new App();
        app.start();
    }

    private final Pad left = new Pad();
    private final Pad right = new Pad();
    private final Ball ball = new Ball();
    private final Score score = new Score();

    private void start() {

        JFrame frame = new JFrame("Pong Édouard");
        frame.setSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);

        initPads();
        initBall();
        initScore();

        frame.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                frame.setTitle("Pong Édouard (" + e.getX() + "," + e.getY());
            }
        });

        frame.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP -> right.vy = -Pad.INITIAL_SPEED;
                    case KeyEvent.VK_DOWN -> right.vy = Pad.INITIAL_SPEED;
                    case KeyEvent.VK_W -> left.vy = -Pad.INITIAL_SPEED;
                    case KeyEvent.VK_S -> left.vy = Pad.INITIAL_SPEED;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP, KeyEvent.VK_DOWN -> right.vy = 0;
                    case KeyEvent.VK_W, KeyEvent.VK_S -> left.vy = 0;
                    case KeyEvent.VK_SPACE -> initBall();
                    case KeyEvent.VK_ESCAPE -> System.exit(0);
                }
            }
        });

        frame.setVisible(true);
        frame.createBufferStrategy(3);

        BufferStrategy bufferStrategy = frame.getBufferStrategy();

        AtomicInteger ballLevel = new AtomicInteger(0);

        new Timer(100, e -> {
            if (ballLevel.incrementAndGet() % 50 == 0) {
                double plusOneRatio = (ballSpeed + 2) / ballSpeed;
                ball.speed(ball.vx * plusOneRatio, ball.vy * plusOneRatio);
                ballSpeed++;
            }
            left.move();
            right.move();

            ball.move();

            collisionLeft();
            collisionRight();

            if (ball.x <= 50) {
                score.right++;
                initBall();
            } else if (ball.x > SCREEN_WIDTH - 50) {
                score.left++;
                initBall();
            }

            Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();

            try {
                fondEcran(g, Color.BLACK);
                ligneDuCentre(g);

                left.draw(g);
                right.draw(g);
                ball.draw(g);
                score.draw(g);
            } finally {
                g.dispose();
            }
            bufferStrategy.show();
        }).start();
    }

    private void collisionRight() {
        collision(right);
    }

    private void collisionLeft() {
        collision(left);
    }

    private void collision(Pad pad) {
        Rectangle2D.Double padRect = new Rectangle2D.Double(pad.x, pad.y, Pad.WIDTH, Pad.HEIGHT);
        Rectangle2D.Double ballRect = new Rectangle2D.Double(ball.x, ball.y, Ball.DIAMETER, Ball.DIAMETER);
        if (padRect.intersects(ballRect)) {
            ball.speed(-ball.vx, ball.vy);
        }
    }

    private void ligneDuCentre(Graphics2D g) {
        g.setColor(Color.YELLOW);
        g.drawLine(SCREEN_WIDTH / 2, 0, SCREEN_WIDTH / 2, SCREEN_HEIGHT);
    }

    private void fondEcran(Graphics g, Color color) {
        g.setColor(color);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    private void initBall() {
        double angle = findGoodAngle();
        ball.position((SCREEN_WIDTH - Ball.DIAMETER) / 2, (SCREEN_HEIGHT - Ball.DIAMETER) / 2);
        double ballvx = (Math.cos(angle) * ballSpeed);
        double ballvy = (Math.sin(angle) * ballSpeed);
        ball.speed(ballvx, ballvy);
    }

    private double findGoodAngle() {
        RandomGenerator rand = RandomGenerator.getDefault();
        double angle = rand.nextDouble(2 * Math.PI);
        while(!goodAngle(angle)) {
            angle = rand.nextDouble(2 * Math.PI);
        }
        return angle;
    }

    private boolean goodAngle(double angle) {
        return angle < Math.PI / 4 ||
                (angle < 5 * Math.PI / 4 && angle > 3 * Math.PI / 4) ||
                angle >  7 * Math.PI / 4;
    }

    private void initPads() {
        int padY = (SCREEN_HEIGHT - Pad.HEIGHT) / 2;
        left.position(50, padY);
        right.position(SCREEN_WIDTH - 50 - Pad.WIDTH, padY);
        left.speed(0, 0);
        right.speed(0, 0);
    }

    private void initScore() {
        score.position(SCREEN_WIDTH / 2 - 15, 40);
    }
}
