/* 2016 */
package de.unia.oc.robotcontrol.coding;

public interface Contextual {

    /**
     *
     * @return The context with which encoding/decoding takes place.
     */
    CodingContext getContext();

    Contextual withContext(CodingContext context);
}
