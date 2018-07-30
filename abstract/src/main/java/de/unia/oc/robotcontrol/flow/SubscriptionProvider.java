/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.flow;

import org.reactivestreams.Subscription;

public interface SubscriptionProvider {

    Subscription asSubscription();
}
