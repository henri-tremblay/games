package pro.tremblay.framework;

import java.awt.*;
import java.util.StringJoiner;

public abstract class Sprite<T extends Game> {
    protected final T game;
    protected double x, y;
    protected double vx, vy;

    protected Sprite(T game) {
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

    public Sprite<T> vx(double vx) {
        this.vx = vx;
        return this;
    }

    public Sprite<T> vy(double vy) {
        this.vy = vy;
        return this;
    }

    public Sprite<T> x(double x) {
        this.x = x;
        return this;
    }

    public Sprite<T> y(double y) {
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

    public Rectangle bounds() {
        return new Rectangle((int) x, (int) y, 0, 0);
    }

    public boolean touch(Sprite<T> sprite) {
        return false;
    }
}
