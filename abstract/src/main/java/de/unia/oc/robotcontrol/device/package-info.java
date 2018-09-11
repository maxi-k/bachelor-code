/* %FILE_TEMPLATE_TEXT% */
/**
 * Module providing abstractions for devices, that is, object oriented adapters
 * that connect to external hardware.
 *
 * A device (described by {@link de.unia.oc.robotcontrol.device.Device}
 * can receive messages (presumably to be sent as actuator commands) and
 * send messages (presumably sensory data), as described by the
 * {@link de.unia.oc.robotcontrol.flow.FlowableProcessor} abstraction.
 *
 * Devices also have to provide a unique id {@link java.util.UUID},
 * so that they can be identified across instances of this software, for example
 * in a multi-agent system.
 *
 * The {@link de.unia.oc.robotcontrol.device.DeviceTarget} interface is used to
 * declare some object that is meant for a specific device, or originated from it,
 * such as messages ({@link de.unia.oc.robotcontrol.message.SensorMessage},
 * {@link de.unia.oc.robotcontrol.message.ActuatorMessage})
 *
 * @author Maximilian Kuschewski
 * @since 1.0
 */
package de.unia.oc.robotcontrol.device;