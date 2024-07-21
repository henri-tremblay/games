package pro.tremblay.learn;

import pro.tremblay.framework.Game;
import pro.tremblay.framework.Sprite;

import javax.swing.JFrame;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;

class Apple extends Sprite<Learn> {

    protected Apple(Learn game) {
        super(game);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.red);
        g.fillRect(75,75 , 25, 25);
    }
}

public class Learn extends Game {

    private static final int CELL_SIZE = 25;
    private static final int SPEED = 25;
    private int x = 0, y = 0;
    private int vx = SPEED, vy = 0;

    public static void main(String[] args) {
        Learn app = new Learn();
        app.start();
    }

    @Override
    public int screenHeight() {
        return 800;
    }

    @Override
    public int screenWidth() {
        return 800;
    }

    @Override
    protected void play(BufferStrategy bufferStrategy) {

        new Timer(80, e -> {

            Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
            try {
                fondEcran(g, Color.BLACK);

                g.setColor(Color.blue);
                g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
            } finally {
                g.dispose();
            }

            x += vx;
            y += vy;

//            if ( x > screenWidth() - CELL_SIZE) {
//                vy = 10;
//                vx = 0;
//                x = screenWidth() - CELL_SIZE;
//            }

            bufferStrategy.show();
        }).start();

    }

    @Override
    protected void fondEcran(Graphics g, Color color) {
        super.fondEcran(g, color);

        g.setColor(Color.WHITE);

        // Draw vertical lines.
        for (int i = CELL_SIZE; i < screenWidth(); i += CELL_SIZE) {
            g.drawLine(i, 0, i, screenHeight());
        }

        // Draw horizontal lines.
        for (int i = CELL_SIZE; i < screenHeight(); i += CELL_SIZE) {
            g.drawLine(0, i, screenWidth(), i);
        }
    }

    @Override
    protected void init(JFrame frame) {

        frame.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP -> speed(0, -SPEED);
                    case KeyEvent.VK_DOWN -> speed(0, SPEED);
                    case KeyEvent.VK_RIGHT -> speed(SPEED, 0);
                    case KeyEvent.VK_LEFT -> speed(-SPEED, 0);
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

    private void speed(int vx, int vy) {
        this.vx = vx;
        this.vy = vy;
    }


    @Override
    protected String frameTitle() {
        return "Learn Charles";
    }

}
