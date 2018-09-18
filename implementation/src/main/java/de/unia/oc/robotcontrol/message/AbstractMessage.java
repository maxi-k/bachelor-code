/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

/**
 * Abstract super-class for messages which handles setting
 * the timestamp used by {@link #getCreationTime()} on
 * creation of the object.
 *
 * @param <T> the concrete type of the message this represents
 */
public abstract class AbstractMessage<T extends Message> implements Message<T>{

    private long creationTime;

    protected AbstractMessage() {
       this.creationTime = System.currentTimeMillis();
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

}
