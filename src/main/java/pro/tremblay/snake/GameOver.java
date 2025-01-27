package pro.tremblay.snake;

import pro.tremblay.framework.Sprite;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

class GameOver extends Sprite<Snake> {

    public GameOver(Snake game) {
        super(game);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.drawString("Appuyez sur Entrée pour rejouer", 280, 250);

        Font currentFont = g.getFont();
        Font newFont = currentFont.deriveFont(currentFont.getSize() * 5F);
        g.setFont(newFont);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawString("Fin de la partie", 280, 200);
    }
}
