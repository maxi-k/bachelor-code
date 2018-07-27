package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.coding.CodingContext;
import de.unia.oc.robotcontrol.coding.Encoding;

public class SingleValueMessageType<T> implements MessageType<SingleValueMessage<T>> {

    private final Encoding<T> encoding;

    public SingleValueMessageType(Encoding<T> encoding) {
        this.encoding = encoding;
    }

    @Override
    public byte[] encode(SingleValueMessage<T> object) throws IllegalArgumentException {
        return encoding.encode(object.getValue());
    }

    @Override
    public SingleValueMessage<T> decode(byte[] raw) throws IllegalArgumentException {
        return produce(encoding.decode(raw));
    }

    public SingleValueMessage<T> produce(T value) {
        long now = System.currentTimeMillis();
        return new SingleValueMessage<T>(value) {
            @Override
            public MessageType<SingleValueMessage<T>> getType() {
                return SingleValueMessageType.this;
            }

            @Override
            public long getCreationTime() {
                return now;
            }
        };
    }

    @Override
    public CodingContext getContext() {
        return encoding.getContext();
    }
}
