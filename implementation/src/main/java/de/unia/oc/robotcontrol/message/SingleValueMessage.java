package de.unia.oc.robotcontrol.message;

public abstract class SingleValueMessage<T> implements Message<SingleValueMessage<T>> {

    private final T value;

    public SingleValueMessage(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Message wrapping: " + (value != null ? value.toString() : "null");
    }
}
