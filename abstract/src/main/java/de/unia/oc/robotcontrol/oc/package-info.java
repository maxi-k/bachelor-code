/* %FILE_TEMPLATE_TEXT% */
/**
 * Module implementing the Organic Computing Observer / Controller architecture.
 * Observer and Controller communicate with each other and the rest of the system using
 * the abstractions defined in the {@link de.unia.oc.robotcontrol.flow} module.
 *
 * The Controller can communicate with the observer using the {@link de.unia.oc.robotcontrol.oc.ObservationModel},
 * as defined by the Observer / Controller pattern. It is advised to design any instances of
 * {@link de.unia.oc.robotcontrol.oc.ObservationModel} immutably, for thread safety. Communicating
 * back to the observer can then be done using
 * {@link de.unia.oc.robotcontrol.oc.Observer#setObservationModel(de.unia.oc.robotcontrol.oc.ObservationModel)}
 *
 * @author Maximilian Kuschewski
 * @since 1.0
 */
package de.unia.oc.robotcontrol.oc;