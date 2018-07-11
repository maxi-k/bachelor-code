/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.util;

import java.util.Optional;

public interface Registry<K, V> {

    boolean register(K key, V value);

    Optional<V> getValueFor(K key);

    Optional<K> getKeyFor(V value);

    default Bijection<K, V> asBijection() {
        return Bijection.create(
                (K obj)  -> getValueFor(obj).orElseThrow(IllegalArgumentException::new),
                (V obj)  -> getKeyFor(obj).orElseThrow(IllegalAccessError::new)        );
    }
}
