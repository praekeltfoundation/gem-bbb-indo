package org.gem.indo.dooit.helpers.bot.param;

/**
 * Created by Wimpie Victor on 2016/12/07.
 */

public class ParamArg {

    private String key;
    private String match;

    ParamArg(String key, String match) {
        this.key = key;
        this.match = match;
    }

    public String getKey() {
        return key;
    }

    public String getMatch() {
        return match;
    }
}