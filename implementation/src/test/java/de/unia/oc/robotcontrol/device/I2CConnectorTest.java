/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.device;

import de.unia.oc.robotcontrol.coding.CodingContext;
import de.unia.oc.robotcontrol.coding.IntegerEncoding;
import de.unia.oc.robotcontrol.concurrent.ScheduleProvider;
import de.unia.oc.robotcontrol.message.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class I2CConnectorTest {

    private void assertInRange(long actual, long expected, long hysteresis) {
        try {
            Assertions.assertTrue(
                    actual > (expected - hysteresis) &&
                            actual < (expected + hysteresis)
            );
        } catch(AssertionFailedError e) {
            throw new AssertionFailedError(
                    "Assertion Failed: Value was not in range: ",
                    expected  + " +/- " + hysteresis,
                    actual
            );
        }
    }

    @Test
    void testScheduling() throws InterruptedException
    {
        int firstInterval = 40;
        int secondInterval = 5;
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;

        // record time for each execution to test rescheduling
        List<Long> timestamps = new ArrayList<>(15);
        int sentValue = (int) (Math.random() * 1024);

        SingleValueMessageType<Integer> msgType = new SingleValueMessageType<>(new IntegerEncoding(CodingContext.ARDUINO));

        // Create new schedule provider with fixed interval
        ScheduleProvider schedule = ScheduleProvider.interval(
                Executors.newScheduledThreadPool(1),
                firstInterval,
                timeUnit
        );

        CallbackMessageRecipient recipient = new CallbackMessageRecipient((m) -> {
            SingleValueMessage<Integer> msg = msgType.cast(m);
            Assertions.assertNotNull(m);
            Assertions.assertEquals((int) msg.getValue(), sentValue);
            timestamps.add(System.currentTimeMillis());
        });

        // Mock Device which echoes back the last value sent
        // on the schedule provides by the ScheduleProvider
        I2CEchoConnector connector = new I2CEchoConnector(msgType.asEncoding(), schedule, recipient.inFlow());

        // Sent the connector a first value
        connector.inFlow().accept(msgType.produce(sentValue));

        // Execute the task a couple of times, then
        // reschedule the ScheduleProvider
        TimeUnit.MILLISECONDS.sleep(firstInterval * 5);
        schedule.reschedule(1, secondInterval, timeUnit);
        TimeUnit.MILLISECONDS.sleep(secondInterval * 5);

        // Assert that the provider rescheduled the task correctly
        long firstDelay = timestamps.get(2) - timestamps.get(1);
        long lastDelay = timestamps.get(timestamps.size() - 1) - timestamps.get(timestamps.size() - 2);
        long hysteresis = 10;

        assertInRange(firstDelay, firstInterval, hysteresis);
        assertInRange(lastDelay, secondInterval, hysteresis);
    }
}
