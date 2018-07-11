package de.unia.oc.robotcontrol.message;

public class ErrorMessage<T extends Exception> extends SingleValueMessage<T> {

    public ErrorMessage(T value) {
        super(value);
    }

    @Override
    public MessageType<SingleValueMessage<T>> getType() {
        return null;
    }
}
