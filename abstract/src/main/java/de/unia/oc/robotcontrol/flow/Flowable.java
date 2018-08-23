/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import de.unia.oc.robotcontrol.concurrent.Concurrent;

public interface Flowable extends Concurrent {

    FlowStrategy<?, ?> getFlowStrategy();

    @Override
    default ConcurrencyType getConcurrencyType() {
        // Let the threading be handled by the system by default.
        return ConcurrencyType.EXTERNAL;
    }
}
