/* %FILE_TEMPLATE_TEXT% */
/**
 * Module defining the {@link de.unia.oc.robotcontrol.message.Message} abstraction
 * for communicating with devices ({@link de.unia.oc.robotcontrol.device.Device}) and
 * other parts of the system.
 *
 * Each Message has a {@link de.unia.oc.robotcontrol.message.MessageType},
 * which is responsible for encoding and decoding it using the {@link de.unia.oc.robotcontrol.coding.Encoding}
 * interface, and identifies it.
 *
 * As Messages or MessageTypes themselves should not be concerned with how the system
 * differentiates between them on a byte level, and instead should only be concerned with
 * how their own data is encoded, there is a concept of and identifier defined by
 * {@link de.unia.oc.robotcontrol.message.MessageIdentifier} which splits a sequence of bytes
 * into an instance of the identifier and the pure message data. Different identifiers can then be
 * registered using {@link de.unia.oc.robotcontrol.message.MessageTypeRegistry}, which defines
 * an encoding for all message types that are registered in it.
 * Using this mechanic, a general-purpose encoding can be created for all messages the application
 * defines.
 *
 * This module also defines subtypes of the abstractions provided by the flow module
 * ({@link de.unia.oc.robotcontrol.flow}) specifically tailored to system parts which
 * handle subtypes of {@link de.unia.oc.robotcontrol.message.Message}.
 *
 * @author Maximilian Kuschewski
 * @since 1.0
 */
package de.unia.oc.robotcontrol.message;