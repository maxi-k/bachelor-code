/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.reactivestreams.Subscriber;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@SuppressWarnings("initialization")
class MulticastTest {

    @ParameterizedTest(name = "{index} => {0}")
    @MethodSource("multicastProvider")
    void publishesAllValuesToPublisher(FlowableMulticast<Character, String> multicast) {
        multicast.subscribeTo('A');
        multicast.subscribeTo('B');

        StepVerifier verifier = StepVerifier
                .create(multicast.asPublisher())
                .expectNext("ABC")
                .expectNext("BCD")
                .expectNext("ACD")
                .expectNext("DEF")
                .thenCancel();

        multicastValuesDelayed(multicast);
        verifier.verify();
    }

    @ParameterizedTest(name = "{index} => {0}")
    @MethodSource("multicastProvider")
    void publishesOnlySubscribedValues(FlowableMulticast<Character, String> multicast) {
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

        multicastValuesDelayed(multicast);
        verifierA.verify();
        verifierD.verify();
        verifierZ.verify();
    }

    private void multicastValuesDelayed(FlowableMulticast<Character, String> multicast) {
        Subscriber<String> sub = multicast.asSubscriber();
        Executors.newScheduledThreadPool(1).schedule(() -> {
            sub.onNext("ABC");
            sub.onNext("BCD");
            sub.onNext("ACD");
            sub.onNext("DEF");
        }, 50, TimeUnit.MILLISECONDS);
    }

    private static Stream<Arguments> multicastProvider() {
        return Stream
                .of(createReemittingMulticast(),
                    createFilteringMulticast())
                .map(Arguments::of);
    }

    private static FlowableMulticast<Character, String> createReemittingMulticast() {
        return new ReemittingMulticast<Character, String>(Schedulers.immediate()) {
            @Override
            public Character topicFromValue(String s) {
                return s.toCharArray()[0];
            }
        };
    }

    private static FlowableMulticast<Character, String> createFilteringMulticast() {
        return new FilteringMulticast<Character, String>(Schedulers.immediate()) {
            @Override
            public Character topicFromValue(String v) {
                return v.toCharArray()[0];
            }
        };

    }

}