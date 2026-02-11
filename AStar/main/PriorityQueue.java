package AStar.main;

public interface PriorityQueue<E> {
    void add(E value);
    void clear();
    boolean isEmpty();
    E peek();
    E remove();
    int size();
}
