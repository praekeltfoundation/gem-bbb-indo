package org.gem.indo.dooit.helpers.bot.param;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Wimpie Victor on 2016/12/07.
 */

public class ParamMatch {

    private static final String TAG = ParamMatch.class.getName();

    private String original;
    private String forFormat;
    private String processed;
    private ParamArg[] args;
    private Map<String, ParamArg> map = new HashMap<>();

    /**
     * @param original
     * @param forFormat The original string with the matched args replaced with %s
     * @param args
     */
    ParamMatch(String original, String forFormat, ParamArg[] args) {
        this.original = original;
        this.forFormat = forFormat;
        this.processed = null;
        this.args = args;

        for (ParamArg arg : args)
            map.put(arg.getKey(), arg);
    }

    public boolean isProcessed() {
        return processed != null;
    }

    /**
     * Populate the string with arguments. The arguments must be provided in the same order that
     * they appear in the string.
     *
     * @param objects
     */
    public String populate(Object[] objects) {
        // TODO: Support populating using a map for in case the translations have the arguments in different orders
        if (isProcessed())
            throw new RuntimeException(TAG + " is already processed.");

        if (args.length != objects.length)
            throw new RuntimeException(
                    String.format("%s received an unexpected number of objects. Expected %d, received %d",
                    TAG, args.length, objects.length));

        processed = String.format(forFormat, objects);
        return processed;
    }
}
