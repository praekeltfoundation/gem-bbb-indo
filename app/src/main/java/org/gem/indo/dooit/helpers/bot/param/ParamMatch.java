package org.gem.indo.dooit.helpers.bot.param;

import org.gem.indo.dooit.helpers.bot.param.exceptions.AlreadyProcessedError;
import org.gem.indo.dooit.helpers.bot.param.exceptions.InvalidArgCountError;

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

    public boolean isEmpty() {
        return args.length <= 0;
    }

    public int count() {
        return args.length;
    }

    /**
     * Populate the string with arguments. The arguments must be provided in the same order that
     * they appear in the string.
     *
     * @param objects
     */
    public String process(Object[] objects) {
        if (isProcessed())
            throw new AlreadyProcessedError(TAG + " is already processed.");

        if (args.length != objects.length)
            throw new InvalidArgCountError(
                    String.format("%s received an unexpected number of objects. Expected %d, received %d",
                    TAG, args.length, objects.length));

        processed = String.format(forFormat, objects);
        return processed;
    }

    /**
     * Alternative process to support cases where arguments are located in different places. For
     * localisation.
     *
     * @param mapping
     * @return
     */
    public String process(Map<String, Object> mapping) {
        Object[] objects = new Object[args.length];
        for (int i = 0; i < args.length; i++)
            objects[i] = mapping.get(args[i].getKey());
        return process(objects);
    }

    public String getProcessed() {
        return processed;
    }

    public ParamArg[] getArgs() {
        return args;
    }
}
