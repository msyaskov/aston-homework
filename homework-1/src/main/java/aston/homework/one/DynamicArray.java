package aston.homework.one;

import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Реализация интерфейса {@link List} с изменяемым размером массива.
 * Реализованы все необязательные операции со списком и разрешено использование любых элементов, в том числе {@code null}.
 *
 * <p>Помимо реализации интерфейса {@link List}, этот класс предоставляет методы для управления размером массива,
 * используемого для хранения списка. Емкость - размер этого массива, всегда не меньше размера списка и увеличивается по мере добавления элементов.
 * Политика увеличения емкости имеет особенности, однако, в среднем, увеличение в полтора раза.
 * Для оптимизации добавления большого количества элементов, реализация предоставлется метод {@link DynamicArray#ensureCapacity(int)}
 * позволяющий увеличить емность но необходимого минимума перед добавлением. Вместе с этим реализация предоставляет метод {@link DynamicArray#trimToSize()}
 * позволяющий уменьшить емкость до текущмего размера списка.</p>
 *
 * <p>Поскольку реализация не синхронизирована, для обеспечения безопасности стуктурных изменений в многопоточной среде,
 * экземпляр DynamicArray необходимо синхронизовать извне или обернуть список с помощью {@link java.util.Collections#synchronizedList(List)}.</p>
 *
 * <p>Реализация итераторов, возвращаемых методами iterator и listIterator, наследуется от базового класса {@link AbstractList}.</p>
 *
 * @param <E> тип элементов в этом списке
 *
 * @author Максим Яськов
 * @see     AbstractCollection
 * @see     AbstractList
 * @see     Collection
 * @see     List
 */

public class DynamicArray<E> extends AbstractList<E> implements List<E> {

    /**
     * Безопасная максимальная длина массива, используемая при увеличении массива.
     * Данное значение выбрано консервативно, чтобы оно было меньше любого возможного предела реализации JVM.
     * Смотрите jdk.internal.util.ArraysSupport#SOFT_MAX_ARRAY_LENGTH.
     */
    public static final int SOFT_MAX_ARRAY_LENGTH = Integer.MAX_VALUE - 8;

    /**
     * Емкость, используемая при первом добавлении элемента.
     */
    private static final int DEFAULT_CAPACITY = 10;

    /**
     * Общий экземпляр пустого массива, используемый для пустых экземпляров.
     */
    private static final Object[] EMPTY_ARRAY = {};

    /**
     * Массив для хранения элементов списка.
     */
    private Object[] elements;

    /**
     * Количество элементов в списке.
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
     * Создает список, содержащий элементы указанной коллекции, в том порядке, в котором они возвращаются итератором коллекции.
     *
     * @param collection коллекция, элементы которой будут помещены в этот список
     * @throws NullPointerException если указанная коллекция равна null
     * @see Collection#toArray()
     */
    public DynamicArray(Collection<E> collection) {
        Object[] collectionArray = collection.toArray();
        if ((size = collectionArray.length) != 0) {
            if (collection instanceof DynamicArray) {
                elements = collectionArray;
            } else {
                elements = Arrays.copyOf(collectionArray, size, Object[].class);
            }
        } else {
            elements = EMPTY_ARRAY;
        }
    }

    /**
     * Вставляет указанный элемент в списков по указанному индексу.
     * Все текущие элементы, индекс которых не меньше указанного, сдвгаются вправо (их индекс увеличивается на единицу).
     *
     * @param index индекс, по которому будет вставлен элемент
     * @param element элемент для вставки
     * @throws IndexOutOfBoundsException если указанный индекс выходит за диапазон для вставки (0 <= index && index <= size)
     */
    @Override
    public void add(int index, E element) {
        if (index < 0 || size < index) {
            throw new IndexOutOfBoundsException("Index: %d, Size: %d".formatted(index, size));
        }

        modCount++;

        if (size == elements.length) {
            elements = grow(size + 1);
        }

        if (index < size) {
            System.arraycopy(elements, index, elements, index + 1, size - index);
        }

        elements[index] = element;
        size += 1;
    }

    /**
     * Добавляет все элементы указанной коллекции в конец этого списка, сохраняя порядок, в котором элементы возвращаются итератором указанной коллекции.
     *
     * @param collection коллекция содержащая элементы для добавления в этот список
     * @return true если вставился хотя бы один элемент, в противном случае false
     * @throws NullPointerException если указанная коллекция равна null
     */
    @Override
    public boolean addAll(Collection<? extends E> collection) {
        return addAll(size, collection);
    }

    /**
     * Вставляет все элементы указанной коллекции в этот список в указанную позицию, сохраняя порядок, в котором элементы возвращаются итератором указанной коллекции.
     * Смещает элемент, находящийся в данный момент в этой позиции, и любые последующие элементы вправо (увеличивает их индексы).
     *
     * @param index индекс, для вставки первого элемента указанной коллекции
     * @param collection коллекция содержащая элементы для вставки в этот список
     * @return true если вставился хотя бы один элемент, в противном случае false
     * @throws IndexOutOfBoundsException если указанный индекс выходит за диапазон для вставки (0 <= index && index <= size)
     */
    @Override
    public boolean addAll(int index, Collection<? extends E> collection) {
        if (index < 0 || size < index) {
            throw new IndexOutOfBoundsException("Index: %d, Size: %d".formatted(index, size));
        }

        Object[] collectionArray = collection.toArray();
        if (collectionArray.length == 0) {
            return false;
        }

        modCount++;

        if (elements.length - size < collectionArray.length)
            elements = grow(size + collectionArray.length);

        int offset = size - index;
        if (0 < offset) {
            System.arraycopy(elements, index, elements, index + collectionArray.length, offset);
        }

        System.arraycopy(collectionArray, 0, elements, index, collectionArray.length);
        size = size + collectionArray.length;

        return true;
    }

    /**
     * Удаляет все элементы из этого списка, при этом не изменяя емкость.
     */
    @Override
    public void clear() {
        modCount++;

        Arrays.fill(elements, 0, size, null);
        size = 0;
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
     * @param minNewCapacity требуемая минимальная емкость
     * @return новый массив с увеличенной емкостью, с сохраненными элементами
     * @throws IllegalArgumentException если minNewCapacity меньше нуля или больше, чем {@link DynamicArray#SOFT_MAX_ARRAY_LENGTH}
     * @throws OutOfMemoryError если не удается обеспечить минимальную емкость
     */
    private Object[] grow(int minNewCapacity) {
        if (minNewCapacity < 0) {
            throw new IllegalArgumentException("Capacity must not be negative, but: " + minNewCapacity);
        }

        if (SOFT_MAX_ARRAY_LENGTH < minNewCapacity) {
            throw new IllegalArgumentException("The minNewCapacity is greater than maximum array capacity");
        }

        int currentCapacity = elements.length;
        if (currentCapacity == 0) {
            return elements = new Object[Math.max(minNewCapacity, DEFAULT_CAPACITY)];
        }

        int minGrowth = minNewCapacity - currentCapacity;
        int preferredGrowth = currentCapacity >> 1;
        int newCapacity = currentCapacity + Math.max(minGrowth, preferredGrowth);
        if (newCapacity < 0) { // Переполнение возникает из-за слишком большого приращения preferredGrowth.
            newCapacity = SOFT_MAX_ARRAY_LENGTH; // В таком случае новая емкость равна максимально возможной.
        } else if (SOFT_MAX_ARRAY_LENGTH < newCapacity) {
            newCapacity = SOFT_MAX_ARRAY_LENGTH;
        }

        if (newCapacity < minNewCapacity) { // Не удается обеспечить минимальную емкость
            throw new OutOfMemoryError("Cannot meet minimum new capacity");
        }

        return elements = Arrays.copyOf(elements, newCapacity);
    }

    /**
     * При необходимости увеличивает ёмкость этого списка, гарантируя указанную минимальную емкость.
     *
     * @param minNewCapacity требуемая минимальная емкость
     */
    public void ensureCapacity(int minNewCapacity) {
        if (elements.length < minNewCapacity) {
            modCount++;
            grow(minNewCapacity);
        }
    }

    /**
     * Удаляет элемент по указанному индексу в этом списке.
     * Все текущие элементы, индекс которых не меньше указанного, сдвигаются влево (их индекс уменьшается на единицу).
     *
     * @param index индекс удаляемого элемента
     * @return удаленный элемент
     * @throws IndexOutOfBoundsException если указанный индекс выходит за диапазон (0 <= index && index < size)
     */
    @Override
    public E remove(int index) {
        if (index < 0 || size <= index) {
            throw new IndexOutOfBoundsException("Index: %d, Size: %d".formatted(index, size));
        }

        E element = get(index);

        modCount++;

        final int newSize = size - 1;
        if (index < newSize) {
            System.arraycopy(elements, index + 1, elements, index, newSize - index);
        }

        size = newSize;
        elements[size] = null; // убираю жесткую ссылку

        return element;
    }

    /**
     * Заменяет в этом списке текущий элемент по указанному индексу указанным элементом.
     *
     * @param index индекс заменяемого элемента
     * @param element элемент, который будет установлен в этот список по указанному индексу
     * @return замененный элемент
     * @throws IndexOutOfBoundsException если указанный индекс выходит за диапазон (0 <= index && index < size)
     */
    @Override
    public E set(int index, E element) {
        E currentElement = get(index);
        elements[index] = element;
        return currentElement;
    }

    /**
     * Возвращает количество элементов в списке.
     *
     * @return количество элементов в списке
     */
    @Override
    public int size() {
        return size;
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

    /**
     * Уменьшает емкость до текущего размера списка.
     */
    public void trimToSize() {
        if (size < elements.length) {
            modCount++;
            elements = size == 0 ? EMPTY_ARRAY : Arrays.copyOf(elements, size);
        }
    }
}
