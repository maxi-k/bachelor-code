/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import de.unia.oc.robotcontrol.flow.strategy.IgnoringFlowStrategy;
import de.unia.oc.robotcontrol.flow.strategy.TransparentFlowStrategy;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import reactor.core.scheduler.Scheduler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class ConnectingMulticast<Topic extends Object, Value extends Object>
    extends ReemittingMulticast<Topic, Publisher<Value>>
        {

            protected ConnectingMulticast(Scheduler scheduler) {
                super(scheduler);
            }
        }
