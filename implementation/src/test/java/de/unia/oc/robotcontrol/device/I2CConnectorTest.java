/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.device;

import de.unia.oc.robotcontrol.coding.CodingContext;
import de.unia.oc.robotcontrol.coding.IntegerEncoding;
import de.unia.oc.robotcontrol.concurrent.ScheduleProvider;
import de.unia.oc.robotcontrol.message.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class I2CConnectorTest {

    @Test
    void testScheduling() throws InterruptedException, ExecutionException {
        SingleValueMessageType<Integer> msgType = new SingleValueMessageType<>(new IntegerEncoding(CodingContext.ARDUINO));
        ScheduleProvider schedule = ScheduleProvider.interval(
                Executors.newScheduledThreadPool(2),
                1,
                TimeUnit.SECONDS
        );

        int sentValue = (int) (Math.random() * 1024);

        CallbackMessageRecipient recipient = new CallbackMessageRecipient((m) -> {
            // System.out.println("Message received: " + m.toString());
            SingleValueMessage<Integer> msg = msgType.cast(m);
            Assertions.assertNotNull(m);
            Assertions.assertEquals((int) msg.getValue(), sentValue);
        });

        I2CEchoConnector connector = new I2CEchoConnector(msgType.asEncoding(), schedule, recipient.inFlow());

        connector.inFlow().accept(msgType.produce(sentValue));

        TimeUnit.SECONDS.sleep(3);

        // Reschedule the scheduler to run faster. Works.
        // TODO: Think about how actually to test this.

        schedule.reschedule(2, 5, TimeUnit.MILLISECONDS);

        TimeUnit.MILLISECONDS.sleep(20);

    }
}
