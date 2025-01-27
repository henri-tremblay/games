package pro.tremblay.snake;

import pro.tremblay.framework.Sprite;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

class Score extends Sprite<Snake> {
    private final SnakeSprite snake;

    public Score(Snake game, SnakeSprite snake) {
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
        g.drawString("Longueur: " + snake.length(), (int) x, (int) y);
    }
}
