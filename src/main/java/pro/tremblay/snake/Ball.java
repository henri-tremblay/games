package pro.tremblay.snake;

import pro.tremblay.framework.Geometry;
import pro.tremblay.framework.Sprite;

import java.awt.Color;
import java.awt.Graphics;

class Ball extends Sprite<Snake> {

    private int followers = 0;

    protected Ball(Snake game) {
        super(game);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(followers > 0 ? Color.BLUE : Color.RED);
        // Draw face circle
        g.fillOval((int) x, (int) y, (int) SnakeSprite.DIAMETER / 2, (int) SnakeSprite.DIAMETER / 2);
    }

    public double squareDistance(double x, double y) {
        return Geometry.squareDistance(x, y, this.x, this.y);
    }

    @Override
    public boolean touch(Sprite<Snake> sprite) {
        double snakeX = sprite.x() + SnakeSprite.DIAMETER / 2;
        double snakeY = sprite.y() + SnakeSprite.DIAMETER / 2;
        double ballX = x() + SnakeSprite.DIAMETER / 4;
        double ballY = y() + SnakeSprite.DIAMETER / 4;
        double snakeR = SnakeSprite.DIAMETER / 2;
        double ballR = SnakeSprite.DIAMETER / 4;
        return Geometry.circleIntersect(
                snakeX, snakeY, snakeR,
                ballX, ballY, ballR
        );
    }

    public void followed() {
        followers++;
    }

    public void unfollowed() {
        followers--;
    }
}
