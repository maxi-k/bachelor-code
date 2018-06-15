package de.unia.oc.robotcontrol.coding;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class IntegerEncodingTest {

    @Test
    void decodesNative() {
        byte[] input = {3, 0, 0, 0};
        IntegerEncoding encoding = new IntegerEncoding(CodingContext.NATIVE);
        int result = encoding.decode(input);
        assertEquals(3, result);
    }

    @Test
    void encodesNative() {
        IntegerEncoding encoding = new IntegerEncoding(CodingContext.NATIVE);
        byte[] result = encoding.encode(3);
        byte[] expected = {3, 0, 0, 0};
        assertArrayEquals(expected, result);
    }

    @Test
    void decodesArduino() {
        byte[] input = {0, 7};
        IntegerEncoding encoding = new IntegerEncoding(CodingTestUtil.BE_ENCODING);
        int result = encoding.decode(input);
        assertEquals(7, result);
    }

    @Test
    void encodesArduino() {
        IntegerEncoding encoding = new IntegerEncoding(CodingTestUtil.BE_ENCODING);
        byte[] result = encoding.encode(7);
        byte[] expected = {0, 7};
        assertArrayEquals(expected, result);
    }

    @Test
    public void isReversible() {
        int myInt = (int) (Math.random() * 256);
        IntegerEncoding encoding = new IntegerEncoding();
        int result = encoding.decode(encoding.encode(myInt));
        assertEquals(myInt, result);
    }


}