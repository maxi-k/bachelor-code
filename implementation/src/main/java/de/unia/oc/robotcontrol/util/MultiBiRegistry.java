/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.util;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;

public class MultiBiRegistry<K extends Object, V extends Object>
        implements MultiRegistry<K, V> {

    private final Map<K, Set<V>> keyMap;
    private final Map<V, Set<K>> valueMap;

    public MultiBiRegistry() {
        this.keyMap = new HashMap<>();
        this.valueMap = new HashMap<>();
    }

    @Override
    public synchronized boolean register(K key, V value) {
        if (keyMap.containsKey(key) && keyMap.get(key).contains(value)) {
            return false;
        }
        return putOrMerge(keyMap, key, value) || putOrMerge(valueMap, value, key);
    }

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
