/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.concurrent;

import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public interface Terminable {

    boolean isTerminated();

    void terminate();

    static Terminable create(Supplier<Boolean> isTerminated, Runnable terminate) {
        return new Terminable() {
            @Override
            public boolean isTerminated() {
                return isTerminated.get();
            }

            @Override
            public void terminate() {
                if (!isTerminated()) terminate.run();
            }
        };
    }

    interface TimedTerminable extends Terminable {

        boolean terminate(@Positive long time, @NonNull TimeUnit unit) throws InterruptedException;

    }

    final class ConstantlyTerminated implements TimedTerminable {

        public static ConstantlyTerminated create() {
            return new ConstantlyTerminated();
        }

        @Override
        public boolean isTerminated() {
            return true;
        }

        @Override
        public void terminate() {}

        @Override
        public boolean terminate(@Positive long time, @NonNull TimeUnit unit) {
            return true;
        }
    }

}
