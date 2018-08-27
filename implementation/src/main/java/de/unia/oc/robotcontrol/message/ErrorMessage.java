package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.coding.CodingContext;
import de.unia.oc.robotcontrol.coding.Encoding;

public class ErrorMessage extends SingleValueMessage<Exception> {

    public static final MessageType<SingleValueMessage<Exception>> errorMessageType =
            MessageType.fromEncoding(
                    Encoding.nullEncoding(
                            CodingContext.NATIVE,
                            () -> new ErrorMessage(new RuntimeException())
                    )
            );

    private final long creationTime;

    public ErrorMessage(Exception value) {
        super(value);
        this.creationTime = System.currentTimeMillis();
    }

    @Override
    public MessageType<SingleValueMessage<Exception>> getType() {
        return errorMessageType;
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }
}
