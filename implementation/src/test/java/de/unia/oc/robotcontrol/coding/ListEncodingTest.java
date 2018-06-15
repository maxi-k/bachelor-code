package de.unia.oc.robotcontrol.coding;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListEncodingTest {

    @Test
    void encodesList() {
        int numElements = (int) (Math.random() * 10);
        List<Integer> ints = genInts(numElements);
        ListEncoding<Integer> encoding = new ListEncoding<>(new IntegerEncoding(), ints.size());
        List<Integer> result = encoding.decode(encoding.encode(ints));
        assertEquals(ints, result);
    }

    private List<Integer> genInts(int amount) {
        List<Integer> ints = new ArrayList<>(amount) ;
        for (int i = 0; i < ints.size(); ++i) {
            ints.set(i, (int) (Math.random() * 256));
        }
        return ints;
    }
}