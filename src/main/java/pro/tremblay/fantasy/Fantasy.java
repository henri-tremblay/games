package pro.tremblay.fantasy;

import pro.tremblay.framework.Game;
import pro.tremblay.framework.Sprite;

import javax.swing.JFrame;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

class World extends Sprite<Fantasy> {
    private Hero hero;
    private final List<Grass> grasses = new ArrayList<>();
    private final List<Tree> trees = new ArrayList<>();
    private final List<Wall> walls = new ArrayList<>();
    private final List<Chest> chests = new ArrayList<>();

    protected World(Fantasy game) {
        super(game);
        readWorld("world.txt");
    }

    public Hero hero() {
        return hero;
    }

    private void readWorld(String file) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResource(file).openStream()))) {
            String line = in.readLine();
            int width = line.length();
            for (int i = 0;; i++) {
                if (line == null) {
                    break;
                }
                for (int j = 0; j < line.length(); j++) {
                    char c = line.charAt(j);
                    if (c == 'h') {
                        hero = new Hero(game);
                        hero.position(j * Hero.DIAMETER, i * Hero.DIAMETER);
                        // Hero has grass under him/her
                        Grass grass = new Grass(game);
                        grass.position(j * Hero.DIAMETER, i * Hero.DIAMETER);
                        grasses.add(grass);
                    } else if (c == 't') {
                        Tree tree = new Tree(game, j * Hero.DIAMETER, i * Hero.DIAMETER);
                        trees.add(tree);
                    } else if (c == 'x') {
                        Wall none = new Wall(game);
                        none.position(j * Hero.DIAMETER, i * Hero.DIAMETER);
                        walls.add(none);
                    } else if (c == 'o') {
                        Grass grass = new Grass(game);
                        grass.position(j * Hero.DIAMETER, i * Hero.DIAMETER);
                        grasses.add(grass);
                    } else if (c == 'c') {
                        Chest chest = new Chest(game);
                        chest.position(j * Hero.DIAMETER, i * Hero.DIAMETER);
                        chests.add(chest);
                    }
                }
                line = in.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void draw(Graphics g) {
        grasses.forEach(t -> drawIfClose(t, g));
        trees.forEach(t -> drawIfClose(t, g));
        chests.forEach(c -> drawIfClose(c, g));
        walls.forEach(n -> drawIfClose(n, g));
        hero.draw(g);
    }

    private void drawIfClose(Sprite<?> s, Graphics g) {
        Point spritePosition = new Point((int) s.x(), (int) s.y());
        double distance = spritePosition.distance((int) hero.x(), (int) hero.y());
        if (distance > Hero.DIAMETER * 5) {
            g.setColor(Color.BLACK);
            g.fillRect((int) s.x(), (int) s.y(), Hero.DIAMETER, Hero.DIAMETER);
        } else {
            s.draw(g);
        }
    }

    public boolean touch(double x, double y) {
//        trees.stream().anyMatch(t -> )
        return false;
    }
}

class Wall extends Sprite<Fantasy> {
    private static final Color BROWN = new Color(160,82,45);

    public Wall(Fantasy game) {
        super(game);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(BROWN);
        g.fillRect((int) x, (int) y, Hero.DIAMETER, Hero.DIAMETER);
    }
}

class Grass extends Sprite<Fantasy> {

    public Grass(Fantasy game) {
        super(game);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillRect((int) x, (int) y, Hero.DIAMETER, Hero.DIAMETER);
    }
}

class Tree extends Sprite<Fantasy> {
    private static final Color GOLD = new Color(255, 215, 0);
    
    protected Tree(Fantasy game, double x, double y) {
        super(game);
        this.x = x;
        this.y = y;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(GOLD);
        g.fillOval((int) x, (int) y, Hero.DIAMETER, Hero.DIAMETER);
    }
}

class Hero extends Sprite<Fantasy> {
    static final int DIAMETER = 50;

    public Hero(Fantasy game) {
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
}

class Chest extends Sprite<Fantasy> {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 30;

    private boolean open = false;

    protected Chest(Fantasy game) {
        super(game);
    }

    @Override
    public Rectangle bounds() {
        return new Rectangle((int) x, (int) y, WIDTH, HEIGHT);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect((int) x, (int) y, WIDTH, HEIGHT);
    }

    public String treasure() {
        if (open) {
            return null;
        }
        open = true;
        RandomGenerator random = RandomGenerator.getDefault();
        int i = random.nextInt(100);
        if (i < 50) {
            return "Common"; // 50%
        }
        if (i < 80) {
            return "Rare"; // 30%
        }
        if (i < 95) {
            return "Epic"; // 15%
        }
        return "Legendary"; // 5%
    }
}

public class Fantasy extends Game {

    public static void main(String[] args) {
        Fantasy app = new Fantasy();
        app.start();
    }

    private final World world = new World(this);

    @Override
    public int screenHeight() {
        return 1000;
    }

    @Override
    public int screenWidth() {
        return 1000;
    }

    @Override
    protected void play(BufferStrategy bufferStrategy) {

        new Timer(100, e -> {
            Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();

            try {
                fondEcran(g, Color.GREEN);
                world.draw(g);
            } finally {
                g.dispose();
            }

            bufferStrategy.show();

//            if(checkCollision()) {
//                String treasure = chest.treasure();
//                if (treasure != null) {
//                    System.out.println(treasure);
//                }
//            }

        }).start();
    }

    @Override
    protected void init(JFrame frame) {
        frame.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                Hero hero = world.hero();
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP -> moveIfPossible(hero.x(), hero.y() - Hero.DIAMETER);
                    case KeyEvent.VK_DOWN -> moveIfPossible(hero.x(), hero.y() + Hero.DIAMETER);
                    case KeyEvent.VK_LEFT -> moveIfPossible(hero.x() - Hero.DIAMETER, hero.y());
                    case KeyEvent.VK_RIGHT -> moveIfPossible(hero.x() + Hero.DIAMETER, hero.y());
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ESCAPE -> System.exit(0);
                }
            }
        });
    }

    @Override
    protected String frameTitle() {
        return "Fantasy Charles";
    }

    private void moveIfPossible(double proposedX, double proposedY) {
        if (!world.touch(proposedX, proposedY)) {
            world.hero().position(proposedX, proposedY);
        }
    }
//
//    boolean checkCollision() {
//        return chest.bounds().intersects(hero.bounds());
//    }
}
