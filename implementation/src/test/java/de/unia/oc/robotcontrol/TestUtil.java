/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol;

import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;

public class TestUtil {

    public static void assertInRange(long actual, long expected, long hysteresis) {
        try {
            Assertions.assertTrue(
                    actual > (expected - hysteresis) &&
                            actual < (expected + hysteresis)
            );
        } catch(AssertionFailedError e) {
            Assertions.fail(new AssertionFailedError(
                    "Assertion Failed: Value was not in range: " +
                            expected  + " +/- " + hysteresis + " vs. " + actual,
                    expected  + " +/- " + hysteresis,
                    actual
            ));
        }
    }
}
