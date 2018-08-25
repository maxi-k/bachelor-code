/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import de.unia.oc.robotcontrol.util.Tuple;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class PairingFlowStrategy<T extends Object> extends ReducerFlowStrategy<T, Tuple<T, T>> {

    private PairingFlowStrategy(Supplier<Tuple<T, T>> initialValueSupplier) {
        super(initialValueSupplier, (last, next) -> Tuple.create(last.second, next));
    }

    public static <T extends Object> PairingFlowStrategy<T> create(Supplier<Tuple<T, T>> initialValueSupplier) {
        return new PairingFlowStrategy<>(initialValueSupplier);
    }

}
