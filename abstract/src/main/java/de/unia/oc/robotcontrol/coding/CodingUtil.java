/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.coding;

import de.unia.oc.robotcontrol.util.Tuple;

/**
 * Utility class that Encodings may use.
 */
public class CodingUtil {

    /**
     * Reverses the given byte array in-place, mutating it.
     * @param input The byte array to reverse.
     */
    public static void reverseBytes(byte[] input)  {
        for(int i = 0; i < input.length / 2; i++)
        {
            byte temp = input[i];
            input[i] = input[input.length - i - 1];
            input[input.length - i - 1] = temp;
        }
    }

    public static Tuple<byte[], byte[]> splitAt(byte[] input, int pos) {
        byte[] first = new byte[pos];
        byte[] rest = new byte[input.length - pos];
        System.arraycopy(input, 0, first, 0, rest.length);
        System.arraycopy(input, pos, rest, 0, first.length);
        return new Tuple<>(first, rest);
    }

    public static byte[] join(byte[] first, byte[] second) {
        byte[] combined = new byte[first.length + second.length];
        System.arraycopy(first, 0, combined, 0, first.length);
        System.arraycopy(second, 0, combined, first.length, second.length);
        return combined;
    }
}
