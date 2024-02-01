package aston.homework.one;

import java.util.Comparator;
import java.util.List;

/**
 * Реализация интерфеса {@link ListSorter} для быстрой сортировки списков.
 *
 * <p>Данная быстрая сортировка использует разбиение по схеме Хоара.</p>
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

public class QuickSortListSorter<E> implements ListSorter<E> {

    private final Comparator<E> comparator;

    public QuickSortListSorter(Comparator<E> comparator) {
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

        internalSort(list, 0, list.size() - 1);
    }

    private void internalSort(List<E> list, int left, int right) {
        if (left < right) {
            int p = partition(list, left, right);
            internalSort(list, left, p);
            internalSort(list, p + 1, right);
        }
    }

    private int partition(List<E> list, int left, int right) {
        E pivot = list.get((left + right) / 2);
        while (true) {
            while(comparator.compare(list.get(left), pivot) < 0) {
                left++;
            }

            while(comparator.compare(list.get(right), pivot) > 0) {
                right--;
            }

            if (right <= left) {
                return right;
            }

            swap(list, left++, right--);
        }
    }

    private void swap(List<E> list, int i, int j) {
        E buf = list.get(i);
        list.set(i, list.get(j));
        list.set(j, buf);
    }
}
