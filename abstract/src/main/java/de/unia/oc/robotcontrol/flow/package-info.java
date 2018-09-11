/* %FILE_TEMPLATE_TEXT% */
/**
 * Module providing abstractions for establishing a Flow of data
 * through the system, using the reactive-streams specification
 * {@link org.reactivestreams}.
 *
 * The topmost interface is {@link de.unia.oc.robotcontrol.flow.Flowable},
 * and it is extended by interfaces describing the different possible types
 * of system elements:
 * - Data Sources: {@link de.unia.oc.robotcontrol.flow.FlowableSource}
 * - Data Sinks: {@link de.unia.oc.robotcontrol.flow.FlowableSink}
 * - Data Processors: {@link de.unia.oc.robotcontrol.flow.FlowableProcessor},
 *      which act as both data recipient and sender / source and sink.
 * - Data Transformers: {@link de.unia.oc.robotcontrol.flow.FlowableTransformer},
 *      which are like processors, but only for "on-the-line" transformations of data
 * - Flow Branchers: {@link de.unia.oc.robotcontrol.flow.FlowableMulticast},
 *      which multicast values based on topic, thus "branching" the flow of data.
 *
 * Each {@link de.unia.oc.robotcontrol.flow.Flowable} has to define a
 * {@link de.unia.oc.robotcontrol.flow.FlowStrategy}, which explicitly describes
 * how the flow of data is changed by the Flowable.
 * This change is categorized by {@link de.unia.oc.robotcontrol.flow.FlowStrategyType}.
 *
 * @author Maximilian Kuschewski
 * @since 1.0
 */
package de.unia.oc.robotcontrol.flow;