/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.coding;

import de.unia.oc.robotcontrol.util.Bijection;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BijectiveTransformationTest {

    @Test
    void isStackable() {

        Bijection<Character, Integer> top = Bijection.create(
                (c) -> (int) c,
                (i) -> (char) i.intValue()
        );

        Bijection<Integer, String> bottom = Bijection.create(
                Object::toString,
                Integer::parseInt
        );


        char testChar = 'c';
        int testInt = 99;
        String testString = "99";

        assertEquals(testChar, top.decode(testInt).charValue());
        assertEquals(testInt, top.encode(testChar).intValue());

        assertEquals(testString, bottom.encode(testInt));
        assertEquals(testInt, bottom.decode(testString).intValue());

        List<Bijection<Character, String>> transforms = new ArrayList<>(2);

        transforms.add(bottom.stack(top));
        transforms.add(top.supplement(bottom));
        transforms.add(bottom.stack(top).wrap(makeIdentity('a'), makeIdentity("a")));
        transforms.add(top.supplement(bottom).wrap(makeIdentity('a'), makeIdentity("a")));
        transforms.add(makeIdentity(65).wrap(top, bottom));

        for (Bijection<Character, String> whole : transforms) {
            assertEquals(testString, whole.encode(testChar));
            assertEquals(testChar, whole.decode(testString).charValue());
        }
    }

    /**
     * Returns an Bijection instance that does nothing. Requires unused type-parameter
     * to prevent type erasure in call cases.
     */
    private <A> Bijection<A, A> makeIdentity(A type) {
        return Bijection.create(
                (A a)  -> a,
                (A a) -> a
        );
    }

}