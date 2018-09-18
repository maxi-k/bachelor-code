/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;

import java.util.logging.Level;

/**
 * Implementation of {@link FlowStrategy} used for logging the values
 * which traverse using {@link Flux#log(String, Level, SignalType...)}.
 *
 * @param <T> the type of object received and published by the Publisher
 *           which is to be transformed by this flow strategy
 */
public class LoggingFlowStrategy<T extends Object> extends TransparentFlowStrategy<T> {

    /**
     * The default category name to set {@link #category} to if
     * no category is set in the constructor.
     */
    private static final String DEFAULT_CATEGORY = "robotcontrol.LoggingFlowStrategy";

    /**
     * The category to log on (as defined by {@link Flux#log(String, Level, SignalType...)}
     */
    private final String category;
    /**
     * The log {@link Level} level to log on.
     */
    private final Level logLevel;

    /**
     * Create a new instance of {@link LoggingFlowStrategy}, which will log
     * on the given category with the given log level. If no category string is given,
     * will log on the default category {@link #DEFAULT_CATEGORY}.
     * @param category the category to log on (as defined by {@link Flux#log(String, Level, SignalType...)}
     * @param logLevel the log level ({@link Level}) to use for logging
     */
    private LoggingFlowStrategy(@Nullable String category, Level logLevel) {
        this.category = category == null ? DEFAULT_CATEGORY : category;
        this.logLevel = logLevel;
    }

    /**
     * Create a new {@link LoggingFlowStrategy} instance.
     * Mirror of {@link #LoggingFlowStrategy(String, Level)}
     * @param category the category to log on (as defined by {@link Flux#log(String, Level, SignalType...)}
     * @param logLevel the log level ({@link Level}) to use for logging
     * @param <T> the type of object received and published by the Publisher
     *           which is to be transformed by this flow strategy
     * @return a new instance of {@link LoggingFlowStrategy}
     */
    public static <T extends Object> LoggingFlowStrategy<T> create(@Nullable String category, Level logLevel) {
        return new LoggingFlowStrategy<>(category, logLevel);
    }

    /**
     * Create a new {@link LoggingFlowStrategy} instance, using the default
     * category {@link #DEFAULT_CATEGORY}.
     *
     * @param logLevel the log level to log on
     * @param <T> the type of object received and published by the Publisher
     *           which is to be transformed by this flow strategy
     * @return a new instance of {@link LoggingFlowStrategy}
     */
    public static <T extends Object> LoggingFlowStrategy<T> create(Level logLevel) {
        return create(null, logLevel);
    }

    /**
     * Create a new {@link LoggingFlowStrategy} instance, using
     * the log level {@link Level#INFO}.
     * @param <T> the type of object received and published by the Publisher
     *           which is to be transformed by this flow strategy
     * @return a new instance of {@link LoggingFlowStrategy}
     */
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
