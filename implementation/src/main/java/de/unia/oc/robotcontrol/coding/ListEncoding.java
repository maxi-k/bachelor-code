/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.coding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListEncoding<T> implements FixedEncoding<List<T>> {

    private FixedEncoding<T> singleEncoding;
    private int numElements;

    public ListEncoding(FixedEncoding<T> singleEncoding,
                        int numElements) {
        this.singleEncoding = singleEncoding;
        this.numElements = numElements;
    }

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

    @Override
    public CodingContext getContext() {
        return singleEncoding.getContext();
    }

    @Override
    public ListEncoding<T> withContext(CodingContext context) {
        return new ListEncoding<T>(
                this.singleEncoding.withContext(context),
                this.numElements);
    }
}
