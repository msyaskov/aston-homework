package aston.homework.one;

import java.util.*;

/**
 * Реализация интерфейса {@link Array} с изменяемым размером массива.
 * Разрешено использование любых элементов, в том числе {@code null}.
 *
 * <p>Помимо реализации интерфейса {@link Array}, этот класс предоставляет методы для управления размером внутреннего массива,
 * используемого для хранения элементов. Емкость - размер этого массива, всегда не меньше размера массива и увеличивается по мере добавления элементов.
 * Политика увеличения емкости имеет особенности, однако, в среднем, увеличение в полтора раза.
 * Для оптимизации добавления большого количества элементов, реализация предоставляет метод {@link DynamicArray#ensureCapacity(int)}
 * позволяющий увеличить емкость до требуемой величины перед добавлением. Вместе с этим реализация предоставляет метод {@link DynamicArray#trimToSize()}
 * позволяющий уменьшить емкость до текущего размера массива.</p>
 *
 * <p>Поскольку реализация не синхронизирована, для обеспечения безопасности структурных изменений в многопоточной среде,
 * экземпляр синхронизовать извне.</p>
 *
 * @param <E> тип элементов в этом массиве.
 *
 * @author Максим Яськов
 * @see Array
 */

public class DynamicArray<E> implements Array<E> {

    /**
     * Безопасная максимальная длина массива, используемая при увеличении массива.
     * Данное значение выбрано консервативно, чтобы оно было меньше любого возможного предела реализации JVM.
     * Смотрите jdk.internal.util.ArraysSupport#SOFT_MAX_ARRAY_LENGTH.
     */
    public static final int MAX_CAPACITY = Integer.MAX_VALUE - 8;

    /**
     * Общий экземпляр пустого массива, используемый в качестве внутреннего массива для пустых экземпляров.
     */
    private static final Object[] EMPTY_ARRAY = {};

    /**
     * Минимальная емкость, используемая при первом добавлении элемента, при условии нулевой емкости перед увеличением.
     */
    private static final int MIN_CAPACITY = 10;

    /**
     * Счетчик модификаций массива. спользуется итератором.
     */
    protected int modificationCounter = 0;

    /**
     * Внутренний массив для хранения элементов.
     */
    private Object[] elements;

    /**
     * Количество элементов в массиве.
     */
    private int size;

    /**
     * Создает пустой список с нулевой изначальной емкостью.
     */
    public DynamicArray() {
        this(0);
    }

    /**
     * Создает пустой список с указанной изначальной емкость.
     *
     * @param  initialCapacity изначальная емкость списка
     * @throws IllegalArgumentException если указанная емкость имеет отрицательное значение
     */
    public DynamicArray(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("InitialCapacity must not be negative, but: " + initialCapacity);
        }

        if (initialCapacity == 0) {
            this.elements = EMPTY_ARRAY;
        } else {
            this.elements = new Object[initialCapacity];
        }
    }

    /**
     * Вставляет указанный элемент в конец массива, то есть по индексу равному размеру массива до вставки.
     *
     * @param element элемент для вставки
     */
    public boolean add(E element) {
        return add(size, element);
    }

    /**
     * Вставляет указанный элемент в массив по указанному индексу.
     * Все текущие элементы, индекс которых не меньше указанного, сдвигаются вправо (их индекс увеличивается на единицу).
     *
     * @param index индекс, по которому будет вставлен элемент
     * @param element элемент для вставки
     * @throws IndexOutOfBoundsException если указанный индекс выходит за диапазон для вставки (0 <= index && index <= size)
     */
    public boolean add(int index, E element) {
        checkIndexForAdd(index);
        modificationCounter++;

        if (size == elements.length) {
            elements = grow(size + 1);
        }

        if (index < size) {
            System.arraycopy(elements, index, elements, index + 1, size - index);
        }

        elements[index] = element;
        size += 1;

        return true;
    }

    /**
     * Проверяет допустимость использования индекса для вставки в массив.
     *
     * @param index индекс для проверки
     * @throws IndexOutOfBoundsException если указанный индекс выходит за диапазон для вставки (0 <= index && index <= size)
     */
    private void checkIndexForAdd(int index) {
        if (index < 0 || size < index) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    /**
     * Проверяет допустимость использования индекса для операции с массивом.
     *
     * @param index индекс для проверки
     * @throws IndexOutOfBoundsException если указанный индекс выходит за диапазон для вставки (0 <= index && index < size)
     */
    private void checkIndex(int index) {
        if (index < 0 || size <= index) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    /**
     * Удаляет все элементы из этого списка, не изменяя при этом емкость.
     */
    public void clear() {
        modificationCounter++;

        Arrays.fill(elements, null);
        size = 0;
    }

    /**
     * При необходимости увеличивает ёмкость этого списка, гарантируя указанную минимальную емкость.
     *
     * @param requiredCapacity требуемая минимальная емкость
     */
    public void ensureCapacity(int requiredCapacity) {
        if (elements.length < requiredCapacity) {
            modificationCounter++;
            elements = grow(requiredCapacity);
        }
    }

    /**
     * Возвращает элемент по указанному индексу.
     *
     * @param index индекс возвращаемого элемента
     * @return элемент по указанному индексу в этом списке
     * @throws IndexOutOfBoundsException если указанный индекс выходит за диапазон (0 <= index && index < size)
     */
    @Override
    @SuppressWarnings("unchecked")
    public E get(int index) {
        return (E) elements[index];
    }

    /**
     * Увеличивает емкость, гарантируя как минимум указанную минимальную емкость.
     *
     * @param requiredCapacity требуемая минимальная емкость
     * @return новый массив с увеличенной емкостью, с сохраненными элементами
     * @throws IllegalArgumentException если requiredCapacity меньше нуля или больше, чем {@link DynamicArray#MAX_CAPACITY}
     * @throws OutOfMemoryError если не удается обеспечить минимальную емкость
     */
    private Object[] grow(int requiredCapacity) {
        if (requiredCapacity < 0) {
            throw new IllegalArgumentException("Capacity must not be negative, but: " + requiredCapacity);
        }

        if (MAX_CAPACITY < requiredCapacity) {
            throw new IllegalArgumentException("The requiredCapacity is greater than maximum array capacity");
        }

        int currentCapacity = elements.length;
        if (currentCapacity == 0) {
            return elements = new Object[Math.max(requiredCapacity, MIN_CAPACITY)];
        }

        int minGrowth = requiredCapacity - currentCapacity;
        int preferredGrowth = currentCapacity >> 1;
        int newCapacity = currentCapacity + Math.max(minGrowth, preferredGrowth);
        if (newCapacity < 0) { // Переполнение возникает из-за слишком большого приращения preferredGrowth.
            newCapacity = MAX_CAPACITY; // В таком случае новая емкость равна максимально возможной.
        } else if (MAX_CAPACITY < newCapacity) {
            newCapacity = MAX_CAPACITY;
        }

        if (newCapacity < requiredCapacity) { // Не удается обеспечить минимальную емкость
            throw new OutOfMemoryError("Cannot meet minimum new capacity");
        }

        return elements = Arrays.copyOf(elements, newCapacity);
    }

    /**
     * Возвращает индекс первого вхождения указанного элемента.
     *
     * @param element элемент, индекс первого вхождения которого будет возвращен
     * @return индекс первого вхождения указанного элемента или -1 если элемент не найден
     */
    @Override
    public int indexOf(E element) {
        if (element == null) {
            for (int i = 0; i < size; i++) {
                if (elements[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (element.equals(elements[i])) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * Проверяет массив на пустоту.
     *
     * @return true если массив пустой, в противном случае false
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Возвращает новый итератор.
     * тератор является fail-fast и не поддерживает операцию remove.
     *
     * @return новый итератор
     */
    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    /**
     * Возвращает индекс последнего вхождения указанного элемента.
     *
     * @param element элемент, индекс последнего вхождения которого будет возвращен
     * @return индекс последнего вхождения указанного элемента или -1 если элемент не найден
     */
    @Override
    public int lastIndexOf(E element) {
        if (element == null) {
            for (int i = size - 1; i >= 0; i--) {
                if (elements[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = size - 1; i >= 0; i--) {
                if (element.equals(elements[i])) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * Удаляет элемент по указанному индексу в этом массиве.
     * Все текущие элементы, индекс которых не меньше указанного, сдвигаются влево (их индекс уменьшается на единицу).
     *
     * @param index индекс удаляемого элемента
     * @return удаленный элемент
     * @throws IndexOutOfBoundsException если указанный индекс выходит за диапазон (0 <= index && index < size)
     */
    public E remove(int index) {
        checkIndex(index);
        modificationCounter++;

        E element = get(index);

        final int newSize = size - 1;
        if (index < newSize) {
            System.arraycopy(elements, index + 1, elements, index, newSize - index);
        }

        size = newSize;
        elements[size] = null; // убираю жесткую ссылку

        return element;
    }

    /**
     * Удаляет первое вхождение указанного элемента в этом массиве.
     *
     * @param element удаляемый элемент
     * @return true если массив изменился, в противном случае false.
     * @throws IndexOutOfBoundsException если указанный индекс выходит за диапазон (0 <= index && index < size)
     */
    public boolean remove(E element) {
        int index = indexOf(element);
        if (index < 0) {
            return false;
        }

        remove(index);
        return true;
    }

    /**
     * Заменяет в этом массиве текущий элемент по указанному индексу указанным элементом.
     *
     * @param index индекс заменяемого элемента
     * @param element элемент, который будет установлен в этот массив по указанному индексу
     * @return замененный элемент
     * @throws IndexOutOfBoundsException если указанный индекс выходит за диапазон (0 <= index && index < size)
     */
    @Override
    public E set(int index, E element) {
        checkIndex(index);
        modificationCounter++;

        E old = get(index);
        elements[index] = element;

        return old;
    }

    /**
     * Возвращает количество элементов в массиве.
     *
     * @return количество элементов в массиве
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Сортирует этот массив быстрой сортировкой.
     *
     * @param comparator компаратор для сравнения элементов.
     */
    public void sort(Comparator<E> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("A comparator must not be null");
        }

        if (size < 2) {
            return;
        }

        internalSort(comparator, 0, size - 1);
    }

    private void internalSort(Comparator<E> comparator, int left, int right) {
        if (left < right) {
            int[] partition = partition(comparator, left, right);
            internalSort(comparator, left, partition[0] - 1);
            internalSort(comparator, partition[1] + 1, right);
        }
    }

    // "Толстое" разбиение, т.е. на три группы: меньше опорного, равные опорному, больше опорного.
    // Возвращает массив с двумя индексами:
    //      left - такой что все элементы с индексами меньше, меньше чем опорный.
    //      right - такой что все элементы с индексами больше, большем чем опорный.
    private int[] partition(Comparator<E> comparator, int left, int right) {
        E pivot = get((left + right) >> 1);

        for (int i = left; i <= right; ) {
            int comparison = comparator.compare(pivot, get(i));
            if (comparison > 0) {
                swap(left, i);
                left++;
                i++;
            } else if (comparison < 0) {
                swap(right, i);
                right--;
            } else {
                i++;
            }
        }

        return new int[]{left, right};
    }

    private void swap(int i, int j) {
        Object buffer = elements[i];
        elements[i] = elements[j];
        elements[j] = buffer;
    }

    /**
     * Возвращает новый массив, содержащий все элементы этого списка в правильной последовательности.
     *
     * @return новый массив, содержащий все элементы этого списка в правильной последовательности
     */
    @Override
    public Object[] toArray() {
        return Arrays.copyOf(elements, size);
    }

    /**
     * Возвращает массив, содержащий все элементы этого списка в правильной последовательности.
     * Тип возвращаемого массива соответствует указанному массиву.
     * Если список помещается в указанный массив, он копируется туда, в противном случае выделяется новый массив.
     *
     * @param a массив, в котором должны храниться элементы списка, если он достаточно велик, в противном случае выделяется новый массив того же типа.
     * @return массив, содержащий все элементы этого списка в правильной последовательности
     * @throws ArrayStoreException если тип указанного массива не является супертипом типа каждого элемента в этом списке
     * @throws NullPointerException если указанный массив имеет значение NULL
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            return (T[]) Arrays.copyOf(elements, size, a.getClass());
        }

        System.arraycopy(elements, 0, a, 0, size);
        return a;
    }

    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }

        Iterator<E> it = iterator();
        StringBuilder sb = new StringBuilder("[");

        while (true) {
            E element = it.next();
            sb.append(element == this ? "(this)" : element); // исключаем рекурсию по этому массиву

            if (it.hasNext()) {
                sb.append(',').append(' ');
            } else {
                return sb.append(']').toString();
            }
        }
    }

    /**
     * Уменьшает емкость до текущего размера массива.
     */
    public void trimToSize() {
        if (size < elements.length) {
            modificationCounter++;
            elements = size == 0 ? EMPTY_ARRAY : Arrays.copyOf(elements, size);
        }
    }

    private class Itr implements Iterator<E> {

        int cursor = 0;

        int expectedModificationCount = modificationCounter;

        public boolean hasNext() {
            return cursor < size;
        }

        public E next() {
            checkForModification();

            try {
                return get(cursor++);
            } catch (IndexOutOfBoundsException e) {
                checkForModification();
                throw new NoSuchElementException();
            }
        }

        private void checkForModification() {
            if (expectedModificationCount != modificationCounter) {
                throw new ConcurrentModificationException();
            }
        }
    }
}
