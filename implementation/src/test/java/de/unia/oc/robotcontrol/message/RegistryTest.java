/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.coding.CharEncoding;
import de.unia.oc.robotcontrol.coding.CodingContext;
import de.unia.oc.robotcontrol.util.Builder;
import de.unia.oc.robotcontrol.util.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RegistryTest {

    @Test
    void registeresAndEncodes() {

        MessageIdentifier<Character> id =
                Builder.start(CodingContext.NATIVE)
                        .map(CharEncoding::new)
                        .map(SimpleMessageIdentifier::new)
                        .get();

        MessageTypeRegistry<Character> registry = new MapMessageTypeRegistry<>(id);

        SingleValueMessageType<Character>  k = Builder.start(CodingContext.NATIVE)
                .map(CharEncoding::new)
                .map(SingleValueMessageType::new)
                .get();

        SingleValueMessageType<Character> m = Builder.start(CodingContext.NATIVE)
                .map(CharEncoding::new)
                .map(SingleValueMessageType::new)
                .get();

        registry.register('k', k);
        registry.register('m', m);

        Assertions.assertEquals(registry.getIdentifier(), id);

        Assertions.assertTrue(registry.getValueFor('k').isPresent());
        Assertions.assertTrue(registry.getValueFor('m').isPresent());

        Assertions.assertEquals(registry.getValueFor('k').get(), k);
        Assertions.assertEquals(registry.getValueFor('m').get(), m);

        Assertions.assertTrue(registry.getKeyFor(k).isPresent());
        Assertions.assertTrue(registry.getKeyFor(m).isPresent());

        Assertions.assertEquals((char) registry.getKeyFor(k).get(), 'k');
        Assertions.assertEquals((char) registry.getKeyFor(m).get(), 'm');

        Assertions.assertArrayEquals(
                registry.encode(k.produce('a')),
                id.encode(Tuple.create('k', k.encode(k.produce('a'))))
        );

        SingleValueMessage<Character> msg = m.produce('b');
        Assertions.assertEquals(
                registry.decode(registry.encode(msg)).getType(),
                m
        );

        Assertions.assertEquals(
                registry.decode(registry.encode(msg)).toString(),
                msg.toString()
        );
    }
}
