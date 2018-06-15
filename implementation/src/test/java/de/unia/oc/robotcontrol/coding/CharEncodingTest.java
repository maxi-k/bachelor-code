package de.unia.oc.robotcontrol.coding;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CharEncodingTest {

    @Test
    void decodesNative() {
        byte[] input = {97, 0};
        CharEncoding encoding = new CharEncoding(CodingContext.NATIVE);
        char result = encoding.decode(input);
        assertEquals('a', result);
    }

    @Test
    void encodesNative() {
        CharEncoding encoding = new CharEncoding(CodingContext.NATIVE);
        byte[] result = encoding.encode('d');
        byte[] expected = {100, 0};
        assertArrayEquals(expected, result);
    }

    @Test
    void decodesArduino() {
        byte[] input = {65};
        CharEncoding encoding = new CharEncoding(CodingTestUtil.BE_ENCODING);
        int result = encoding.decode(input);
        assertEquals('A', result);
    }

    @Test
    void encodesArduino() {
        CharEncoding encoding = new CharEncoding(CodingTestUtil.BE_ENCODING);
        byte[] result = encoding.encode('X');
        byte[] expected = {88};
        assertArrayEquals(expected, result);
    }

    @Test
    public void isReversible() {
        char myChar = (char) (Math.random() * 127);
        CharEncoding encoding = new CharEncoding();
        int result = encoding.decode(encoding.encode(myChar));
        assertEquals(myChar, result);
    }

}