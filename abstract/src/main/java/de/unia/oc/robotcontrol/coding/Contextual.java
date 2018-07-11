/* %FILE_TEMPLATE_TEXT% */
package de.unia.oc.robotcontrol.coding;

/**
 * Interface which describes something that is dependent
 * on the (device) context it is run in
 */
public interface Contextual {

    /**
     *
     * @return The context with which encoding/decoding takes place.
     */
    CodingContext getContext();

    /**
     * Gives back an instance of Contextual which uses the given context.
     * It is advised that this be implemented immutably so as to not cause thread-safety problems.
     *
     * @param context the context to be used
     * @return an instance of Contextual which uses the given context
     */
    Contextual withContext(CodingContext context);
}
