package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.flow.PassiveInFlow;

import java.util.function.Consumer;

public class CallbackMessageRecipient implements MessageRecipient<Message> {

    private final PassiveInFlow<Message> inFlow;
    private final Consumer<Message> callback;

    public CallbackMessageRecipient(Consumer<Message> callback) {
        this.inFlow = PassiveInFlow.createUnbuffered(callback);
        this.callback = callback;
    }

    @Override
    public PassiveInFlow<Message> inFlow() {
        return inFlow;
    }
}
