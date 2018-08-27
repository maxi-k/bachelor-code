/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.message;

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
