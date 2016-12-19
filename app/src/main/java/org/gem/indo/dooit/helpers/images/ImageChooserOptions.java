package org.gem.indo.dooit.helpers.images;

import android.content.Context;

import org.gem.indo.dooit.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Wimpie Victor on 2016/12/19.
 */

public enum ImageChooserOptions {
    CAMERA(0, R.string.label_take_picture),
    GALLERY(1, R.string.label_select_gallery),
    CANCEL(2, R.string.label_cancel);

    private static Map<Integer, ImageChooserOptions> map = new HashMap();

    static {
        for (ImageChooserOptions option : values())
            map.put(option.getId(), option);
    }

    private int id;
    private int textRes;

    ImageChooserOptions(int id, int textRes) {
        this.id = id;
        this.textRes = textRes;
    }

    public int getId() {
        return id;
    }

    public int getTextRes() {
        return textRes;
    }

    public static ImageChooserOptions valueOf(int id) {
        return map.get(id);
    }

    public static CharSequence[] createMenuItems(Context context) {
        CharSequence[] items = new CharSequence[values().length];
        for (ImageChooserOptions option : values())
            items[option.getId()] = context.getString(option.getTextRes());
        return items;
    }
}
