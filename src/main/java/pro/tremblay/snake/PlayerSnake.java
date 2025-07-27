package pro.tremblay.snake;

import java.awt.Color;
import java.awt.Graphics;

public class PlayerSnake extends SnakeSprite {

    private static final double INITIAL_SPEED = 15;
    private static final double BOOST_SPEED = 20;

    private boolean boosted = false;

    public PlayerSnake(Snake game) {
        super(game);
    }

    public void setInitialSpeed() {
        vx = 0;
        vy = 0;
        boosted = false;
    }

    @Override
    protected Color color() {
        return Color.YELLOW;
    }

    @Override
    protected double speed() {
        return boosted && length > 0 ? BOOST_SPEED : INITIAL_SPEED;
    }

    protected void drawMouth(Graphics g) {
        g.drawArc((int) (x + 10), (int) (y + 20), 30, 20, 180, 180);
    }

    public boolean boosted() {
        return boosted;
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

}
