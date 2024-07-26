package pro.tremblay.framework;

import java.util.ArrayDeque;
import java.util.List;
import java.util.stream.Stream;

public class CircularQueue<T> {
    private final ArrayDeque<T> array;
    private final int capacity;

    public CircularQueue(int capacity) {
        this.array = new ArrayDeque<>(capacity);
        this.capacity = capacity;
    }

    public void add(T value) {
        array.addFirst(value);
        if (array.size() > capacity) {
            array.removeLast();
        }
    }

    public T getFirst() {
        return array.getFirst();
    }

    public Stream<T> getFirsts(int length) {
        return array.stream().limit(length);
    }

    public int size() {
        return array.size();
    }

    public void clear() {
        array.clear();
    }
}
