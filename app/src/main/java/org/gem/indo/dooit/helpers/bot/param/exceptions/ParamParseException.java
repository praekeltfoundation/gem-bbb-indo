package org.gem.indo.dooit.helpers.bot.param.exceptions;

/**
 * Created by Wimpie Victor on 2017/01/03.
 */

public abstract class ParamParseException extends RuntimeException {
    public ParamParseException(String message) {
        super(message);
    }
}
