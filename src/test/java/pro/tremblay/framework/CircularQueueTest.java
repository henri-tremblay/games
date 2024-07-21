package pro.tremblay.framework;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CircularQueueTest {

    CircularQueue<Integer> queue = new CircularQueue<>(3);

    @Test
    void addOnce() {
        queue.add(1);
        assertThat(queue.getFirst()).isEqualTo(1);
    }

    @Test
    void addTwice_getLatest() {
        queue.add(1);
        queue.add(2);
        assertThat(queue.getFirst()).isEqualTo(2);
    }

    @Test
    void size_cantBeBiggerThan3() {
        queue.add(1);
        queue.add(2);
        assertThat(queue.size()).isEqualTo(2);
        queue.add(3);
        assertThat(queue.size()).isEqualTo(3);
        queue.add(4);
        assertThat(queue.size()).isEqualTo(3);
        assertThat(queue.getFirst()).isEqualTo(4);
    }

    @Test
    void getFirsts() {
        queue.add(1);
        queue.add(2);
        queue.add(3);
        assertThat(queue.getFirsts(2)).containsExactly(3, 2);
    }
}