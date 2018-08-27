/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

public abstract class AbstractSensorMessage<T extends SensorMessage>
        extends AbstractDeviceMessage<T>
        implements SensorMessage<T> {
}
