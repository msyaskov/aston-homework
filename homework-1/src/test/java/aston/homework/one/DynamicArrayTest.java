package aston.homework.one;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DynamicArrayTest {

    private DynamicArray<Integer> dynamicArray;

    @BeforeEach
    void setUp() {
        dynamicArray = new DynamicArray<>();
    }

    @Test
    void testAdd() {
        dynamicArray.add(123);
        dynamicArray.add(456);

        assertEquals(2, dynamicArray.size());
        assertEquals(123, dynamicArray.get(0));
        assertEquals(456, dynamicArray.get(1));
    }

    @Test
    void testAddAtIndex() {
        dynamicArray.add(0, 123);
        dynamicArray.add(1, 456);
        dynamicArray.add(1, 789);

        assertEquals(3, dynamicArray.size());
        assertEquals(123, dynamicArray.get(0));
        assertEquals(789, dynamicArray.get(1));
        assertEquals(456, dynamicArray.get(2));
    }

    @Test
    void testRemove() {
        dynamicArray.add(123);
        dynamicArray.add(456);
        dynamicArray.add(123);
        dynamicArray.add(789);

        boolean status = dynamicArray.remove(Integer.valueOf(123));

        assertTrue(status);
        assertEquals(3, dynamicArray.size());
        assertEquals(456, dynamicArray.get(0));
        assertEquals(123, dynamicArray.get(1));
        assertEquals(789, dynamicArray.get(2));
    }

    @Test
    void testRemoveAtIndex() {
        dynamicArray.add(123);
        dynamicArray.add(456);

        Integer removedElement = dynamicArray.remove(0);

        assertEquals(1, dynamicArray.size());
        assertEquals(123, removedElement);
        assertEquals(456, dynamicArray.get(0));
    }

    @Test
    void testClear() {
        dynamicArray.add(123);
        dynamicArray.add(456);
        assertEquals(2, dynamicArray.size());

        dynamicArray.clear();

        assertEquals(0, dynamicArray.size());
        assertTrue(dynamicArray.isEmpty());
    }

    @Test
    void testGet() {
        dynamicArray.add(123);
        dynamicArray.add(456);

        assertEquals(123, dynamicArray.get(0));
        assertEquals(456, dynamicArray.get(1));
    }

    @Test
    void testSet() {
        dynamicArray.add(123);
        dynamicArray.add(456);

        Integer replacedElement = dynamicArray.set(1, 789);

        assertEquals(2, dynamicArray.size());
        assertEquals(456, replacedElement);
        assertEquals(123, dynamicArray.get(0));
        assertEquals(789, dynamicArray.get(1));
    }

    @Test
    void testSize() {
        assertEquals(0, dynamicArray.size());

        dynamicArray.add(123);
        assertEquals(1, dynamicArray.size());

        dynamicArray.add(456);
        assertEquals(2, dynamicArray.size());
    }

    @Test
    void testToArray() {
        dynamicArray.add(123);
        dynamicArray.add(456);

        Object[] array = dynamicArray.toArray();

        assertEquals(2, array.length);
        assertArrayEquals(new Object[]{123, 456}, array);
    }

    @Test
    void testToArrayWithGenericType() {
        dynamicArray.add(123);
        dynamicArray.add(456);

        Integer[] array = dynamicArray.toArray(new Integer[0]);
        assertArrayEquals(new Integer[]{123, 456}, array);

        Integer[] intArray = new Integer[2];
        array = dynamicArray.toArray(intArray);
        assertSame(array, intArray);
        assertArrayEquals(new Integer[]{123, 456}, array);
    }
}
