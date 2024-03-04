package pro.tremblay.pong;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.random.RandomGenerator;

// Collision balle avec bas à corriger
// Rebondir sur les palettes
// Augmenter la balle de vitesse à chaque 10 secondes xxxx
// Écrire le pointage
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

    public abstract void draw(Graphics g);
}

class Pad extends Sprite {
    static final int WIDTH = 30;
    static final int HEIGHT = 150;

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

public class App {
    static final int SCREEN_WIDTH = 1024;
    static final int SCREEN_HEIGHT = 768;
    static double ballSpeed = 15;
    static final int BUFFERS = 2;

    public static void main(String[] args) {
        App app = new App();
        app.start();
    }

    private final Pad left = new Pad();
    private final Pad right = new Pad();
    private final Ball ball = new Ball();

    private void start() {

        JFrame frame = new JFrame("Pong Édouard");
        frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((d.width- SCREEN_WIDTH) / 2, (d.height - SCREEN_HEIGHT) / 2);

        initPads();
        initBall();

        frame.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP -> right.vy = -10;
                    case KeyEvent.VK_DOWN -> right.vy = 10;
                    case KeyEvent.VK_W -> left.vy = -10;
                    case KeyEvent.VK_S -> left.vy = 10;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP, KeyEvent.VK_DOWN -> right.vy = 0;
                    case KeyEvent.VK_W, KeyEvent.VK_S -> left.vy = 0;
                    case KeyEvent.VK_SPACE -> initBall();
                }
            }
        });

        frame.setVisible(true);
        frame.createBufferStrategy(3);

        BufferStrategy bufferStrategy = frame.getBufferStrategy();

        AtomicInteger ballLevel = new AtomicInteger(0);

        new Timer(100, e -> {
            if (ballLevel.incrementAndGet() % 1000 == 0) {
                double plusOneRatio = (ballSpeed + 1) / ballSpeed;
                ball.speed(ball.vx * plusOneRatio, ball.vy * plusOneRatio);
                ballSpeed++;
            }
            left.move();
            right.move();
            ball.move();

            Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();

            try {
                fondEcran(g, Color.BLACK);

                left.draw(g);
                right.draw(g);
                ball.draw(g);
            } finally {
                g.dispose();
            }
            bufferStrategy.show();
        }).start();
    }

    private void fondEcran(Graphics g, Color color) {
        g.setColor(color);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    private void initBall() {
        RandomGenerator rand = RandomGenerator.getDefault();
        double angle = rand.nextDouble(2 * Math.PI);
        ball.position((SCREEN_WIDTH - Ball.DIAMETER) / 2, (SCREEN_HEIGHT - Ball.DIAMETER) / 2);
        double ballvx = (Math.cos(angle) * ballSpeed);
        double ballvy = (Math.sin(angle) * ballSpeed);
        ball.speed(ballvx, ballvy);
    }

    private void initPads() {
        int padY = (SCREEN_HEIGHT - Pad.HEIGHT) / 2;
        left.position(50, padY);
        right.position(SCREEN_WIDTH - 50 - Pad.WIDTH, padY);
        left.speed(0, 0);
        right.speed(0, 0);
    }
}
