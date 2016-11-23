package org.gem.indo.dooit.views.helpers.activity;

/**
 * Created by Bernhard Müller on 9/29/2016.
 */

public class IncompleteBuilderException extends IllegalArgumentException {

    /**
     * Constructs an IncompleteBuilderException with no detail message.
     * A detail message is a String that describes this particular
     * exception.
     */
    public IncompleteBuilderException() {
        super();
    }

    /**
     * Constructs an IncompleteBuilderException with the specified
     * detail message.  A detail message is a String that describes
     * this particular exception.
     *
     * @param msg the detail message.
     */
    public IncompleteBuilderException(String msg) {
        super(msg);
    }
}
