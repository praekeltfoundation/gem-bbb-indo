package org.gem.indo.dooit.helpers.string;

import org.gem.indo.dooit.helpers.strings.StringHelper;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by Wimpie Victor on 2017/02/15.
 */

public class StringHelperTest {

    @Test
    public void isEmpty_null() throws Exception {
        assertTrue(StringHelper.isEmpty(null));
    }

    @Test
    public void isEmpty_empty() throws Exception {
        assertTrue(StringHelper.isEmpty(""));
    }

    @Test
    public void isEmpty_notEmpty() throws Exception {
        assertFalse(StringHelper.isEmpty("foobar"));
    }
}
