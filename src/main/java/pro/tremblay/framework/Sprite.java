package pro.tremblay.framework;

import java.awt.Graphics;
import java.util.StringJoiner;

public abstract class Sprite {
    protected final Game game;
    protected double x, y;
    protected double vx, vy;

    protected Sprite(Game game) {
        this.game = game;
    }

    public void position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void speed(double vx, double vy) {
        this.vx = vx;
        this.vy = vy;
    }

    public void move() {
        x += vx;
        y += vy;
    }

    public double vx() {
        return vx;
    }

    public double vy() {
        return vy;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public Sprite vx(double vx) {
        this.vx = vx;
        return this;
    }

    public Sprite vy(double vy) {
        this.vy = vy;
        return this;
    }

    public Sprite x(double x) {
        this.x = x;
        return this;
    }

    public Sprite y(double y) {
        this.y = y;
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Sprite.class.getSimpleName() + "[", "]")
                .add("x=" + x)
                .add("y=" + y)
                .add("vx=" + vx)
                .add("vy=" + vy)
                .toString();
    }

    public abstract void draw(Graphics g);
}
