/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.coding.*;

public abstract class ArduinoMessageType<M extends Message> implements MessageType<M> {

    private static CodingContext context = CodingContext.ARDUINO;

    private ArduinoMessageType() {}

    public static class SpeedCommand extends ArduinoMessageType<SpeedCmdMessage> {

        private final ListEncoding<Integer> answerEncoding;
        private final Encoding<Character> cmdEncoding;

        private static SpeedCommand _instance;

        private SpeedCommand() {
            this.answerEncoding = new ListEncoding<>(new IntegerEncoding(), 3)
                    .withContext(context);
            this.cmdEncoding = new CharEncoding().withContext(context);
        }

        public static SpeedCommand instance() {
            if (_instance == null) {
                _instance = new SpeedCommand();
            }
            return _instance;
        }

        @Override
        public CodingContext getContext() {
            return context;
        }

        @Override
        public byte[] encode(SpeedCmdMessage object) throws IllegalArgumentException {
            return cmdEncoding.encode(object.command);
        }

        @Override
        public SpeedCmdMessage decode(byte[] raw) throws IllegalArgumentException {
            return new SpeedCmdMessage(answerEncoding.decode(raw));
        }
    }
}
