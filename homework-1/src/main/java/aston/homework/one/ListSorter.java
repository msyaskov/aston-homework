package aston.homework.one;

import java.util.List;

/**
 * Функция сортировки для реализации интерфейса {@link List}.
 *
 * @param <E> тип элементов в списке, подлежащем сортировке
 *
 * @author Максим Яськов
 * @see List
 */

@FunctionalInterface
public interface ListSorter<E> {

    /**
     * Сортирует указанный список.
     *
     * @param list список, который требуется отсортировать
     */
    void sort(List<E> list);

}
