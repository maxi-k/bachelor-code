/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import org.reactivestreams.Publisher;

import java.util.function.Function;

public interface FlowStrategy<T extends Object, R extends Object>
        extends PublisherTransformer<T, R> {

    String PROPERTY_NAME = "flowStrategy";

    FlowStrategyType getType();

    default <S extends Object> FlowStrategy<T, S> chain(Function<? super Publisher<R>, ? extends Publisher<S>> other) {
        FlowStrategy<T, R> self = this;
        return new FlowStrategy<T, S>() {
            @Override
            public FlowStrategyType getType() {
                return self.getType();
            }

            @Override
            public Publisher<S> apply(Publisher<T> next) {
                return self.andThen(other).apply(next);
            }
        };
    }
}
