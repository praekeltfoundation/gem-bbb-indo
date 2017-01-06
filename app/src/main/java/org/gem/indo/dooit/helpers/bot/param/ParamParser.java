package org.gem.indo.dooit.helpers.bot.param;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Wimpie Victor on 2016/12/06.
 */

public class ParamParser {

    private static final Pattern paramPattern = Pattern.compile("(\\$\\(([a-zA-Z0-9-_\\.]+)\\))");

    private ParamParser() {

    }

    public static ParamMatch parse(String text) {
        // Escape percentage to prevent MissingFormatArgumentException later in ParamMatch
        text = text.replace("%", "%%");
        Matcher matcher = paramPattern.matcher(text);
        List<ParamArg> matches = new ArrayList<>();
        while (matcher.find()) {
            matches.add(new ParamArg(
                    matcher.group(2),
                    matcher.group(0)
            ));
        }
        ParamArg[] arr = new ParamArg[matches.size()];
        return new ParamMatch(text, matcher.replaceAll("%s"), matches.toArray(arr));
    }
}
