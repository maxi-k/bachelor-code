/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.time.Duration;


/**
 * Implementation of {@link FlowStrategy} used for sending the
 * most recent N values to new subscribers of the transformed publisher,
 * thus 'replaying' a part of the history to them.
 *
 * @param <T> the type of object published by the Publisher
 *           which is to be transformed by this flow strategy
 */
public class ReplayFlowStrategy<T extends Object> extends TransparentFlowStrategy<T> {

    /**
     * The default value used for when all previously published values
     * should be replayed to new subscribers
     */
    private static final int unboundedHistorySize = Integer.MAX_VALUE;
    /**
     * The default duration for when this is used to replay values
     * received in a certain timespan instead of 'the most recent N values'
     */
    private static final Duration defaultTTL = Duration.ofMillis(0);

    /**
     * The amount of values to replay
     */
    private final int historySize;
    /**
     * The timespan from which values will be replayed
     */
    private final Duration ttl;

    /**
     * Create a new instance of {@link ReplayFlowStrategy}, which will
     * replay the most recent {@code historySize} values received back
     * to {@code now() - ttl} to new subscribers.
     * @param historySize the number of values to replay
     * @param ttl the timeframe from which to replay values
     */
    private ReplayFlowStrategy(int historySize, Duration ttl) {
        this.historySize = historySize;
        this.ttl = ttl;
    }

    /**
     * Create a new instance of {@link ReplayFlowStrategy}, which will
     * replay the most recent {@code historySize} values received back
     * to {@code now() - ttl} to new subscribers.
     * Mirror of {@link #ReplayFlowStrategy(int, Duration)}
     *
     * @param historySize the number of values to replay
     * @param ttl the timeframe from which to replay values
     * @param <T> the type of object published by the Publisher
     *           which is to be transformed by this flow strategy
     * @return a new instance of {@link ReplayFlowStrategy}
     */
    public static <T> ReplayFlowStrategy<T> create(int historySize, Duration ttl) {
        return new ReplayFlowStrategy<>(historySize, ttl);
    }

    /**
     * Like {@link #create(int, Duration)}, but uses {@link #defaultTTL}
     * for the {@code ttl} parameter
     *
     * @param historySize the number of values to replay
     * @param <T> the type of object published by the Publisher
     *           which is to be transformed by this flow strategy
     * @return a new instance of {@link ReplayFlowStrategy}
     */
    public static <T> ReplayFlowStrategy<T> create(int historySize) {
        return create(historySize, defaultTTL);
    }

    /**
     * Like {@link #create(int, Duration)}, but uses {@link #unboundedHistorySize}
     * for the {@code historySize} parameter
     *
     * @param ttl the timeframe from which to replay values
     * @param <T> the type of object published by the Publisher
     *           which is to be transformed by this flow strategy
     * @return a new instance of {@link ReplayFlowStrategy}
     */
    public static <T> ReplayFlowStrategy<T> create(Duration ttl) {
        return create(unboundedHistorySize, ttl);
    }

    /**
     * Like {@link #create(int, Duration)}, but uses {@link #unboundedHistorySize}
     * for the {@code historySize} parameter and {@link #defaultTTL} for the {@code ttl}
     * parameter
     * @param <T> the type of object published by the Publisher
     *           which is to be transformed by this flow strategy
     * @return a new instance of {@link ReplayFlowStrategy}
     */
    public static <T> ReplayFlowStrategy<T> create() {
        return create(unboundedHistorySize, defaultTTL);
    }

    @Override
    public Publisher<T> apply(Publisher<T> publisher) {
        return Flux.from(publisher)
                .replay(historySize, ttl);
    }
}
