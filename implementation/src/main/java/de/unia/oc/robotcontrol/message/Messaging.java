package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.util.Tuple;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public final class Messaging {

    @SafeVarargs
    public static <I> MessageTypeRegistry<I> createRegistry(
            MessageIdentifier<I> identifier,
            Tuple<@NonNull I, MessageType>... entries) {
        MapMessageTypeRegistry<I> reg = new MapMessageTypeRegistry<>(identifier);
        for (Tuple<I, MessageType> e : entries) {
            reg.register(e.first, e.second);
        }
        return reg;
    };

    public static <I> MessageTypeRegistry<I> createRegistry(
            MessageIdentifier<I> identifier,
            Consumer<BiFunction<@NonNull I, MessageType, Boolean>> registration) {
        MapMessageTypeRegistry<I> reg = new MapMessageTypeRegistry<>(identifier);
        registration.accept(reg::register);
        return reg;
    }

    public static MessageMulticast createDispatcher(Executor executor) {
        return new EmittingMessageMulticast(executor);
    }

}
