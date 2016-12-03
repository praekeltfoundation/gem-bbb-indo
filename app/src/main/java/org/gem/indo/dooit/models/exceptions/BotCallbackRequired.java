package org.gem.indo.dooit.models.exceptions;

/**
 * Created by Wimpie Victor on 2016/12/03.
 */

public class BotCallbackRequired extends RuntimeException {
    public BotCallbackRequired(String message) {
        super(message);
    }
}
