/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.util;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Class with utility methods for collections.
 * This only contains static methods and cannot be instantiated.
 */
public final class CollectionUtil {

    private CollectionUtil() {}

    /**
     * Interpose the given separator value between
     * the passed values vararg.
     * @param sep the value to interpose
     * @param values the values to interpose in
     * @param <T> the type of the values
     * @param <I> the type of the separator
     * @return a new {@link ArrayList} with the given separator interleaved
     */
    public static <T, I extends T> List<T> interpose(I sep, T... values) {
        List<T> elems = new ArrayList<>(2 * values.length);
        for (T val : values) {
            elems.add(val);
            elems.add(sep);
        }
        elems.remove(elems.size() - 1);
        return elems;
    }

    /**
     * Join the given String values with the given separator using
     * a {@link StringBuilder}
     *
     * @param sep the separator to interleave
     * @param values the strings to join
     * @return a new String which joins all passed strings,
     * putting the separator in between
     */
    public static String joinWith(@Nullable String sep, String... values) {
        StringBuilder builder = new StringBuilder();
        String separator = sep == null ? "" : sep;
        int length = 0;
        for (String val : values) {
            builder.append(val);
            builder.append(separator);
            length += val.length();
        }
        builder.delete(length, builder.length());
        return builder.toString();
    }
}
