/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow.strategy;

public class PrinterFlowStrategy<T extends Object> extends CallbackFlowStrategy<T> {

    private PrinterFlowStrategy() {
        super(System.out::println);
    }

    public static <T> PrinterFlowStrategy<T> create() {
        return new PrinterFlowStrategy<>();
    }

}
