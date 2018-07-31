/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.coding.CharEncoding;
import de.unia.oc.robotcontrol.coding.CodingContext;
import de.unia.oc.robotcontrol.coding.IntegerEncoding;
import de.unia.oc.robotcontrol.util.Builder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DispatcherTest {

    @Test
    void dispatchesCorrectly() {

        SingleValueMessageType<Integer> msgType1 = Builder.start(CodingContext.NATIVE)
                .map(IntegerEncoding::new)
                .map(SingleValueMessageType::new)
                .get();

        SingleValueMessageType<Character> msgType2 = Builder.start(CodingContext.NATIVE)
                .map(CharEncoding::new)
                .map(SingleValueMessageType::new)
                .get();

        MessageRecipient rec1 = new CallbackMessageRecipient((m) -> {
            // System.out.println("Received Message for Type 1: " + m.toString());
            Assertions.assertSame(m.getType(), msgType1);
        });

        MessageRecipient rec2 = new CallbackMessageRecipient((m) ->{
            // System.out.println("Received Message for Type 2: " + m.toString());
            Assertions.assertSame(m.getType(), msgType2);
        });

        Executor exec = Executors.newSingleThreadExecutor();
        MessageMulticaster dispatcher = Messaging.createDispatcher(exec);

        dispatcher.register(msgType1, rec1);
        dispatcher.register(msgType2, rec2);

        dispatcher.dispatch(msgType1.produce(10));
        dispatcher.dispatch(msgType2.produce('a'));

        long time = System.currentTimeMillis();
        dispatcher.dispatch(msgType1.produce(42));
        dispatcher.dispatch(msgType2.produce('b'));
        dispatcher.dispatch(msgType2.produce('c'));
        dispatcher.dispatch(msgType2.produce('d'));
        dispatcher.dispatch(msgType1.produce(10));
        dispatcher.dispatch(msgType1.produce(1000));
        // System.out.println(System.currentTimeMillis() - time);
        Assertions.assertTrue(System.currentTimeMillis() - time < 10);
    }
}
