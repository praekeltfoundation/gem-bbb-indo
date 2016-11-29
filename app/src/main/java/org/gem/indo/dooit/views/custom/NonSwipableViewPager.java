package org.gem.indo.dooit.views.custom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * Created by Rudolph Jacobs on 2016-11-28.
 */

public class NonSwipableViewPager extends ViewPager {
    public NonSwipableViewPager(Context context) {
        super(context);
        setSmoothScroller();
    }

    public NonSwipableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSmoothScroller();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    private void setSmoothScroller() {
        /* Fixes tab animation on API 19 */
        try {
            Class<?> viewpager = ViewPager.class;
            Field scroller = viewpager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            scroller.set(this, new SmoothScroller(getContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class SmoothScroller extends Scroller {
        private SmoothScroller(Context context) {
            super(context);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, 350 /* 1 s */);
        }
    }
}
