package pro.tremblay.framework;

public class Geometry {

    public static boolean circleIntersect(double x1, double y1, double r1, double x2, double y2, double r2) {
        double distance = Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
        return distance <= r1 + r2;
    }

}
