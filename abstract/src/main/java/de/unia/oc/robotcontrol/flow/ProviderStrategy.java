/* 2016 */
package de.unia.oc.robotcontrol.flow;

public class ProviderStrategy {

    public static final class TimedProviderStrategy extends ProviderStrategy
    {
        private long timeMs;

        private TimedProviderStrategy(long timeMs) {
            this.timeMs = timeMs;
        }

        public long getTime() {
            return timeMs;
        }

        public synchronized void setTime(long time) {
            this.timeMs = time;
        }
    }

    public static final class OnDemandProviderStrategy extends ProviderStrategy
    {
        private OnDemandProviderStrategy() { }
    }

    private ProviderStrategy() {}

    public static TimedProviderStrategy timed(long timeMs) {
        return new TimedProviderStrategy(timeMs);
    }

    public static OnDemandProviderStrategy onDemand() {
        return new OnDemandProviderStrategy();
    }

}
