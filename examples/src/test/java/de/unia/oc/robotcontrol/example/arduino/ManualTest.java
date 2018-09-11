/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.example.arduino;

import de.unia.oc.robotcontrol.concurrent.Clock;
import de.unia.oc.robotcontrol.concurrent.EmittingClock;
import de.unia.oc.robotcontrol.device.Device;
import de.unia.oc.robotcontrol.example.arduino.device.MockArduino;
import de.unia.oc.robotcontrol.example.arduino.message.ArduinoMessageTypes;
import de.unia.oc.robotcontrol.example.arduino.message.UpdateRequestMessage;
import de.unia.oc.robotcontrol.message.Message;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;

import java.time.Duration;

@Disabled
class ManualTest {

    @Test
    void manualTest() throws InterruptedException {
        final Duration time = Duration.ofMillis(100);

        Clock timer = EmittingClock.create(time);
        Device<Message, Message> device = createDevice();

        device.getClockType().runOn(timer);

        device.asPublisher().subscribe(new BaseSubscriber<Message>() {
            @Override
            protected void hookOnNext(Message value) {
                System.out.println("Received Message " + value + " on Thread " + Thread.currentThread().getName());
            }

            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                System.out.println("onSubscribe! on Thread " + Thread.currentThread().getName());
                subscription.request(Long.MAX_VALUE);
            }
        });

        Thread.sleep(1000);
    }

    private Device<Message, Message> createDevice() {
        return new MockArduino(ArduinoMessageTypes.ENCODING, UpdateRequestMessage::new);
    }

}
