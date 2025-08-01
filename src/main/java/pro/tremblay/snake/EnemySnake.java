package pro.tremblay.snake;

import pro.tremblay.framework.Geometry;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

public class EnemySnake extends SnakeSprite {

    private Ball closestBall = null;

    public EnemySnake(Snake game, String name) {
        super(game, name);
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

    protected void drawMouth(Graphics g) {
        g.drawLine((int) (x + 15), (int) (y + 30), (int) (x + 35), (int) (y + 30));
    }

    void follow(Ball closest) {
        if (closest != closestBall) {
            unfollow();
            closestBall = closest;
            closestBall.followed();
        }
        double angle = Geometry.angleBetween(x, y, closest.x(), closest.y());
        changeDirection(angle);
    }

    public void unfollow() {
        if (closestBall != null) {
            closestBall.unfollowed();
        }
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

}
