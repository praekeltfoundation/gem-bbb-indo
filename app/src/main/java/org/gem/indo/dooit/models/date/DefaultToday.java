package org.gem.indo.dooit.models.date;

import java.util.Date;

/**
 * Created by Wimpie Victor on 2017/02/20.
 */

public class DefaultToday implements Today {

    @Override
    public Date now() {
        return new Date();
    }
}
