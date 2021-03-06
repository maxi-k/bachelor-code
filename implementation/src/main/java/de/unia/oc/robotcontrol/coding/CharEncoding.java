/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.coding;

import java.nio.ByteBuffer;

/**
 * Encodes a single character using a {@link ByteBuffer},
 * as implemented by the superclass {@link SingleValueEncoding}.
 */
public class CharEncoding extends SingleValueEncoding<Character> {

    public CharEncoding() {
        super();
    }

    public CharEncoding(CodingContext context) {
        super(context);
    }

    @Override
    protected int nativeByteCount() {
        return CodingContext.NATIVE.getCharSize();
    }

    @Override
    protected ByteBuffer intoByteBuffer(ByteBuffer buffer, Character object) {
        return buffer.putChar(object);
    }

    @Override
    protected Character fromByteBuffer(ByteBuffer buffer) {
        return buffer.getChar();
    }

    @Override
    public int numBytes() {
        return getContext().getCharSize();
    }
}
