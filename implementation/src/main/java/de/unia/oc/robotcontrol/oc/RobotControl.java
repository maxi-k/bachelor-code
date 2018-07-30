/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.oc;

import de.unia.oc.robotcontrol.device.Device;
import de.unia.oc.robotcontrol.flow.FlowStrategy;
import de.unia.oc.robotcontrol.message.Message;
import de.unia.oc.robotcontrol.message.MessageType;

import java.util.HashMap;
import java.util.List;

public class RobotControl<
        WoldState,
        ControllerAction,
        ObserverModel extends ObservationModel<WoldState>,
        OCObserver extends Observer<WoldState, ObserverModel>,
        OCController extends Controller<WoldState, ObserverModel, ControllerAction>> {

    RobotControl(OCObserver observer,
                 OCController controller,
                 List<Device> devices,
                 HashMap<MessageType<? extends Message>, FlowStrategy> messageStrategies,
                 HashMap<Device<? extends Message, ? extends Message>, List<MessageType<? extends Message>>> deviceMap) {

    }

    public static final class Builder {

    }

    public static Builder build() {

    }
}
