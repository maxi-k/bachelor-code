/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.Optional;

/**
 * An implementation of a one-to-one bidirectional registry.
 * @param <K> the type used for the keys
 * @param <V> the type used for the values
 */
public class BidirectionalRegistry<K extends Object, V extends Object>
        implements Registry<K, V> {

    /**
     * The Guava {@link BiMap} backing this store.
     * Uses a {@link HashBiMap} by default
     */
    private final BiMap<K, V> store;

    /**
     * Create a new {@link BidirectionalRegistry} instance
     * backed by a Guava {@link HashBiMap}.
     */
    public BidirectionalRegistry() {
        this.store = HashBiMap.create();
    }

    /**
     * {@inheritDoc}
     *
     * Put the given key and the given value into the
     * backing {@link #store} together. Synchronized.
     *
     * @param key the key under which to store the value
     * @param value the value to store
     *
     * @return whether the key and value were already
     * associated
     */
    @Override
    public boolean register(K key, V value) {
        synchronized(store) {
            if (store.containsKey(key)) {
                return false;
            }
            store.put(key, value);
            return true;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param key the key to look up the value for
     * @return an Optional wrapping the value retrieved from the {@link #store}
     */
    @Override
    public Optional<V> getValueFor(K key) {
        if (key == null) return Optional.empty();
        synchronized(store) {
            return store.containsKey(key) ?
                    Optional.ofNullable(store.get(key)) :
                    Optional.empty();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param value the value to look up the key for
     * @return an Optional wrapping the associated value
     * retrieved from the {@link #store}
     */
    @Override
    public Optional<K> getKeyFor(V value) {
        if (value == null) return Optional.empty();
        synchronized(store) {
            return store.containsValue(value) ?
                    Optional.ofNullable(store.inverse().get(value)) :
                    Optional.empty();
        }
    }
}
