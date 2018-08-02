/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.FluxProcessor;
import reactor.core.scheduler.Scheduler;

import java.util.concurrent.Executor;

public abstract class EmittingMulticast<Topic extends Object, Value extends Object>
        extends MapMulticast<Topic, Value>  {

    protected EmittingMulticast(Scheduler scheduler) {
        super(scheduler);
    }

    protected EmittingMulticast(Executor executor) {
        super(executor);
    }

    @Override
    protected FluxProcessor<Value, Value> createMainProcessor() {
        return EmitterProcessor.create();
    }

    @Override
    protected FluxProcessor<Value, Value> createTopicProcessor() {
        return EmitterProcessor.create();
    }
}
