/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.Optional;

public class BidirectionalRegistry<K, V> implements Registry<K, V> {

    private BiMap<K, V> store;

    public BidirectionalRegistry() {
        this.store = HashBiMap.create();
    }

    @Override
    public boolean register(K key, V value) {
        if (store.containsKey(key)) {
            return false;
        }
        store.put(key, value) ;
        return true;
    }

    @Override
    public Optional<V> getValueFor(K key) {
        return store.containsKey(key) ?
                Optional.of(store.get(key)) :
                Optional.empty();
    }

    @Override
    public Optional<K> getKeyFor(V value) {
        return store.containsValue(value) ?
                Optional.of(store.inverse().get(value)) :
                Optional.empty();
    }
}
