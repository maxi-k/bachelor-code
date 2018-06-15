/* 2016 */
package de.unia.oc.robotcontrol.coding;

import java.nio.ByteOrder;

public class CodingTestUtil {

    /**
     * A mock coding context for big endian encodings.
     * Mirrors the arduino when it comes to data type byte sizes.
     */
    public static CodingContext BE_ENCODING = new CodingContext() {
        @Override
        public ByteOrder getByteOrder() { return ByteOrder.BIG_ENDIAN; }

        @Override
        public int getIntSize() { return 2; }

        @Override
        public int getShortSize() { return 2; }

        @Override
        public int getDoubleSize() { return 4; }

        @Override
        public int getFloatSize() { return 4; }

        @Override
        public int getCharSize() { return 1; }
    };
}
