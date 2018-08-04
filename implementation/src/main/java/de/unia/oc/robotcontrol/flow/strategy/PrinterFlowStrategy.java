/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

public class PrinterFlowStrategy<T extends Object> extends TransparentFlowStrategy<T> {

    private PrinterFlowStrategy() {

    }

    public static <T> PrinterFlowStrategy<T> create() {
        return new PrinterFlowStrategy<>();
    }

    @Override
    public Publisher<T> apply(Publisher<T> publisher) {
        return Flux
                .from(super.apply(publisher))
                .doOnNext(System.out::println);
    }
}
