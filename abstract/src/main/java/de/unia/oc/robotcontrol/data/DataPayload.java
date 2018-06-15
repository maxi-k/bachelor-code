/* 2016 */
package de.unia.oc.robotcontrol.data;

public interface DataPayload<T> {

    long getCreationTime();

    T getData();
}
