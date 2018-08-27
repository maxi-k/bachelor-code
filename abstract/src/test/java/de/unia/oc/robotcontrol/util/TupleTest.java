/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.util;

import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TupleTest {

    @Test
    void valueBasedEquality() {
        assertEquals(
                Tuple.create("Hello", "World"),
                Tuple.create("Hello", "World")
        );
    }

    @Test
    void mapsCorrectly() {
        assertEquals(
                Tuple.create(42, 420),
                Tuple.create(41, 420).mapFirst((i) -> i + 1)
        );

        Function<String, Character> second = (s) -> s.toCharArray()[1];
        assertEquals(
                Tuple.create('e', 'T'),
                Tuple.create("Test", "String").map(second, second.andThen(Character::toUpperCase))
        );
    }

    @Test
    void joinsCorrectly() {
       assertEquals(
               "Saul Goodman",
               Tuple.create("Saul", "Goodman").joinWith((f, s) -> f + " " + s)
       );
    }

    @Test
    void printsCorrectly() {
        assertEquals(
                "Tuple(Hal, 9001)",
                Tuple.create("Hal", 9001).toString()
        );
    }
}
