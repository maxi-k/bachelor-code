/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.util;

import java.util.Optional;

public interface Registry<K, V> extends Bijection<K, V> {

    boolean register(K key, V value);

    Optional<V> getValueFor(K key);

    Optional<K> getKeyFor(V value);

    @Override
    default V encode(K object) throws IllegalArgumentException {
        return getValueFor(object).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    default K decode(V raw) throws IllegalArgumentException {
        return getKeyFor(raw).orElseThrow(IllegalAccessError::new);
    }
}
