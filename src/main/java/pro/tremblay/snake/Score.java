package pro.tremblay.snake;

import pro.tremblay.framework.Sprite;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

class Score extends Sprite<Snake> {
    private final SnakeSprite player;
    private final SnakeSprite nemesis;

    public Score(Snake game, SnakeSprite player, SnakeSprite nemesis) {
        super(game);
        this.player = player;
        this.nemesis = nemesis;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        Font currentFont = g.getFont();
        Font newFont = currentFont.deriveFont(currentFont.getSize() * 1.4F);
        g.setFont(newFont);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawString(game.resource("player.length") + ": " + player.length(), (int) x, (int) y);
        g.drawString(game.resource("nemesis.length") + ": " + nemesis.length(), (int) x, (int) y + 20);
        g.drawString(game.resource("enemies") + ": " + game.numberOfEnnemies(), (int) x, (int) y + 40);

        g.setFont(currentFont);
    }
}
