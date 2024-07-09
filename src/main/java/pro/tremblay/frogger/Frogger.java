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

public class Frogger extends Game {

    public static void main(String[] args) {
        Frogger app = new Frogger();
        app.start();
    }

    @Override
    protected void play(BufferStrategy bufferStrategy) {

        new Timer(100, e -> {
            Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();

            try {
                fondEcran(g, Color.GREEN);
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
        return "Frogger Édouard";
    }

    private void fondEcran(Graphics g, Color color) {
        g.setColor(color);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

}
