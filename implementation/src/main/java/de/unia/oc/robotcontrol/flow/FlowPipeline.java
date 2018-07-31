/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import de.unia.oc.robotcontrol.util.Dispatcher;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

public class FlowPipeline<Input, Output> {

    public static final class Builder<Input, Output> {

        private Flux<Input> inputPublisher;
        private Flux<Output> outputPublisher;

        public Builder<Input, Output> addSource(Publisher<Input> publisher) {
            if (inputPublisher == null) {
                this.inputPublisher = Flux.from(publisher);
            } else {
                this.inputPublisher = Flux.merge(this.inputPublisher, publisher);
            }
            return this;
        }

        public FlowPipeline build() {
            // return new FlowPipeline();
            return null;
        }

    }

    public static Builder build() {
        return new Builder();
    }
}
