package pro.tremblay.frogger;

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
import java.util.stream.Stream;

class Frog extends Sprite {
    static final int DIAMETER = 50;

    public Frog(Game game) {
        super(game);
    }

    public Rectangle bounds() {
        return new Rectangle((int) x, (int) y, DIAMETER, DIAMETER);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval((int) x, (int) y, DIAMETER, DIAMETER);
    }

    @Override
    public Sprite x(double x) {
        if (x + DIAMETER > game.screenWidth()) {
            x = game.screenWidth() - DIAMETER;
        } else if (x - DIAMETER < 0) {
            x = DIAMETER;
        }

        return super.x(x);
    }

    @Override
    public Sprite y(double y) {
        if (y + DIAMETER > game.screenHeight()) {
            y = game.screenHeight() - DIAMETER;
        } else if (y - DIAMETER < 0) {
            y = DIAMETER;
        }

        return super.y(y);
    }
}

class Road extends Sprite {

    public Road(Game game, int y) {
        super(game);
        this.x = 0;
        this.y = y;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.GRAY);
        g.fillRect((int) x, (int) y, game.screenWidth(), Frog.DIAMETER * 3);
    }
}

class Car extends Sprite {
    public static final int LENGTH = Frog.DIAMETER * 2;
    protected Car(Game game, int x, int y) {
        super(game);
        this.x = x;
        this.y = y;
        this.vy = 0;
        this.vx = 15;
    }

    public Rectangle bounds() {
        return new Rectangle((int) x, (int) y, LENGTH, LENGTH);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect((int) x, (int) y, LENGTH, Frog.DIAMETER);
    }

    @Override
    public void move() {
        super.move();
        if (x > game.screenWidth()) {
            x = - LENGTH;
        }
    }
}

public class Frogger extends Game {

    public static void main(String[] args) {
        Frogger app = new Frogger();
        app.start();
    }

    private final Frog frog = new Frog(this);
    private final Road road = new Road(this, screenHeight() - Frog.DIAMETER * 5);
    private final Car[] cars = new Car[3];

    @Override
    public int screenHeight() {
        return 700;
    }

    @Override
    public int screenWidth() {
        return 1000;
    }

    @Override
    protected void play(BufferStrategy bufferStrategy) {

        new Timer(100, e -> {
            for (Car car : cars) {
                car.move();
            }

            if(checkCollision()) {
                initFrog();
            }

            Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();

            try {
                fondEcran(g, Color.GREEN);
                road.draw(g);
                frog.draw(g);
                for (Car car : cars) {
                    car.draw(g);
                }
            } finally {
                g.dispose();
            }

            bufferStrategy.show();
        }).start();
    }

    @Override
    protected void init(JFrame frame) {
        frame.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP -> frog.y(frog.y() - Frog.DIAMETER);
                    case KeyEvent.VK_DOWN -> frog.y(frog.y() + Frog.DIAMETER);
//                    case KeyEvent.VK_LEFT -> frog.x(frog.x() - Frog.DIAMETER);
//                    case KeyEvent.VK_RIGHT -> frog.x(frog.x() + Frog.DIAMETER);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ESCAPE -> System.exit(0);
                }
            }
        });

        initFrog();
        initCars();
    }

    private void initFrog() {
        frog.x(screenWidth() / 2);
        frog.y(screenHeight() - Frog.DIAMETER);
    }

    private void initCars() {
        for (int i = 0; i < cars.length; i++) {
            Car car = new Car(this, -(2 * Car.LENGTH) * i, screenHeight() - Frog.DIAMETER * (i + 3));
            cars[i] = car;
        }
    }

    @Override
    protected String frameTitle() {
        return "Frogger Édouard";
    }

    private void fondEcran(Graphics g, Color color) {
        g.setColor(color);
        g.fillRect(0, 0, screenWidth(), screenHeight());
    }

    boolean checkCollision() {
        return Stream.of(cars)
                .map(Car::bounds)
                .anyMatch(frog.bounds()::intersects);
    }
}
