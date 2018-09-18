/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.util;

import java.util.*;

/**
 * Implementation of a many-to-many registry ({@link MultiRegistry}).
 * Uses two separate maps to guarantee fast lookup times in both
 * directions.
 *
 * @param <K> the type used for the keys
 * @param <V> the type used for the values
 */
public class MultiBiRegistry<K extends Object, V extends Object>
        implements MultiRegistry<K, V> {

    /**
     * The map associating {@link K} instances to multiple values
     */
    private final Map<K, Set<V>> keyMap;
    /**
     * The map associating {@link V} instances to multiple keys
     */
    private final Map<V, Set<K>> valueMap;

    /**
     * Creates a new instance of {@link MultiBiRegistry}, backed
     * by two Instances of {@link HashMap}
     */
    public MultiBiRegistry() {
        this.keyMap = new HashMap<>();
        this.valueMap = new HashMap<>();
    }

    /**
     * {@inheritDoc}
     *
     * @param key the key under which to store the value
     * @param value the value to store under the given key
     *
     * @return whether the key was associated with the given value
     * and vice versa
     */
    @Override
    public synchronized boolean register(K key, V value) {
        if (keyMap.containsKey(key) && keyMap.get(key).contains(value)) {
            return false;
        }
        return putOrMerge(keyMap, key, value) || putOrMerge(valueMap, value, key);
    }

    /**
     * {@inheritDoc}
     *
     * Retrieves the values associated with the given
     * key. If the key is {@code null}, returns an empty
     * set. The returned set is not modifiable.
     *
     * @param key the key to look up
     * @return a collection containing all the associated values
     */
    @Override
    public Collection<V> getValuesFor(K key) {
        if (key == null) return Collections.emptySet();
        synchronized (keyMap) {
            if (keyMap.containsKey(key)) {
                return Collections.unmodifiableSet(keyMap.get(key));
            }
            return Collections.emptySet();
        }
    }

    /**
     * {@inheritDoc}
     *
     * Retrieves the keys associated with the given
     * value. If the value is {@code null}, returns an empty
     * set. The returned set is not modifiable.
     *
     * @param value the value to look up
     * @return a collection containing all the associated keys
     */
    @Override
    public Collection<K> getKeysFor(V value) {
        if (value == null) return Collections.emptySet();
        synchronized (valueMap) {
            if (valueMap.containsKey(value)) {
                return Collections.unmodifiableSet(valueMap.get(value));
            }
            return Collections.emptySet();
        }
    }

    /**
     * Puts the given value into the map from key to multiple values
     * if there is an entry already. If not, creates a new Set and puts it
     * in the map.
     * @param map the map to lookup in and modify
     * @param key the key to put in values under
     * @param value the value to put into the map
     * @param <A> the type of the key
     * @param <B> the type of a single value
     * @return whether the map was modified
     */
    private static <A extends Object, B extends Object> boolean putOrMerge(Map<A, Set<B>> map, A key, B value) {
        if (map.containsKey(key)) {
            return map.get(key).add(value);
        } else {
            map.put(key, new HashSet<B>(1));
            map.get(key).add(value);
            return true;
        }
    }
}
