/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.util.Tuple;

import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Implementation of {@link FlowStrategy} used for pairing values together
 * into a tuple:
 *
 * {@code v1 ... v2 ... v3 ... v4 -> (v1, v2) ... (v2, v3) ... (v3, v4) }
 *
 * @param <T> the type of object published by the Publisher
 *           which is to be transformed by this flow strategy
 */
public class PairingFlowStrategy<T extends Object> extends ReducerFlowStrategy<T, Tuple<T, T>> {

    /**
     * Create a new instance of {@link PairingFlowStrategy}, using the given
     * supplier to create an initial value for when no two values have been published yet.
     * @param initialValueSupplier the supplier used for generating an initial tuple
     */
    private PairingFlowStrategy(Supplier<Tuple<T, T>> initialValueSupplier) {
        super(initialValueSupplier, (last, next) -> Tuple.create(last.second, next));
    }

    /**
     * Create a new instance of {@link PairingFlowStrategy}.
     * Mirror of {@link #PairingFlowStrategy(Supplier)}
     * @param initialValueSupplier the supplier used for generating an initial tuple
     * @param <T> the type of object received and published by the Publisher
     *           which is to be transformed by this flow strategy
     * @return a new instance of {@link PairingFlowStrategy}
     */
    public static <T extends Object> PairingFlowStrategy<T> create(Supplier<Tuple<T, T>> initialValueSupplier) {
        return new PairingFlowStrategy<>(initialValueSupplier);
    }

}
