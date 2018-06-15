/* 2016 */
package de.unia.oc.robotcontrol.coding;

/**
 * Utility class that Encodings may use.
 */
class CodingUtil {
    /**
     * Reverses the given byte array in-place, mutating it.
     * @param input The byte array to reverse.
     */
    static void reverseBytes(byte[] input)  {
        for(int i = 0; i < input.length / 2; i++)
        {
            byte temp = input[i];
            input[i] = input[input.length - i - 1];
            input[input.length - i - 1] = temp;
        }
    }
}
