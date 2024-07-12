package pro.tremblay.frogger;

import pro.tremblay.framework.Game;
import pro.tremblay.framework.Sprite;

import javax.swing.JFrame;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

class Frog extends Sprite {
    static final int DIAMETER = 50;

    public Frog(Game game) {
        super(game);
    }

    public Rectangle bounds() {
        return new Rectangle((int) x, (int) y, DIAMETER - 1, DIAMETER - 1);
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
    private final Car[] line1 = new Car[6];
    private final Car[] line2 = new Car[6];
    private final Car[] line3 = new Car[6];

    public Road(Game game, int y) {
        super(game);
        this.x = 0;
        this.y = y;
        for (int i = 0; i < line1.length; i++) {
            line1[i] = new Car(game, i * Car.LENGTH * 2, y, 5, 0);
        }
        for (int i = 0; i < line2.length; i++) {
            line2[i] = new Car(game, Car.LENGTH / 2  + i * Car.LENGTH * 2, y + Car.HEIGHT, -6, 0);
        }
        for (int i = 0; i < line3.length; i++) {
            line3[i] = new Car(game, Car.LENGTH / 4  + i * Car.LENGTH * 3, y + Car.HEIGHT * 2, 7, 0);
        }
    }

    @Override
    public Rectangle bounds() {
        return new Rectangle((int) x, (int) y, game.screenWidth(), Frog.DIAMETER * 3);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.GRAY);
        g.fillRect((int) x, (int) y, game.screenWidth(), Frog.DIAMETER * 3);
        Arrays.stream(line1).forEach(c -> c.draw(g));
        Arrays.stream(line2).forEach(c -> c.draw(g));
        Arrays.stream(line3).forEach(c -> c.draw(g));
    }

    @Override
    public void move() {
        super.move();
        Arrays.stream(line1).forEach(Car::move);
        Arrays.stream(line2).forEach(Car::move);
        Arrays.stream(line3).forEach(Car::move);
    }

    @Override
    public boolean touch(Sprite frog) {
        if (!bounds().intersects(frog.bounds())) {
            return false;
        }
        return Stream.of(line1).map(Car::bounds).anyMatch(frog.bounds()::intersects)
                || Stream.of(line2).map(Car::bounds).anyMatch(frog.bounds()::intersects)
                || Stream.of(line3).map(Car::bounds).anyMatch(frog.bounds()::intersects);
    }
}

class Car extends Sprite {
    public static final int LENGTH = Frog.DIAMETER * 2;
    public static final int HEIGHT = Frog.DIAMETER;

    protected Car(Game game, int x, int y, int vx, int vy) {
        super(game);
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    public Rectangle bounds() {
        return new Rectangle((int) x, (int) y, LENGTH, Frog.DIAMETER - 2);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect((int) x, (int) y, LENGTH, Frog.DIAMETER - 2);
    }

    @Override
    public void move() {
        super.move();
        if (x > game.screenWidth()) {
            x = - LENGTH;
        }
        else if (x < - LENGTH) {
            x = game.screenWidth();
        }
    }
}

class Railroad extends Sprite {
    private static final Color BROWN = new Color(255, 255, 173);
    private final Train train;

    protected Railroad(Game game, int y, int vx) {
        super(game);
        this.y = y;
        train = new Train(game, -Train.LENGTH, y, vx, 0);
    }

    @Override
    public Rectangle bounds() {
        return new Rectangle((int) x, (int) y, game.screenWidth(), Frog.DIAMETER);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(BROWN);
        g.fillRect(0, (int) y, game.screenWidth(), Frog.DIAMETER);
        g.setColor(Color.DARK_GRAY);
        g.drawLine(0, (int) y + Frog.DIAMETER / 3, game.screenWidth(), (int) y + Frog.DIAMETER / 3);
        g.drawLine(0, (int) y + 2 * Frog.DIAMETER / 3, game.screenWidth(), (int) y + 2 * Frog.DIAMETER / 3);
        train.draw(g);
    }

    @Override
    public void move() {
        super.move();
        train.move();
    }

    @Override
    public boolean touch(Sprite frog) {
        if (!bounds().intersects(frog.bounds())) {
            return false;
        }
        return train.bounds().intersects(frog.bounds());
    }
}

class Train extends Sprite {
    public static final int LENGTH = Frog.DIAMETER * 4;
    public static final int HEIGHT = Frog.DIAMETER;

    protected Train(Game game, int x, int y, int vx, int vy) {
        super(game);
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    public Rectangle bounds() {
        return new Rectangle((int) x, (int) y, LENGTH, HEIGHT);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect((int) x, (int) y, LENGTH, HEIGHT);
    }

    @Override
    public void move() {
        super.move();
        if (x > game.screenWidth()) {
            x = - LENGTH;
        }
        else if (x < - LENGTH) {
            x = game.screenWidth();
        }
    }
}

class River extends Sprite {
    private final Trunk[] line1 = new Trunk[6];
    private final Trunk[] line2 = new Trunk[6];
    private final Trunk[] line3 = new Trunk[6];

    public River(Game game, int y) {
        super(game);
        this.x = 0;
        this.y = y;
        for (int i = 0; i < line1.length; i++) {
            line1[i] = new Trunk(game, i * Car.LENGTH * 2, y, 5, 0);
        }
        for (int i = 0; i < line2.length; i++) {
            line2[i] = new Trunk(game, Car.LENGTH / 2  + i * Car.LENGTH * 2, y + Car.HEIGHT, -6, 0);
        }
        for (int i = 0; i < line3.length; i++) {
            line3[i] = new Trunk(game, Car.LENGTH / 4  + i * Car.LENGTH * 3, y + Car.HEIGHT * 2, 7, 0);
        }
    }

    @Override
    public Rectangle bounds() {
        return new Rectangle((int) x, (int) y, game.screenWidth(), Frog.DIAMETER * 3);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect((int) x, (int) y, game.screenWidth(), Frog.DIAMETER * 3);
        Arrays.stream(line1).forEach(c -> c.draw(g));
        Arrays.stream(line2).forEach(c -> c.draw(g));
        Arrays.stream(line3).forEach(c -> c.draw(g));
    }

    @Override
    public void move() {
        super.move();
        Arrays.stream(line1).forEach(Trunk::move);
        Arrays.stream(line2).forEach(Trunk::move);
        Arrays.stream(line3).forEach(Trunk::move);
    }

    @Override
    public boolean touch(Sprite frog) {
        if (!bounds().intersects(frog.bounds())) {
            return false;
        }
        return Stream.of(line1).map(Trunk::bounds).anyMatch(Predicate.not(frog.bounds()::intersects))
                && Stream.of(line2).map(Trunk::bounds).anyMatch(Predicate.not(frog.bounds()::intersects))
                && Stream.of(line3).map(Trunk::bounds).anyMatch(Predicate.not(frog.bounds()::intersects));
    }
}

class Trunk extends Sprite {
    private static final Color BROWN = new Color(255, 173, 173);
    public static final int LENGTH = Frog.DIAMETER * 3;
    public static final int HEIGHT = Frog.DIAMETER;

    protected Trunk(Game game, int x, int y, int vx, int vy) {
        super(game);
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    public Rectangle bounds() {
        return new Rectangle((int) x, (int) y, LENGTH, Frog.DIAMETER - 2);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(BROWN);
        g.fillRect((int) x, (int) y, LENGTH, Frog.DIAMETER - 2);
    }

    @Override
    public void move() {
        super.move();
        if (x > game.screenWidth()) {
            x = - LENGTH;
        }
        else if (x < - LENGTH) {
            x = game.screenWidth();
        }
    }
}

public class Frogger extends Game {

    public static void main(String[] args) {
        Frogger app = new Frogger();
        app.start();
    }

    private final Frog frog = new Frog(this);
    private final List<Sprite> obstacles = List.of(
            new Road(this, screenHeight() - Frog.DIAMETER * 5),
            new Railroad(this, screenHeight() - Frog.DIAMETER * 2, 30),
            new Railroad(this, screenHeight() - Frog.DIAMETER, 10),
            new River(this, Frog.DIAMETER * 2)
    );

    @Override
    public int screenHeight() {
        return 700;
    }

    @Override
    public int screenWidth() {
        return 1200;
    }

    @Override
    protected void play(BufferStrategy bufferStrategy) {

        new Timer(100, e -> {
            obstacles.forEach(Sprite::move);

            Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();

            try {
                fondEcran(g, Color.GREEN);
                obstacles.forEach(s -> s.draw(g));
                frog.draw(g);
            } finally {
                g.dispose();
            }

            bufferStrategy.show();

            if(checkCollision()) {
                initFrog();
            }
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
                    case KeyEvent.VK_LEFT -> frog.x(frog.x() - Frog.DIAMETER);
                    case KeyEvent.VK_RIGHT -> frog.x(frog.x() + Frog.DIAMETER);
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
    }

    private void initFrog() {
        frog.x(screenWidth() / 2);
        frog.y(screenHeight() - Frog.DIAMETER);
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
        return obstacles.stream().anyMatch(o -> o.touch(frog));
    }
}
