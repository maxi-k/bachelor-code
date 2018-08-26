/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.util.logging.Level;

public class LoggingFlowStrategy<T extends Object> extends TransparentFlowStrategy<T> {

    private final String category;
    private final Level logLevel;

    private LoggingFlowStrategy(@Nullable String category, Level logLevel) {
        this.category = category == null ? "robotcontrol.LoggingFlowStrategy" : category;
        this.logLevel = logLevel;
    }

    public static <T extends Object> LoggingFlowStrategy<T> create(@Nullable String category, Level logLevel) {
        return new LoggingFlowStrategy<>(category, logLevel);
    }

    public static <T extends Object> LoggingFlowStrategy<T> create(Level logLevel) {
        return create(null, logLevel);
    }

    public static <T extends Object> LoggingFlowStrategy<T> create() {
        return create(Level.INFO);
    }

    @Override
    public Publisher<T> apply(Publisher<T> publisher) {
        return Flux
                .from(publisher)
                .log(category, logLevel);
    }
}
