/* %FILE_TEMPLATE_TEXT% */
/**
 * Parent package for everything that is part of the
 * observer/controller robot-control framework. The system
 * is divided into eight modules, which are part of the eight
 * packages:
 *
 * - coding: General Purpose encoding & decoding, presumably for communication with devices
 * - concurrent: Concurrency and scheduling
 * - device: Connection with external devices, which presumably provide sensors and actuators
 * - flow: Asynchronous communication between different parts of the system using reactive-streams
 * - message: Communication with physical devices and inter-system, using the coding module
 * - oc: Implements the Observer / Controller architecture.
 * - util: General-Purpose utilities
 * - visualization: Graphical runtime visualization of data and system metrics
 *
 * @author Maximilian Kuschewski
 * @since 1.0
 */
package de.uni.oc.robotcontrol;
