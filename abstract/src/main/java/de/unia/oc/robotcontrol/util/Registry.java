/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.util;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Optional;

/**
 * Defines a general interface for a (bidirectional) registry,
 * which can store and retrieve values.
 *
 * @param <K> the type of the key elements
 * @param <V> the type of the value elements
 */
public interface Registry<K, V> {

    /**
     * Register a the given value for the given key
     * @param key the key under which to store the value
     * @param value the value to store
     *
     * @return whether the value was registered or not
     */
    boolean register(K key, V value);

    /**
     * Get the value for the given key,
     * wrapped in an Optional as it might not be registered.
     *
     * @param key the key to look up
     * @return An Optional that wraps the retrieved value
     */
    Optional<@NonNull V> getValueFor(K key);

    /**
     * Get the key for the given value,
     * wrapped in an Optional as it might not be registered.
     *
     * @param value the value to look up the key for
     * @return An Optional that wraps the retrieved value
     */
    Optional<@NonNull K> getKeyFor(V value);

    /**
     * Return a bijection that wraps this registry where
     * encode corresponds to getValueFor and
     * decode corresponds to getKeyFor.
     * @return an instance of Bijection that wraps this registry
     */
    default Bijection<K, V> asBijection() {
        return Bijection.create(
                (K obj)  -> getValueFor(obj).<ItemNotRegisteredException>orElseThrow(ItemNotRegisteredException::new),
                (V obj)  -> getKeyFor(obj).<ItemNotRegisteredException>orElseThrow(ItemNotRegisteredException::new));
    }

    /**
     * Exception that is thrown when an item (supposedly given in a method argument)
     * is not registered.
     */
    class ItemNotRegisteredException extends IllegalArgumentException {}
}
