package org.gem.indo.dooit;

import org.gem.indo.dooit.helpers.bot.param.ParamMatch;
import org.gem.indo.dooit.helpers.bot.param.ParamParser;
import org.gem.indo.dooit.helpers.bot.param.exceptions.AlreadyProcessedError;
import org.gem.indo.dooit.helpers.bot.param.exceptions.InvalidArgCountError;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by Wimpie Victor on 2017/01/03.
 */

public class ParamParseUnitTest {

    @Test
    public void parse_basic() throws Exception {
        ParamMatch match = ParamParser.parse("$(one) $(two) $(three)");

        assertEquals(3, match.count());
    }

    @Test
    public void process_objectArray() throws Exception {
        ParamMatch match = ParamParser.parse("$(one) $(two) $(three)");
        Object[] objects = new Object[]{1, "2", 3};

        assertEquals("1 2 3", match.process(objects));
    }

    @Test(expected = InvalidArgCountError.class)
    public void process_objectArray_missingArgs() throws Exception {
        ParamMatch match = ParamParser.parse("$(one) $(two) $(three)");
        Object[] objects = new Object[]{1, 2};

        match.process(objects);
    }

    @Test
    public void process_map() throws Exception {
        ParamMatch match = ParamParser.parse("$(one) $(two) $(three)");
        Map<String, Object> map = new HashMap<>();
        map.put("one", 1);
        map.put("two", "2");
        map.put("three", 3);

        assertEquals("1 2 3", match.process(map));
    }

    @Test
    public void process_map_missingArgs() throws Exception {
        ParamMatch match = ParamParser.parse("$(one) $(two) $(three)");
        Map<String, Object> map = new HashMap<>();
        map.put("one", 1);
        map.put("three", 3);

        assertEquals("1 null 3", match.process(map));
    }

    @Test(expected = AlreadyProcessedError.class)
    public void process_alreadyProcessed() throws Exception {
        ParamMatch match = ParamParser.parse("$(one) $(two) $(three)");
        Object[] objects = new Object[]{1, 2, 3};
        match.process(objects);
        match.process(objects);
    }

    /**
     * Test to verify that a String which contains `%s` doesn't break processing
     *
     * @throws Exception
     */
    @Test
    public void process_stringFormatSymbols() throws Exception {
        ParamMatch match = ParamParser.parse("$(one) $(two) $(three) %s %d");
        Object[] objects = new Object[]{1, 2, 3};

        assertEquals("1 2 3 %s %d", match.process(objects));
    }
}
