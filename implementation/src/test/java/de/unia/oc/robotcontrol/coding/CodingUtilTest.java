package de.unia.oc.robotcontrol.coding;

import org.junit.jupiter.api.Test;

import static de.unia.oc.robotcontrol.coding.CodingUtil.reverseBytes;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class CodingUtilTest {

    @Test
    void reversesArrayInPlace() {
        byte[] input = { 1, 0, 5};
        reverseBytes(input);
        byte[] expected = { 5, 0, 1};
        assertArrayEquals(input, expected);

        input = new byte[]{1, 5};
        reverseBytes(input);
        expected = new byte[]{5, 1};
        assertArrayEquals(input, expected);
    }



}