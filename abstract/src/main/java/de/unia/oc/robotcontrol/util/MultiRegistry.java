/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.util;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collection;

/**
 * Defines a general interface for a (bidirectional) registry,
 * which can store and retrieve values.
 *
 * The association is many-to-many, that is, one key can have
 * multiple values and vice versa.
 *
 * @param <K> the type of the key elements
 * @param <V> the type of the value elements
 */
public interface MultiRegistry<K, V> {

    /**
     * Register a the given value for the given key
     * @param key the key under which to store the value
     * @param value the value to store
     *
     * @return whether the value was registered or not
     */
    boolean register(@NonNull K key, @NonNull V value);

    /**
     * Get the values associated with the given key,
     * as an unmodifiable collection.
     *
     * @param key the key to look up
     * @return A (possibly empty, immutable) collection associated with the given value
     */
    Collection<@NonNull V> getValuesFor(K key);

    /**
     * Get the keys associated with the given value,
     * as an unmodifiable collection.
     *
     * @param value the value to look up the keys for
     * @return A (possibly empty, immutable) collection associated with the given value
     */
    Collection<@NonNull K> getKeysFor(V value);
}
