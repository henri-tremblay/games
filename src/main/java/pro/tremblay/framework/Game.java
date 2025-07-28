package pro.tremblay.framework;

import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferStrategy;
import java.util.Locale;
import java.util.ResourceBundle;

public abstract class Game {

    private final ResourceBundle resourceBundle = ResourceBundle.getBundle("translation.resource", Locale.CANADA_FRENCH);

    public int screenWidth() {
        return 1024;
    }

    public int screenHeight() {
        return 768;
    }

    public int worldWidth() {
        return screenWidth();
    }

    public int worldHeight() {
        return screenHeight();
    }

    public Rectangle bounds() {
        return new Rectangle(0, 0, screenWidth(), screenHeight());
    }

    public void start() {

        JFrame frame = new JFrame(frameTitle());
        frame.setSize(new Dimension(screenWidth(), screenHeight()));
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);

        init(frame);

        frame.setVisible(true);
        frame.createBufferStrategy(3);

        BufferStrategy bufferStrategy = frame.getBufferStrategy();

        play(bufferStrategy);
    }

    public String resource(String key) {
        return resourceBundle.getString(key);
    }

    protected abstract String frameTitle();

    protected abstract void play(BufferStrategy bufferStrategy);

    protected abstract void init(JFrame frame);

    protected void background(Graphics g, Color color) {
        g.setColor(color);
        g.fillRect(0, 0, screenWidth(), screenHeight());
    }
}
