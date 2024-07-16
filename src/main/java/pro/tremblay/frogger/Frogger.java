package pro.tremblay.frogger;

import pro.tremblay.framework.Game;
import pro.tremblay.framework.Sprite;

import javax.swing.JFrame;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

interface HasSpeed {
    void setSpeed();
}

class Frog extends Sprite<Frogger> {
    static final int DIAMETER = 50;

    public Frog(Frogger game) {
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
}

class Road extends Sprite<Frogger> implements HasSpeed {
    private static final int LINE1_SPEED = 5;
    private static final int LINE2_SPEED = -6;
    private static final int LINE3_SPEED = 7;

    private final Car[] line1 = new Car[6];
    private final Car[] line2 = new Car[6];
    private final Car[] line3 = new Car[6];

    public Road(Frogger game, double y) {
        super(game);
        this.x = 0;
        this.y = y;
        for (int i = 0; i < line1.length; i++) {
            line1[i] = new Car(game, i * Car.LENGTH * 3, y, LINE1_SPEED, 0);
        }
        for (int i = 0; i < line2.length; i++) {
            line2[i] = new Car(game, Car.LENGTH / 3.0  + i * Car.LENGTH * 2, y + Car.HEIGHT, LINE2_SPEED, 0);
        }
        for (int i = 0; i < line3.length; i++) {
            line3[i] = new Car(game, Car.LENGTH / 4.0  + i * Car.LENGTH * 3, y + Car.HEIGHT * 2, LINE3_SPEED, 0);
        }
    }

    @Override
    public void setSpeed() {
        for (Car car : line1) {
            car.vx(LINE1_SPEED * game.speedCoefficient());
        }
        for (Car car : line2) {
            car.vx(LINE2_SPEED * game.speedCoefficient());
        }
        for (Car car : line3) {
            car.vx(LINE3_SPEED * game.speedCoefficient());
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
    public boolean touch(Sprite<Frogger> frog) {
        if (!bounds().intersects(frog.bounds())) {
            return false;
        }
        return Stream.of(line1).map(Car::bounds).anyMatch(frog.bounds()::intersects)
                || Stream.of(line2).map(Car::bounds).anyMatch(frog.bounds()::intersects)
                || Stream.of(line3).map(Car::bounds).anyMatch(frog.bounds()::intersects);
    }
}

class Car extends Sprite<Frogger> {
    public static final int LENGTH = Frog.DIAMETER * 2;
    public static final int HEIGHT = Frog.DIAMETER;

    protected Car(Frogger game, double x, double y, double vx, double vy) {
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

class Railroad extends Sprite<Frogger> implements HasSpeed {
    private static final Color BROWN = new Color(255, 255, 173);
    private final Train train;
    private final double initialTrainSpeed;

    protected Railroad(Frogger game, double y, double vx) {
        super(game);
        this.y = y;
        this.initialTrainSpeed = vx;
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
    public void setSpeed() {
        train.vx(initialTrainSpeed * game.speedCoefficient());
    }

    @Override
    public boolean touch(Sprite<Frogger> frog) {
        if (!bounds().intersects(frog.bounds())) {
            return false;
        }
        return train.bounds().intersects(frog.bounds());
    }
}

class Train extends Sprite<Frogger> {
    public static final int LENGTH = Frog.DIAMETER * 4;
    public static final int HEIGHT = Frog.DIAMETER;

    protected Train(Frogger game, double x, double y, double vx, double vy) {
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

class River extends Sprite<Frogger> implements HasSpeed {
    private static final int LINE1_SPEED = 5;
    private static final int LINE2_SPEED = -6;
    private static final int LINE3_SPEED = 7;

    private boolean inTheRiver = false;
    private final Trunk[] line1 = new Trunk[6];
    private final Trunk[] line2 = new Trunk[6];
    private final Trunk[] line3 = new Trunk[6];

    public River(Frogger game, double y) {
        super(game);
        this.x = 0;
        this.y = y;
        for (int i = 0; i < line1.length; i++) {
            line1[i] = new Trunk(game, i * Car.LENGTH * 2, y, LINE1_SPEED, 0);
        }
        for (int i = 0; i < line2.length; i++) {
            line2[i] = new Trunk(game, Car.LENGTH / 2.0  + i * Car.LENGTH * 2, y + Car.HEIGHT, LINE2_SPEED, 0);
        }
        for (int i = 0; i < line3.length; i++) {
            line3[i] = new Trunk(game, Car.LENGTH / 4.0  + i * Car.LENGTH * 3, y + Car.HEIGHT * 2, LINE3_SPEED, 0);
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
    public void setSpeed() {
        for (Trunk trunk : line1) {
            trunk.vx(LINE1_SPEED * game.speedCoefficient());
        }
        for (Trunk trunk : line2) {
            trunk.vx(LINE2_SPEED * game.speedCoefficient());
        }
        for (Trunk trunk : line3) {
            trunk.vx(LINE3_SPEED * game.speedCoefficient());
        }
    }

    @Override
    public boolean touch(Sprite<Frogger> frog) {
        if (!bounds().intersects(frog.bounds())) {
            if (inTheRiver) {
                inTheRiver = false;
                frog.vx(0);
            }
            return false;
        }
        inTheRiver = true;
        if (touchTrunk(frog, line1)) {
            return false;
        }
        if (touchTrunk(frog, line2)) {
            return false;
        }
        if (touchTrunk(frog, line3)) {
            return false;
        }
        return true;
    }

    private boolean touchTrunk(Sprite<Frogger> frog, Trunk[] line) {
        Optional<Trunk> trunkOptional = Stream.of(line)
                .filter(trunk -> trunk.bounds().contains(frog.bounds()))
                .findFirst();
        if (trunkOptional.isPresent()) {
            frog.vx(trunkOptional.get().vx());
            return true;
        }
        return false;
    }
}

class Trunk extends Sprite<Frogger> {
    private static final Color BROWN = new Color(0X62, 0x2A, 0x0F);
    public static final int LENGTH = Frog.DIAMETER * 3;
    public static final int HEIGHT = Frog.DIAMETER;

    protected Trunk(Frogger game, double x, double y, double vx, double vy) {
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
        g.setColor(BROWN);
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

class Victory extends Sprite<Frogger> {

    protected Victory(Frogger game) {
        super(game);
    }

    @Override
    public Rectangle bounds() {
        return new Rectangle(0, 0, game.screenWidth(), Frog.DIAMETER);
    }

    @Override
    public void draw(Graphics g) {
        for (int i = 0; i < game.screenWidth() / (Frog.DIAMETER * 2); i++) {
            g.setColor(Color.WHITE);
            g.fillRect(i * Frog.DIAMETER * 2, 0, Frog.DIAMETER, Frog.DIAMETER);
            g.setColor(Color.BLACK);
            g.fillRect(i * Frog.DIAMETER * 2 + Frog.DIAMETER, 0, Frog.DIAMETER, Frog.DIAMETER);
        }
    }

    @Override
    public boolean touch(Sprite<Frogger> sprite) {
        return sprite.bounds().intersects(bounds());
    }
}

public class Frogger extends Game {

    public static void main(String[] args) {
        Frogger app = new Frogger();
        app.start();
    }

    private int level = 1;
    private final Frog frog = new Frog(this);
    private final Victory victory = new Victory(this);
    private final List<Sprite<Frogger>> obstacles = List.of(
            new Road(this, screenHeight() - Frog.DIAMETER * 5),
            new Railroad(this, screenHeight() - Frog.DIAMETER * 2, 30),
            new Railroad(this, 7 * Frog.DIAMETER, 10),
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

    public double speedCoefficient() {
        return 1 + 0.1 * level;
    }

    @Override
    protected void play(BufferStrategy bufferStrategy) {

        new Timer(100, e -> {
            frog.move();
            obstacles.forEach(Sprite::move);

            Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();

            try {
                fondEcran(g, Color.GREEN);
                victory.draw(g);
                obstacles.forEach(s -> s.draw(g));
                frog.draw(g);
                drawLevel(g);
            } finally {
                g.dispose();
            }

            bufferStrategy.show();

            if(checkCollision()) {
                initFrog();
                return;
            }

            if (victory.touch(frog)) {
                level++;
                setSpeed();
                initFrog();
            }
        }).start();
    }

    private void setSpeed() {
        obstacles
                .stream()
                .map(o -> (HasSpeed) o)
                .forEach(HasSpeed::setSpeed);
    }

    private void drawLevel(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(8, 10, 90, 30);
        g.setColor(Color.YELLOW);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        g.drawString("Level: " + level, 10, 30);
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
        frog.x(screenWidth() / 2.0);
        frog.y(screenHeight() - Frog.DIAMETER);
    }

    @Override
    protected String frameTitle() {
        return "Frogger Édouard";
    }

    boolean checkCollision() {
        if (!bounds().contains(frog.bounds())) {
            return true;
        }
        return obstacles.stream().anyMatch(o -> o.touch(frog));
    }
}
