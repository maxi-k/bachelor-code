/* %FILE_TEMPLATE_TEXT% */
/**
 * General purpose module for encoding Objects to bytes and decoding them.
 * Is presumed to be used for encoding messages ({@link de.unia.oc.robotcontrol.message.Message})
 * to be sent to external devices, as well as decoding messages received from external devices.
 *
 * The main abstraction provided by this package is {@link de.unia.oc.robotcontrol.coding.Encoding},
 * which serves the aforementioned purpose. It also has a more strict version,
 * {@link de.unia.oc.robotcontrol.coding.FixedEncoding}, which guarantees a constant number of bytes.
 * Distinguishing between different Environments and Contexts for the target devices can be done using
 * {@link de.unia.oc.robotcontrol.coding.CodingContext}.
 *
 * @since 1.0
 * @author Maximilian Kuschewski
 */
package de.unia.oc.robotcontrol.coding;
