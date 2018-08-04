/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class ReplayFlowStrategy<T extends Object> extends TransparentFlowStrategy<T> {

    private static final int unboundedHistorySize = Integer.MAX_VALUE;
    private static final Duration defaultTTL = Duration.ofMillis(0);

    private final int historySize;
    private final Duration ttl;

    private ReplayFlowStrategy(int historySize, Duration ttl) {
        this.historySize = historySize;
        this.ttl = ttl;
    }

    public static <T> ReplayFlowStrategy<T> create(int historySize, Duration ttl) {
        return new ReplayFlowStrategy<>(historySize, ttl);
    }

    public static <T> ReplayFlowStrategy<T> create(int historySize) {
        return create(historySize, defaultTTL);
    }

    public static <T> ReplayFlowStrategy<T> create(Duration ttl) {
        return create(unboundedHistorySize, ttl);
    }

    public static <T> ReplayFlowStrategy<T> create() {
        return create(unboundedHistorySize, defaultTTL);
    }

    @Override
    public Publisher<T> apply(Publisher<T> publisher) {
        return Flux.from(publisher)
                .replay(historySize, ttl);
    }
}
