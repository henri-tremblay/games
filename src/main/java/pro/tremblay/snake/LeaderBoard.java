package pro.tremblay.snake;

import pro.tremblay.framework.Sprite;

import java.awt.*;

class LeaderBoard extends Sprite<Snake> {
    private final SnakeSprite snake;

    public LeaderBoard(Snake game, SnakeSprite snake) {
        super(game);
        this.snake = snake;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        Font currentFont = g.getFont();
        Font newFont = currentFont.deriveFont(currentFont.getSize() * 1.4F);
        g.setFont(newFont);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawString(game.resource("length") + ": " + snake.length(), (int) x, (int) y);
        g.drawString(game.resource("enemies") + ": " + game.numberOfEnnemies(), (int) x, (int) y + 20);

        g.setFont(currentFont);
    }
}
