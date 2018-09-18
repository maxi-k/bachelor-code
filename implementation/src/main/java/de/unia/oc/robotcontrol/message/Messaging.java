package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.util.Tuple;

import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Class with utility methods for the messaging module of
 * the project ({@link de.unia.oc.robotcontrol.message})
 * ("module-class").
 *
 * This only contains static methods and cannot be instantiated.
 */
public final class Messaging {

    private Messaging() {}

    /**
     * Create a new {@link MessageTypeRegistry} instance using the given
     * identifier encoding, and adding the entries passed as vararg to
     * it right away.
     *
     * @param identifier the identifier encoding to use to separate the
     *                   message identifier from the actual message data
     * @param entries the entries to add to the registry right away
     * @param <I> the type of the identifier used
     * @return a new instance of {@link MapMessageTypeRegistry}
     */
    @SafeVarargs
    public static <I extends Object> MessageTypeRegistry<I> createRegistry(
            MessageIdentifier<I> identifier,
            Tuple<I, MessageType>... entries) {
        MapMessageTypeRegistry<I> reg = new MapMessageTypeRegistry<>(identifier);
        for (Tuple<I, MessageType> e : entries) {
            reg.register(e.first, e.second);
        }
        return reg;
    };

    /**
     * Create a new {@link MessageTypeRegistry} instance using the given
     * identifier encoding, and passes it to the given function so that
     * it can be used to directly add entries to the registry.
     *
     * @param identifier the identifier encoding to use to separate the
     *                   message identifier from the actual message data
     * @param registration the function which is passed the {@link MessageTypeRegistry},
     *                    so it can register entries in the registry.
     * @param <I> the type of the identifier used
     * @return a new instance of {@link MapMessageTypeRegistry}
     */
    public static <I extends Object> MessageTypeRegistry<I> createRegistry(
            MessageIdentifier<I> identifier,
            Consumer<BiFunction<I, MessageType, Boolean>> registration) {
        MapMessageTypeRegistry<I> reg = new MapMessageTypeRegistry<>(identifier);
        registration.accept(reg::register);
        return reg;
    }

    /**
     * Create a new {@link MessageMulticast}
     * @param executor the {@link Executor} to run the multicast on
     * @return a new instance of {@link EmittingMessageMulticast}
     */
    public static MessageMulticast createDispatcher(Executor executor) {
        return new EmittingMessageMulticast(executor);
    }

}
