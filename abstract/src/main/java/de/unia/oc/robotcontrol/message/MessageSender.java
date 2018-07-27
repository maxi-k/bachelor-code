/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

import de.unia.oc.robotcontrol.flow.old.OutFlowElement;

/**
 * Generic interface for some element that can send Messages
 * @param <T>
 */
public interface MessageSender<T extends Message> extends OutFlowElement {

}
