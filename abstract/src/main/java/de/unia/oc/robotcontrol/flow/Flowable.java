/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import de.unia.oc.robotcontrol.concurrent.Concurrent;

/**
 * Flowable is the super-interface for everything pertaining
 * to the flow of data through the program.
 *
 * Important extending interfaces include:
 * - {@link FlowableSource} a source (origin) of data
 * - {@link FlowableSink} a sink (final destination) for data
 * - {@link FlowableTransformer} a transformer of data
 *      (classically called 'filter' in the pipes & filters model)
 * - {@link FlowableProcessor} a processor of data, which acts as both sink and source,
 *      and may do more than a transformer in that it can break / branch the data pipeline
 * - {@link FlowableMulticast} a special processor used for multicasting data
 */
public interface Flowable extends Concurrent {

    /**
     * Returns the {@link FlowStrategy} of this Flow Element,
     * that is, any transformations applied and the nature of
     * their effect as described by {@link FlowStrategyType}
     * in {@link FlowStrategy#getType()}.
     *
     * @return an instance of {@link FlowStrategy}
     */
    FlowStrategy<?, ?> getFlowStrategy();

    @Override
    default ConcurrencyType getConcurrencyType() {
        // Let the threading be handled by the system by default.
        return ConcurrencyType.EXTERNAL;
    }
}
