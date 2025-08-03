package pro.tremblay.snake;

import java.awt.*;

public class NemesisSnake extends SnakeSprite {

    private static final double INITIAL_SPEED = 15;

    public NemesisSnake(Snake game) {
        super(game, game.resource("nemesis"));
    }

    @Override
    protected Color color() {
        return Color.PINK;
    }

    @Override
    protected double speed() {
        return INITIAL_SPEED;
    }

    protected void drawMouth(Graphics g) {
        g.drawArc((int) (x + 10), (int) (y + 30), 30, 20, 0, 180);
    }

}
