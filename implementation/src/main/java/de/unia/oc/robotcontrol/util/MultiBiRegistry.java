/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.util;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;

public class MultiBiRegistry<K, V> implements MultiRegistry<K, V> {

    private final Map<@NonNull K, Set<@NonNull V>> keyMap;
    private final Map<@NonNull V, Set<@NonNull K>> valueMap;

    public MultiBiRegistry() {
        this.keyMap = new HashMap<>();
        this.valueMap = new HashMap<>();
    }

    @Override
    public synchronized boolean register(@NonNull K key, @NonNull V value) {
        if (keyMap.containsKey(key) && keyMap.get(key).contains(value)) {
            return false;
        }
        return putOrMerge(keyMap, key, value) || putOrMerge(valueMap, value, key);
    }

    @Override
    public Collection<@NonNull V> getValuesFor(@NonNull K key) {
        synchronized (keyMap) {
            if (keyMap.containsKey(key)) {
                return Collections.unmodifiableSet(keyMap.get(key));
            }
            return Collections.emptySet();
        }
    }

    @Override
    public Collection<@NonNull K> getKeysFor(@NonNull V value) {
        synchronized (valueMap) {
            if (valueMap.containsKey(value)) {
                return Collections.unmodifiableSet(valueMap.get(value));
            }
            return Collections.emptySet();
        }
    }

    private static <A, B> boolean putOrMerge(Map<A, Set<B>> map, @NonNull A key, @NonNull B value) {
        if (map.containsKey(key)) {
            return map.get(key).add(value);
        } else {
            map.put(key, new HashSet<B>(1));
            map.get(key).add(value);
            return true;
        }
    }
}
