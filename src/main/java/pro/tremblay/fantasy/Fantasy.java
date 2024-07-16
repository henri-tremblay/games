package pro.tremblay.fantasy;

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

public class Fantasy extends Game {

    public static void main(String[] args) {
        Fantasy app = new Fantasy();
        app.start();
    }

    private final Hero hero = new Hero(this);

    @Override
    public int screenHeight() {
        return 1200;
    }

    @Override
    public int screenWidth() {
        return 1200;
    }

    @Override
    protected void play(BufferStrategy bufferStrategy) {

        new Timer(100, e -> {
            Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();

            try {
                fondEcran(g, Color.GREEN);
                hero.draw(g);
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
                    case KeyEvent.VK_UP -> hero.y(hero.y() - Hero.DIAMETER);
                    case KeyEvent.VK_DOWN -> hero.y(hero.y() + Hero.DIAMETER);
                    case KeyEvent.VK_LEFT -> hero.x(hero.x() - Hero.DIAMETER);
                    case KeyEvent.VK_RIGHT -> hero.x(hero.x() + Hero.DIAMETER);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ESCAPE -> System.exit(0);
                }
            }
        });

        initHero();
    }

    private void initHero() {
        hero.x(screenWidth() / 2.0);
        hero.y(screenHeight() / 2.0);
    }

    @Override
    protected String frameTitle() {
        return "Fantasy Charles";
    }

}
