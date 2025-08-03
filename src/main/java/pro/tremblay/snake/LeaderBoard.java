package pro.tremblay.snake;

import pro.tremblay.framework.Sprite;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Comparator;
import java.util.List;

class LeaderBoard extends Sprite<Snake> {
    private final List<EnemySnake> enemies;

    public LeaderBoard(Snake game, List<EnemySnake> enemies) {
        super(game);
        this.enemies = enemies;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        Font currentFont = g.getFont();
        Font newFont = currentFont.deriveFont(currentFont.getSize() * 1.4F);
        g.setFont(newFont);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawString(game.resource("longest.enemies"), (int) x, (int) y);
        g.drawLine((int) x, (int) y + 10, (int) x + 200, (int) y + 10);

        List<EnemySnake> best = enemies.stream()
                .sorted(Comparator.comparingInt(EnemySnake::length).reversed())
                .limit(15)
                .toList();
        for (int i = 0; i < best.size(); i++) {
            EnemySnake enemy = best.get(i);
            g.drawString((i + 1) + ": " + enemy.name() + " (" + enemy.length() + ")", (int) x, (int) y + 30 + i * 20);
        }


        g.setFont(currentFont);
    }
}
