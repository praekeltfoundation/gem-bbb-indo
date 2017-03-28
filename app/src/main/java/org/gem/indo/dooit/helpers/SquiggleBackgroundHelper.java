package org.gem.indo.dooit.helpers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.views.custom.TilingDrawable;

/**
 * Created by Rudolph Jacobs on 2016-11-24.
 */

public class SquiggleBackgroundHelper {
    public static void setBackground(Context context, int bg, int fg, View background) {
        background.setBackground(getBackground(context, bg, fg, false));
    }

    public static void setBackground(Context context, int bg, int fg, View background, boolean mutable) {
        background.setBackground(getBackground(context, bg, fg, mutable));
    }

    /**
     * @param context Current context
     * @param bg      Color resource of background
     * @param fg      Color resource of foreground
     * @param mutable When false, the default behaviour of drawables are to share state between
     *                instances. When true, the foreground squiggle will be mutable, and setting its
     *                color will not affect other squiggles.
     * @return The tiling background drawable.
     */
    private static TilingDrawable getBackground(Context context, int bg, int fg, boolean mutable) {
        ShapeDrawable back = new ShapeDrawable();
        back.getPaint().setColor(ContextCompat.getColor(context, bg));
        Drawable fore = ContextCompat.getDrawable(context, R.drawable.bkg_clipped);
        if (mutable)
            fore = fore.mutate();
        DrawableCompat.setTint(fore, ContextCompat.getColor(context, fg));
        LayerDrawable layers = new LayerDrawable(new Drawable[]{back, fore});
        return new TilingDrawable(layers);
    }
}
