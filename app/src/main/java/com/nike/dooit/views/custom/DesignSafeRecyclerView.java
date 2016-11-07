package com.nike.dooit.views.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public class DesignSafeRecyclerView extends RecyclerView {
    public DesignSafeRecyclerView(Context context) {
        super(context);
    }

    public DesignSafeRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DesignSafeRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onAttachedToWindow() {
        if (!isInEditMode())
            super.onAttachedToWindow();
    }
}
