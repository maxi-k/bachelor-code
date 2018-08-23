/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.concurrent;

import de.unia.oc.robotcontrol.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.time.Duration;

class EmittingClockTest {

    @Test
    void publishesAtInterval() throws InterruptedException {
        final long millis = 20;
        final long hysteresis = 5;

        Duration duration = Duration.ofMillis(millis);
        EmittingClock clock = EmittingClock.create(duration);

        clock.getTicks().subscribe(createSubscriberExpecting(millis, hysteresis));

        Thread.sleep(100);
    }


    @Test
    void handlesMultipleSubscribers() throws InterruptedException {
        final long millis = 20;
        final long hysteresis = 5;

        Duration duration = Duration.ofMillis(millis);
        EmittingClock clock = EmittingClock.create(duration);

        clock.getTicks().subscribe(createSubscriberExpecting(millis, hysteresis));
        Thread.sleep(60);
        clock.getTicks().subscribe(createSubscriberExpecting(millis, hysteresis));

        Thread.sleep(160);
    }

    @Test
    void switchesIntervals() throws InterruptedException {
        final long[] millis = { 20, 40, 10 };
        final long hysteresis = 5;

        EmittingClock clock = EmittingClock.create(Duration.ofMillis(millis[0]));

        for (long m : millis) {
            clock.getTicks().subscribe(createSubscriberExpecting(m, 5));
            Thread.sleep(m * 5);
        }
    }

    private Subscriber<Long> createSubscriberExpecting(final long expectedMillis, final long hysteresis) {
        return new Subscriber<Long>() {
            private long lastMillis = 0;
            private int msgCount = 0;

            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Long counter) {
                long curTime = System.currentTimeMillis();
                if (lastMillis != 0 && msgCount > 0) {
                    TestUtil.assertInRange(curTime - lastMillis, expectedMillis, hysteresis);
                }
                lastMillis = curTime;
            }

            @Override
            public void onError(Throwable t) {
                Assertions.fail("Clock emitted an error.", t);
            }

            @Override
            public void onComplete() {
                if (lastMillis == 0) {
                    Assertions.fail("Clock did not emit any items");
                }
            }
        };
    }
}