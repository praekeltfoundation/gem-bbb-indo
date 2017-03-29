package org.gem.indo.dooit.views.profile.enums;

import android.support.annotation.Nullable;
import android.util.SparseArray;

import org.gem.indo.dooit.R;

/**
 * Created by Wimpie Victor on 2017/03/28.
 */

public enum ProfileTabPosition {
    ACHIEVEMENTS(0, R.string.profile_tab_achievements),
    BUDGET(1, R.string.profile_tab_budget);

    static SparseArray<ProfileTabPosition> map = new SparseArray<>();

    static {
        for (ProfileTabPosition pos : ProfileTabPosition.values())
            map.put(pos.id, pos);
    }

    int id;
    int textRes;

    ProfileTabPosition(int id, int textRes) {
        this.id = id;
        this.textRes = textRes;
    }

    ////////
    // ID //
    ////////

    public int getId() {
        return id;
    }

    @Nullable
    static public ProfileTabPosition byId(int id) {
        return map.get(id);
    }

    ////////////////
    // Title Text //
    ////////////////

    public int getTitleTextRes() {
        return textRes;
    }
}
