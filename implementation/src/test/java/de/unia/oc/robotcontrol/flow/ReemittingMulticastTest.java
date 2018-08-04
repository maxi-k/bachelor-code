/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import de.unia.oc.robotcontrol.flow.strategy.TransparentFlowStrategy;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("initialization")
class ReemittingMulticastTest {

    private static FlowableMulticast<Character, String> multicast;

    @BeforeAll
    @EnsuresNonNull("this.multicast")
    static void setupMulticast() {
       multicast = new ReemittingMulticast<Character, String>(Schedulers.immediate()) {
           @Override
           protected Character topicFromValue(String s) {
               return s.toCharArray()[0];
           }

           @Override
           public FlowStrategy<String, String> getFlowStrategy() {
               return TransparentFlowStrategy.create();
           }
       };
    }

    private void multicastValuesDelayed() {
        Subscriber<String> sub = multicast.asSubscriber();
        Executors.newScheduledThreadPool(1).schedule(() -> {
            sub.onNext("ABC");
            sub.onNext("BCD");
            sub.onNext("ACD");
            sub.onNext("DEF");
        }, 20, TimeUnit.MILLISECONDS);
    }

    @Test
    void publishesAllValuesToPublisher() {
        multicast.subscribeTo('A');
        multicast.subscribeTo('B');

        StepVerifier verifier = StepVerifier
                .create(multicast.asPublisher())
                .expectNext("ABC")
                .expectNext("BCD")
                .expectNext("ACD")
                .expectNext("DEF")
                .thenCancel();

        multicastValuesDelayed();
        verifier.verify();
    }

    @Test
    void publishesOnlySubscribedValues() {
        StepVerifier verifierA = StepVerifier
                .create(multicast.subscribeTo('A'))
                .expectNext("ABC")
                .expectNext("ACD")
                .thenCancel();

        StepVerifier verifierD = StepVerifier
                .create(multicast.subscribeTo('D'))
                .expectNext("DEF")
                .thenCancel();

        StepVerifier verifierZ = StepVerifier
                .create(multicast.subscribeTo('Z'))
                .expectNextCount(0)
                .thenCancel();

        multicastValuesDelayed();
        verifierA.verify();
        verifierD.verify();
        verifierZ.verify();
    }
}