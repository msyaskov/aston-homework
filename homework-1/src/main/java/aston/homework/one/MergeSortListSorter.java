package aston.homework.one;

import java.util.Comparator;
import java.util.List;

/**
 * Реализация интерфеса {@link ListSorter} для сортировки списков слиянием.
 *
 * <p>В силу частого обращение к списку по индексам, в данной реализации,
 * не рекомендуется для сортировки списков основанных на структурах данных отличных от массива,
 * например для связанных списков.</p>
 *
 * @param <E> тип элементов в списке, подлежащем сортировке
 *
 * @author Максим Яськов
 * @see List
 */

public class MergeSortListSorter<E> implements ListSorter<E> {

    private final Comparator<E> comparator;

    public MergeSortListSorter(Comparator<E> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("A comparator must not be null");
        }

        this.comparator = comparator;
    }

    @Override
    public void sort(List<E> list) {
        if (list == null) {
            throw new IllegalArgumentException("A list must not be null");
        }

        if (list.size() < 2) {
            return;
        }

        DynamicArray<E> buffer = new DynamicArray<>(list.size());
        internalSort(list, 0, list.size() - 1, buffer);
    }

    private void internalSort(List<E> list, int left, int right, List<E> buffer) {
        if (left < right) {
            int middle = left + ((right - left) >> 1);
            internalSort(list, left, middle, buffer);
            internalSort(list, middle + 1, right, buffer);
            merge(list, left, middle, right, buffer);
        }
    }

    private void merge(List<E> list, int left, int middle, int right, List<E> buffer) {
        if (right + 1 - left >= 0) {
            copy(list, left, buffer, left, right + 1 - left);
        }

        int i = middle + 1;
        int j = left;

        while (left <= middle && i <= right) {
            if (comparator.compare(buffer.get(left), buffer.get(i)) < 0) {
                list.set(j++, buffer.get(left++));
            } else {
                list.set(j++, buffer.get(i++));
            }
        }

        while (left <= middle) {
            list.set(j++, buffer.get(left++));
        }
    }

    private void copy(List<E> src, int srcPos, List<E> dst, int dstPos, int length) {
        for (int i = 0; i < length; i++) {
            dst.set(dstPos + i, src.get(srcPos + i));
        }
    }
}
