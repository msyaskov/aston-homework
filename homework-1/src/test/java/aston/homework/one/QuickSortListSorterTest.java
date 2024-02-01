package aston.homework.one;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class QuickSortListSorterTest {

    private QuickSortListSorter<Integer> sorter;

    @BeforeEach
    public void setUp() {
        sorter = new QuickSortListSorter<>(Integer::compareTo);
    }

    @Test
    public void test() {
        testCase(dynamicArrayOf(6,-4,5,1,-7,23,-19,0));
        for (int i = 0; i < 1000; i += 3) {
            testCase(dynamicArrayOf(randomInts(1000, i)));
        }
    }

    private void testCase(DynamicArray<Integer> dynamicArray) {
        sorter.sort(dynamicArray);

        for (int i = 0; i < dynamicArray.size() - 1; i++) {
            assertTrue(dynamicArray.get(i) <= dynamicArray.get(i + 1));
        }
    }

    private <E> DynamicArray<E> dynamicArrayOf(E... elements) {
        DynamicArray<E> array = new DynamicArray<>(elements.length);
        array.addAll(Arrays.asList(elements));

        return array;
    }

    private Integer[] randomInts(int bound, int length) {
        Random random = new Random(1234567890);
        return IntStream.generate(() -> random.nextInt(bound)).limit(length).boxed().toArray(Integer[]::new);
    }
}