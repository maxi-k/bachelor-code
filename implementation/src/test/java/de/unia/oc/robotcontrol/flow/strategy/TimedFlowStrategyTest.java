package de.unia.oc.robotcontrol.flow.strategy;/* %FILE_TEMPLATE_TEXT% */

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class TimedFlowStrategyTest {

    @Test
    void emitsOnEmpty() throws InterruptedException {
        Duration dur = Duration.ofMillis(10);

        Flux<Object> empty = Flux.never();
        FlowStrategy<Object, Long> strategy = TimedFlowStrategy.createDurational(dur, Object::new, (l, o) -> l);

        Publisher<Long> combined = empty.transform(strategy);

        Duration run = StepVerifier
                .create(combined)
                .expectNext(0L)
                .expectNext(1L)
                .thenAwait()
                .thenCancel()
                .verify(Duration.ofMillis(dur.toMillis() * 5));
    }

}