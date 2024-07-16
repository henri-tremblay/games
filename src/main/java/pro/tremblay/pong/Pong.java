package pro.tremblay.pong;

import pro.tremblay.framework.Game;
import pro.tremblay.framework.Sprite;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.random.RandomGenerator;

// Collision balle avec bas à corriger
// Rebondir sur les palettes
// Augmenter la balle de vitesse à chaque 10 secondes xxxx
// Écrire le pointage xxx
// Utiliser un tampon d'affichage xxx

class Pad extends Sprite<Pong> {
    static final int WIDTH = 30;
    static final int HEIGHT = 150;
    static final int INITIAL_SPEED = 15;

    public Pad(Pong game) {
        super(game);
    }

    @Override
    public void move() {
        super.move();
        if (y < 0) {
            y  = 0;
        } else if (y + HEIGHT > game.screenHeight()) {
            y = game.screenHeight() - HEIGHT;
        }
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect((int) x, (int) y, WIDTH, HEIGHT);

    }

}

class Ball extends Sprite<Pong> {
    static final int DIAMETER = 50;

    public Ball(Pong game) {
        super(game);
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
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillOval((int) x, (int) y, DIAMETER, DIAMETER);
    }
}

class Score extends Sprite<Pong> {
    int left, right;

    public Score(Pong game) {
        super(game);
    }

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

public class Pong extends Game {
    static double ballSpeed = 15;

    public static void main(String[] args) {
        Pong app = new Pong();
        app.start();
    }

    private final Pad left = new Pad(this);
    private final Pad right = new Pad(this);
    private final Ball ball = new Ball(this);
    private final Score score = new Score(this);

    @Override
    protected void play(BufferStrategy bufferStrategy) {
        AtomicInteger ballLevel = new AtomicInteger(0);

        new Timer(100, e -> {
            if (ballLevel.incrementAndGet() % 50 == 0) {
                double plusOneRatio = (ballSpeed + 2) / ballSpeed;
                ball.speed(ball.vx() * plusOneRatio, ball.vy() * plusOneRatio);
                ballSpeed++;
            }
            left.move();
            right.move();

            ball.move();

            collisionLeft();
            collisionRight();

            if (ball.x() <= 50) {
                score.right++;
                initBall();
            } else if (ball.x() > screenWidth() - 50) {
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

    @Override
    protected void init(JFrame frame) {
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
                    case KeyEvent.VK_UP -> right.vy(-Pad.INITIAL_SPEED);
                    case KeyEvent.VK_DOWN -> right.vy(Pad.INITIAL_SPEED);
                    case KeyEvent.VK_W -> left.vy(-Pad.INITIAL_SPEED);
                    case KeyEvent.VK_S -> left.vy(Pad.INITIAL_SPEED);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP, KeyEvent.VK_DOWN -> right.vy(0);
                    case KeyEvent.VK_W, KeyEvent.VK_S -> left.vy(0);
                    case KeyEvent.VK_SPACE -> initBall();
                    case KeyEvent.VK_ESCAPE -> System.exit(0);
                }
            }
        });
    }

    @Override
    protected String frameTitle() {
        return "Pong Édouard";
    }

    private void collisionRight() {
        collision(right);
    }

    private void collisionLeft() {
        collision(left);
    }

    private void collision(Pad pad) {
        Rectangle2D.Double padRect = new Rectangle2D.Double(pad.x(), pad.y(), Pad.WIDTH, Pad.HEIGHT);
        Rectangle2D.Double ballRect = new Rectangle2D.Double(ball.x(), ball.y(), Ball.DIAMETER, Ball.DIAMETER);
        if (padRect.intersects(ballRect)) {
            ball.speed(-ball.vx(), ball.vy());
        }
    }

    private void ligneDuCentre(Graphics2D g) {
        g.setColor(Color.YELLOW);
        g.drawLine(screenWidth() / 2, 0, screenWidth() / 2, screenHeight());
    }

    private void initBall() {
        ballSpeed = 15;
        double angle = findGoodAngle();
        ball.position((screenWidth() - Ball.DIAMETER) / 2.0, (screenHeight() - Ball.DIAMETER) / 2.0);
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
        int padY = (screenHeight() - Pad.HEIGHT) / 2;
        left.position(50, padY);
        right.position(screenWidth() - 50 - Pad.WIDTH, padY);
        left.speed(0, 0);
        right.speed(0, 0);
    }

    private void initScore() {
        score.position(screenWidth() / 2.0 - 15, 40);
    }
}
