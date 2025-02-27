package pro.tremblay.snake;

import pro.tremblay.framework.CircularQueue;
import pro.tremblay.framework.Sprite;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;

abstract class SnakeSprite extends Sprite<Snake> {
    static final double DIAMETER = 50;

    protected int length = 0;
    protected final CircularQueue<Point2D.Double> positions = new CircularQueue<>(100);

    protected SnakeSprite(Snake game) {
        super(game);
    }

    protected abstract Color color();

    protected double speed() {
        return Math.sqrt(vx * vx + vy * vy);
    }

    @Override
    public void speed(double vx, double vy) {
        super.speed(vx, vy);
    }

    public int length() {
        return length;
    }

    protected void changeDirection(double angle) {
        double speed = speed();
        speed(Math.cos(angle) * speed, Math.sin(angle) * speed);
    }

    @Override
    public void move() {
        super.move();

        if (y < 0) {
            y = 0;
            vy = -vy;
        } else if (y + DIAMETER > game.screenHeight()) {
            y = game.screenHeight() - DIAMETER;
            vy = -vy;
        }

        if (x < 0) {
            x = 0;
            vx = -vx;
        } else if (x + DIAMETER > game.screenWidth()) {
            x = game.screenWidth() - DIAMETER;
            vx = -vx;
        }

        positions.add(new Point2D.Double(x, y));
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(color());
        // Draw face circle
        g.fillOval((int) x, (int) y, (int) DIAMETER, (int) DIAMETER);

        positions.getFirsts(length).forEach(p -> g.fillOval((int) p.x, (int) p.y, (int) DIAMETER, (int) DIAMETER));

        g.setColor(Color.BLACK);

        drawEyes(g);
        drawMouth(g);
    }

    private void drawEyes(Graphics g) {
        g.fillOval((int) (x + 10), (int) (y + 10), 10, 10);
        g.fillOval((int) (x + 30), (int) (y + 10), 10, 10);
    }

    protected abstract void drawMouth(Graphics g);

    public void addRing() {
        length++;
        if (positions.size() < length) {
            positions.add(new Point2D.Double(x, y));
        }
    }

    public void removeRing() {
        if (length > 0) {
            length--;
        }
    }

    public void clearRings() {
        length = 0;
    }

}
