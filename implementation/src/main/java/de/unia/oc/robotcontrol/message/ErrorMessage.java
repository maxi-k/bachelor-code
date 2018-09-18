package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.coding.CodingContext;
import de.unia.oc.robotcontrol.coding.Encoding;

import java.util.function.Supplier;

/**
 * A generic error message implementation, which uses a {@link MessageType}
 * that has no encoding ({@link Encoding#nullEncoding(CodingContext, Supplier)}.
 *
 * Can used for signaling errors to the system.
 */
public class ErrorMessage extends SingleValueMessage<Exception> {

    /**
     * The {@link MessageType} used for the error message, which
     * does not encode anything.
     */
    public static final MessageType<SingleValueMessage<Exception>> errorMessageType =
            MessageType.fromEncoding(
                    Encoding.nullEncoding(
                            CodingContext.NATIVE,
                            () -> new ErrorMessage(new RuntimeException())
                    )
            );

    /**
     * The time at which this was created.
     */
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
