/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.coding;

import java.nio.ByteOrder;

/**
 * Represents the hardware-specific context in which an encoding
 * takes place.
 */
public interface CodingContext {

    /**
     * {@link CodingContext} that defines the hardware-specific
     * variable for the machine this program is running on.
     */
    CodingContext NATIVE = new CodingContext() {
        @Override
        public ByteOrder getByteOrder() {
            return ByteOrder.nativeOrder();
        }

        @Override
        public int getIntSize() {
            return Integer.BYTES;
        }

        @Override
        public int getShortSize() {
            return Short.BYTES;
        }

        @Override
        public int getLongSize() { return Long.BYTES; }

        @Override
        public int getDoubleSize() { return Double.BYTES; }

        @Override
        public int getFloatSize() { return Float.BYTES; }

        @Override
        public int getCharSize() { return Character.BYTES; }

    };

    /**
     * {@link CodingContext} for a standard arduino board.
     */
    CodingContext ARDUINO = new CodingContext() {
        @Override
        public ByteOrder getByteOrder() { return ByteOrder.BIG_ENDIAN; }

        @Override
        public int getIntSize() { return 2; }

        @Override
        public int getShortSize() { return 2; }

        @Override
        public int getLongSize() { return 4; }

        @Override
        public int getDoubleSize() { return 4; }

        @Override
        public int getFloatSize() { return 4; }

        @Override
        public int getCharSize() { return 1; }
    };

    /**
     *
     * @return The {@link ByteOrder} (little endian or big endian) of the platform.
     */
    ByteOrder getByteOrder();

    /**
     * How many bytes make one {@link Integer}?
     * @return The integer size of the platform (in bytes).
     */
    int getIntSize();

    /**
     * How many bytes make one {@link Short}?
     * @return The short size of the platform (in bytes).
     */
    int getShortSize();

    /**
     * How many bytes make one {@link Long}?
     * @return The long size of the platform (in bytes).
     */
    int getLongSize();

    /**
     * How many bytes make one {@link Double}?
     * @return The double size of the platform (in bytes).
     */
    int getDoubleSize();

    /**
     * How many bytes make one {@link Float}?
     * @return The float size of the platform (in bytes).
     */
    int getFloatSize();

    /**
     * How many bytes make one {@link Character}?
     * @return The char size of the platform (in bytes).
     */
    int getCharSize();

    /**
     * Specifies whether the bytes for some value are saved in reverse
     * on the platform this instance represents in relation of the native platform
     * (little endian vs big endian).
     *
     * @return Whether the {@link ByteOrder} of the native platform is different
     *         from the {@link ByteOrder} of the platform this instance of
     *         {@link CodingContext} represents.
     */
    default boolean doReverse() {
        return getByteOrder() != NATIVE.getByteOrder();
    }
}
