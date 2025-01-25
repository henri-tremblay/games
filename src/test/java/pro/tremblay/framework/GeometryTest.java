package pro.tremblay.framework;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GeometryTest {

    @Test
    public void circleIntersect() {
        assertThat(Geometry.circleIntersect(0, 0, 10, 0, 0, 5)).isTrue();
        assertThat(Geometry.circleIntersect(1, 5, 10, 2, 10, 5)).isTrue();
        assertThat(Geometry.circleIntersect(1, 5, 10, 2, 50, 5)).isFalse();
    }

    @Test
    public void squareDistance() {
        assertThat(Geometry.squareDistance(0, 0, 3, 4)).isEqualTo(25.0);
        assertThat(Geometry.squareDistance(5, 5, 5, 5)).isEqualTo(0.0);
        assertThat(Geometry.squareDistance(-3, -4, 3, 4)).isEqualTo(100.0);
    }

    @Test
    public void angleBetween() {
        assertThat(Geometry.angleBetween(0, 0, 1, 0)).isEqualTo(0.0); // Horizontal line to the right
        assertThat(Geometry.angleBetween(0, 0, 0, 1)).isEqualTo(Math.PI / 2); // Vertical line upwards
        assertThat(Geometry.angleBetween(0, 0, -1, 0)).isEqualTo(Math.PI); // Horizontal line to the left
        assertThat(Geometry.angleBetween(0, 0, 0, -1)).isEqualTo(-Math.PI / 2); // Vertical line downwards
        assertThat(Geometry.angleBetween(0, 0, 1, 1)).isEqualTo(Math.atan2(1, 1)); // 45 degrees
        assertThat(Geometry.angleBetween(0, 0, -1, -1)).isEqualTo(Math.atan2(-1, -1)); // -135 degrees
    }
}