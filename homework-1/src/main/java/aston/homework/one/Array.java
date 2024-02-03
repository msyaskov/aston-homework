package aston.homework.one;

import java.util.Comparator;

public interface Array<E> extends Iterable<E> {

    default boolean contains(E e) {
        return indexOf(e) != -1;
    }

    int indexOf(E e);

    E get(int index);

    int lastIndexOf(E e);

    E set(int index, E element);

    int size();

    void sort(Comparator<E> comparator);

    Object[] toArray();

    <T> T[] toArray(T[] a);

}
