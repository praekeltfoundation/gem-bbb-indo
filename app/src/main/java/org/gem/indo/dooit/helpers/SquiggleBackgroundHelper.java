package org.gem.indo.dooit.helpers;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;

import org.gem.indo.dooit.R;

/**
 * Created by Rudolph Jacobs on 2016-11-24.
 */

public class SquiggleBackgroundHelper {
    public static void setBackground(Context context, int bg, int fg, View background) {
        background.setBackground(getBackground(context, bg, fg));
    }

    public static TilingDrawable getBackground(Context context, int bg, int fg) {
        ShapeDrawable back = new ShapeDrawable();
        back.getPaint().setColor(ContextCompat.getColor(context, bg));
        Drawable fore = ContextCompat.getDrawable(context, R.drawable.bkg_clipped);
        DrawableCompat.setTint(fore, ContextCompat.getColor(context, fg));
        LayerDrawable layers = new LayerDrawable(new Drawable[]{back, fore});
        return new TilingDrawable(layers);
    }
}
