/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.coding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An encoding for a list of values which a fixed length.
 *
 * Uses an existing {@link FixedEncoding} to encode and
 * decode each single value.
 *
 * The overall resulting {@link FixedEncoding} encodes
 * and decodes a {@link List} from a byte array
 * which has the byte representations of each single
 * value just appended to each other.
 *
 * @param <T> The type of the single values encoded.
 */
public class ListEncoding<T> implements FixedEncoding<List<T>> {

    /**
     * The {@link FixedEncoding} instance used for
     * encoding and decoding each element.
     */
    private FixedEncoding<T> singleEncoding;

    /**
     * The fixed number of elements
     * the list is asserted to have.
     */
    private int numElements;

    /**
     * Create a new Instance of {@link ListEncoding} by wrapping
     * an existing encoding for a single element, which is used
     * a fixed number of times.
     *
     * @param singleEncoding the encoding used to encode/decode the elements
     *                       of the list
     * @param numElements the number of elements which will be in the list
     */
    public ListEncoding(FixedEncoding<T> singleEncoding,
                        int numElements) {
        this.singleEncoding = singleEncoding;
        this.numElements = numElements;
    }

    /**
     * {@inheritDoc}
     *
     * @return the overall number of bytes required by this encoding, that is,
     * of the complete list
     *
     */
    @Override
    public int numBytes() {
        return numElements * singleEncoding.numBytes();
    }

    @Override
    public List<T> decode(byte[] raw) {
        int singleBytes = singleEncoding.numBytes();
        List<T> result = new ArrayList<>(this.numElements);

        for (int i = 0; i < this.numElements; ++i) {
            int idx = i * singleBytes;
            byte[] elemBytes = Arrays.copyOfRange(raw, idx, idx + singleBytes);
            result.add(singleEncoding.decode(elemBytes));
        }
        return result;
    }

    @Override
    public byte[] encode(List<T> list) throws IllegalArgumentException {
        if (list.size() != this.numElements) {
            throw new IllegalArgumentException("List is too large or too small! Expected size: " + this.numElements);
        }
        int singleBytes = singleEncoding.numBytes();
        byte[] result = new byte[list.size() * singleBytes];
        for (int i = 0; i < list.size(); ++i) {
            byte[] single = singleEncoding.encode(list.get(i));
            System.arraycopy(single, 0, result, i * singleBytes, singleBytes);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * The {@link CodingContext} is the same as the one used
     * by the encoding for the single value, and they are both
     * when using {@link #withContext(CodingContext)}
     *
     * @return the coding context this encoding uses
     */
    @Override
    public CodingContext getContext() {
        return singleEncoding.getContext();
    }

    /**
     * {@inheritDoc}
     *
     * Creates a new {@link ListEncoding} instance which uses the
     * given context. Also sets the context on the internally
     * used encoding for the single elements
     *
     * @param context the {@link CodingContext} to set
     * @return a new instance of {@link ListEncoding}
     */
    @Override
    public ListEncoding<T> withContext(CodingContext context) {
        return new ListEncoding<T>(
                this.singleEncoding.withContext(context),
                this.numElements);
    }
}
