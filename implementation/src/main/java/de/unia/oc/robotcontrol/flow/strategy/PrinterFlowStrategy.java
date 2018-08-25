/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

import com.sun.javafx.tools.ant.Callback;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

public class PrinterFlowStrategy<T extends Object> extends CallbackFlowStrategy<T> {

    private PrinterFlowStrategy() {
        super(System.out::println);
    }

    public static <T> PrinterFlowStrategy<T> create() {
        return new PrinterFlowStrategy<>();
    }

}
