package de.unia.oc.robotcontrol.coding;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class ListEncodingTest {

    @Test
    void encodesList() {
        int numElements = (int) (Math.random() * 10);
        List<Integer> ints = genInts(numElements);
        ListEncoding<Integer> encoding = new ListEncoding<>(new IntegerEncoding(), ints.size());
        List<Integer> result = encoding.decode(encoding.encode(ints));
        assertArrayEquals(ints.toArray(), result.toArray());
    }

    private List<Integer> genInts(int amount) {
        List<Integer> ints = new ArrayList<>(amount) ;
        for (int i = 0; i < amount; ++i) {
            ints.add((int) (Math.random() * 4096));
        }
        return ints;
    }
}