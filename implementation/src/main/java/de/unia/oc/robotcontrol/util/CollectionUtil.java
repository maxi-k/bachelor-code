/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.util;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CollectionUtil {

    public static <T, I extends T> List<T> interpose(I sep, T... values) {
        List<T> elems = new ArrayList<>(2 * values.length);
        for (T val : values) {
            elems.add(val);
            elems.add(sep);
        }
        elems.remove(elems.size() - 1);
        return elems;
    }

    public static String joinWith(@Nullable String sep, String... values) {
        StringBuilder builder = new StringBuilder();
        int length = 0;
        for (String val : values) {
            builder.append(val);
            builder.append(sep);
            length += val.length();
        }
        builder.delete(length, builder.length());
        return builder.toString();
    }
}
