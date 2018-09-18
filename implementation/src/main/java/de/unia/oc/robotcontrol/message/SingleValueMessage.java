package de.unia.oc.robotcontrol.message;

/**
 * Abstract superclass for Messages which hold a single value.
 * The value has to be passed when constructed, and cannot be changed.
 *
 * @param <T> the type of the encapsulated value
 */
public abstract class SingleValueMessage<T> implements Message<SingleValueMessage<T>> {

    /**
     * The wrapped value.
     */
    private final T value;

    /**
     * Create a new instance of {@link SingleValueMessage}, wrapping
     * the given value.
     * @param value the value to wrap.
     */
    public SingleValueMessage(T value) {
        this.value = value;
    }

    public synchronized T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Message wrapping: " + (value != null ? value.toString() : "null");
    }
}
