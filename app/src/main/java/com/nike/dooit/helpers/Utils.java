package com.nike.dooit.helpers;

import android.content.res.Resources;

/**
 * Created by herman on 2016/11/12.
 */

public class Utils {

    public static float dpTopx(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return  dp * scale + 0.5f;
    }

    public static float spTopx(Resources resources, float sp){
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }
}
