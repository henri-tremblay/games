package pro.tremblay.framework;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;

public abstract class Game {

    public int screenWidth() {
        return 1024;
    }

    public int screenHeight() {
        return 768;
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

    protected abstract String frameTitle();

    protected abstract void play(BufferStrategy bufferStrategy);

    protected abstract void init(JFrame frame);
}