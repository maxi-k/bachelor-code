/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.device;

import de.unia.oc.robotcontrol.coding.CodingContext;
import de.unia.oc.robotcontrol.coding.IntegerEncoding;
import de.unia.oc.robotcontrol.concurrent.ScheduleProvider;
import de.unia.oc.robotcontrol.concurrent.Scheduling;
import de.unia.oc.robotcontrol.message.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LockingDeviceConnectorTest {

    @Test
    void sendsAndRetrievesMessages() throws InterruptedException {

        SingleValueMessageType<Integer> msgType = new SingleValueMessageType<>(new IntegerEncoding(CodingContext.ARDUINO));

        // Create new schedule provider with fixed interval
        ScheduleProvider schedule = Scheduling.interval(
                Executors.newScheduledThreadPool(1),
                20,
                TimeUnit.MILLISECONDS
        );

        int sentValue = 42;
        List<Message<SingleValueMessage<Integer>>> received = new ArrayList<>(5);

        CallbackMessageRecipient<? extends Message> recipient = new CallbackMessageRecipient<>((Message m) -> {
            SingleValueMessage<Integer> msg = msgType.cast(m);
            // System.out.println("Received message: " + m);
            received.add(msg);
            Assertions.assertNotNull(m);
            Assertions.assertEquals((int) msg.getValue(), sentValue);
        });

        // Mock Device which echoes back the last value sent
        // on the schedule provides by the TrackingScheduleProvider
        MockLockingDeviceConnector<Message, Message> connector = new MockLockingDeviceConnector(
                msgType.asEncoding(),
                () -> msgType.produce(42));

        // Sent the connector a first value
        // connector.inFlow().accept(msgType.produce(sentValue));

        Thread.sleep(120);
        Assertions.assertTrue(received.size() >= 5);

        connector.asSubscriber().onNext(msgType.produce(sentValue));
    }
}
