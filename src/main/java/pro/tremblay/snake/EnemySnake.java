package pro.tremblay.snake;

import pro.tremblay.framework.Geometry;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.List;

public class EnemySnake extends SnakeSprite {

    private Ball closestBall = null;

    public EnemySnake(Snake game) {
        super(game);
    }

    @Override
    protected Color color() {
        double speed = speed();
        if (speed > 9) {
            return Color.GRAY;
        } else if (speed > 7) {
            return Color.RED;
        } else if (speed > 5) {
            return Color.ORANGE;
        }
        return Color.BLUE;
    }

    @Override
    protected void changeDirection(double angle) {
        double speed = speed();
        speed(Math.cos(angle) * speed, Math.sin(angle) * speed);
    }

    protected void drawMouth(Graphics g) {
        g.drawLine((int) (x + 15), (int) (y + 30), (int) (x + 35), (int) (y + 30));
    }

    void follow(Ball closest) {
        if (closest != closestBall) {
            if (closestBall != null) {
                closestBall.unfollowed();
            }
            closestBall = closest;
            closestBall.followed();
        }
        double angle = Geometry.angleBetween(x, y, closest.x(), closest.y());
        changeDirection(angle);
    }

    Ball targetBall(List<Ball> balls) {
        // no balls, just keep our current route
        if (balls.isEmpty()) {
            return null;
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
        return closest;
    }

    /**
     * Check if an enemy touch the snake. "this" is the enemy
     *
     * @param snake the snake
     * @return if touched
     */
    boolean touch(PlayerSnake snake) {
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
                return true;
            }
        }
        return false;
    }
}
