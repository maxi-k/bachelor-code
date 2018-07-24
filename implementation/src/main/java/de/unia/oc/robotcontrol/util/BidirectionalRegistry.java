/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Optional;

public class BidirectionalRegistry<K, V> implements Registry<K, V> {

    private final BiMap<K, V> store;

    public BidirectionalRegistry() {
        this.store = HashBiMap.create();
    }

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

    @Override
    public Optional<@NonNull V> getValueFor(K key) {
        synchronized(store) {
            return store.containsKey(key) ?
                    Optional.ofNullable(store.get(key)) :
                    Optional.empty();
        }
    }

    @Override
    public Optional<@NonNull K> getKeyFor(V value) {
        synchronized(store) {
            return store.containsValue(value) ?
                    Optional.ofNullable(store.inverse().get(value)) :
                    Optional.empty();
        }
    }
}
