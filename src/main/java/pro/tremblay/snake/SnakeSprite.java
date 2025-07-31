package pro.tremblay.snake;

import pro.tremblay.framework.CircularQueue;
import pro.tremblay.framework.Geometry;
import pro.tremblay.framework.Sprite;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

abstract class SnakeSprite extends Sprite<Snake> {
    static final double DIAMETER = 50;

    protected int length = 0;
    protected final CircularQueue<Point2D.Double> positions = new CircularQueue<>(10_000);

    protected SnakeSprite(Snake game) {
        super(game);
    }

    protected abstract Color color();

    protected double speed() {
        return Math.sqrt(vx * vx + vy * vy);
    }

    public List<Point2D.Double> positions() {
        return positions.getAll().toList();
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
        } else if (y + DIAMETER > game.worldHeight()) {
            y = game.worldHeight() - DIAMETER;
            vy = -vy;
        }

        if (x < 0) {
            x = 0;
            vx = -vx;
        } else if (x + DIAMETER > game.worldWidth()) {
            x = game.worldWidth() - DIAMETER;
            vx = -vx;
        }

        positions.add(new Point2D.Double(x, y));
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(color());
        // Draw face as a circle
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

    /**
     * Check if an enemy touch the snake. "this" is the enemy
     *
     * @param other@return if touched
     */
    boolean touch(SnakeSprite other) {
        // If any part of the enemy "This" touches the snake
        double radius = SnakeSprite.DIAMETER / 2;
        double x = x() + radius;
        double y = y() + radius;

        List<Point2D.Double> all = other.positions.getFirsts(other.length)
                .toList();
        for (Point2D.Double p : all) {
            double px = p.x + radius;
            double py = p.y + radius;
            if (Geometry.circleIntersect(x, y, radius, px, py, radius)) {
                return true;
            }
        }
        return false;
    }
}
