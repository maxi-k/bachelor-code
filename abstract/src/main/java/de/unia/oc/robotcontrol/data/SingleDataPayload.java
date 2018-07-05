/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.data;

import java.time.Instant;

public abstract class SingleDataPayload<T> implements DataPayload<T> {

    private final long timestamp;
    private final T data;

    public SingleDataPayload(T i) {
        this.timestamp = Instant.now().getEpochSecond();
        this.data = i;
    }

    public T getData() {
        return data;
    }

    @Override
    public long getCreationTime() {
        return timestamp;
    }

    @Override
    public String toString() {
        return getData().toString() + " @ " + Instant.ofEpochSecond(timestamp).toString();
    }

}
