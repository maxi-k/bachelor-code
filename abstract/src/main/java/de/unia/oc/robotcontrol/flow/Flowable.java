/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import de.unia.oc.robotcontrol.concurrent.Clockable;

public interface Flowable extends Clockable {

    FlowStrategy<?, ?> getFlowStrategy();
}
