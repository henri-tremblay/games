package pro.tremblay.snake;

import pro.tremblay.framework.CircularQueue;
import pro.tremblay.framework.Geometry;
import pro.tremblay.framework.Sprite;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.List;

class SnakeSprite extends Sprite<Snake> {
    static final double DIAMETER = 50;
    static final double INITIAL_SPEED = 15;
    static final double BOOST_SPEED = 20;
    static final double ENEMY_SPEED = 10;

    private final boolean enemy;
    private Color color;
    private Ball closestBall = null;

    private boolean boosted = false;
    private int length = 0;
    private final CircularQueue<Point2D.Double> positions = new CircularQueue<>(100);

    public SnakeSprite(Snake game) {
        this(game, false);
    }

    public SnakeSprite(Snake game, boolean enemy) {
        super(game);
        this.enemy = enemy;
    }

    @Override
    public void speed(double vx, double vy) {
        super.speed(vx, vy);
        this.color = enemy ? enemyColor() : Color.YELLOW;
    }

    public int length() {
        return length;
    }

    public boolean boosted() {
        return boosted;
    }

    public void follow(Ball closest) {
        if (closest != closestBall) {
            if (closestBall != null) {
                closestBall.unfollowed();
            }
            closestBall = closest;
            closestBall.followed();
        }
        double angle = Geometry.angleBetween(x(), y(), x, y);
        speed(angle);
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
        g.setColor(color);
        // Draw face circle
        g.fillOval((int) x, (int) y, (int) DIAMETER, (int) DIAMETER);

        positions.getFirsts(length).forEach(p -> g.fillOval((int) p.x, (int) p.y, (int) DIAMETER, (int) DIAMETER));

        g.setColor(Color.BLACK);
        // Draw eyes
        g.fillOval((int) (x + 10), (int) (y + 10), 10, 10);
        g.fillOval((int) (x + 30), (int) (y + 10), 10, 10);
        // Draw mouth
        if (enemy) {
            g.drawLine((int) (x + 15), (int) (y + 30), (int) (x + 35), (int) (y + 30));
        } else {
            g.drawArc((int) (x + 10), (int) (y + 20), 30, 20, 180, 180);
        }
    }

    private Color enemyColor() {
        double speed = vx * vx + vy * vy;
        if (speed > 81) {
            return Color.GRAY;
        } else if (speed > 49) {
            return Color.RED;
        } else if (speed > 25) {
            return Color.ORANGE;
        }
        return Color.BLUE;
    }

    public void setInitialSpeed() {
        vx = 0;
        vy = 0;
        boosted = false;
    }

    public void speed(double angle) {
        double speed = enemy ? ENEMY_SPEED : (boosted ? BOOST_SPEED : INITIAL_SPEED);
        speed(Math.cos(angle) * speed, Math.sin(angle) * speed);
    }

    public void boost() {
        if (length == 0) {
            return;
        }
        if (!boosted) {
            vx = vx / INITIAL_SPEED * BOOST_SPEED;
            vy = vy / INITIAL_SPEED * BOOST_SPEED;
            boosted = true;
        }
    }

    public void normal() {
        if (!boosted) {
            return;
        }
        vx = vx / BOOST_SPEED * INITIAL_SPEED;
        vy = vy / BOOST_SPEED * INITIAL_SPEED;
        boosted = false;
    }

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
     * @param snake the snake
     * @return if touched
     */
    public boolean touch(SnakeSprite snake) {
        // If any part of the enemy "This" touches the snake
        double radius = SnakeSprite.DIAMETER / 2;
        double x = snake.x() + radius;
        double y = snake.y() + radius;

        List<Point2D.Double> all = positions.getFirsts(length)
                .toList();
        for (Point2D.Double p : all) {
            double px = p.x + radius;
            double py = p.y + radius;
            if (Geometry.circleIntersect(x, y, radius, px, py, radius)) {
                Snake.snakeTouched = new Point2D.Double(snake.x(), snake.y());
                Snake.enemyTouched = new Point2D.Double(p.x, p.y);
                return true;
            }
        }
        return false;

//        return all.stream().anyMatch(p -> {
//           double px = p.x + radius;
//           double py = p.y + radius;
//           return Geometry.circleIntersect(x, y, radius, px, py, radius);
//        });
    }

    public void targetBall(List<Ball> balls) {
        // no balls, just keep our current route
        if (balls.isEmpty()) {
            return;
        }
        double radius = SnakeSprite.DIAMETER / 2;
        // center of the square
        double x = x() + radius;
        double y = y() + radius;

        // find the closest ball
        double minDistance = Double.MAX_VALUE;
        Ball closest = null;
        for (Ball ball : balls) {
            double distance = ball.squareDistance(x, y);
            if (distance < minDistance) {
                closest = ball;
                minDistance = distance;
            }
        }
        follow(closest);
    }
}
