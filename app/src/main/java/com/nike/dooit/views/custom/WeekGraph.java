package com.nike.dooit.views.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nike.dooit.R;

import java.util.ArrayList;
import java.util.Locale;


/**
 * TODO: document your custom view class.
 */
public class WeekGraph extends LinearLayout {
    private int mBarWidth = 0, mBarMarginStart = 0, mBarMarginEnd = 0;
    private Drawable mBarDrawable;
    private ArrayList<Float> mValues = new ArrayList<>();
    private Float maxValue = 0f;

    public WeekGraph(Context context) {
        super(context);
        init(null, 0);
    }

    public WeekGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public WeekGraph(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.WeekGraph, defStyle, 0);

        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mBarWidth = a.getDimensionPixelSize(
                R.styleable.WeekGraph_gw_bar_width,
                mBarWidth);
        mBarMarginStart = a.getDimensionPixelSize(
                R.styleable.WeekGraph_gw_bar_marginStart,
                mBarMarginStart);
        mBarMarginEnd = a.getDimensionPixelSize(
                R.styleable.WeekGraph_gw_bar_marginEnd,
                mBarMarginEnd);

        if (a.hasValue(R.styleable.WeekGraph_gw_bar_drawable)) {
            mBarDrawable = a.getDrawable(
                    R.styleable.WeekGraph_gw_bar_drawable);
            mBarDrawable.setCallback(this);
        }

        a.recycle();

        if (isInEditMode()) {
            mValues = new ArrayList<Float>() {{
                add(1908.0f);
                add(5329.0f);
                add(7696.0f);
                add(4389.0f);
                add(4089.0f);
                add(7648.0f);
                add(3788.0f);
                add(6025.0f);
                add(6488.0f);
                add(8907.0f);
                add(6262.0f);
                add(7305.0f);
                add(2209.0f);
                add(2498.0f);
                add(8069.0f);
                add(5342.0f);
            }};
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        createView();
    }

    private void createView() {
        for (int k = 0; k < mValues.size(); k++) {
            if (mValues.get(k) > maxValue)
                maxValue = mValues.get(k);
        }

        for (int i = 0; i < mValues.size(); i++) {
            LinearLayout viewGroup = new LinearLayout(getContext());
            viewGroup.setOrientation(VERTICAL);
            LinearLayout.LayoutParams paramsViewGroup = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            paramsViewGroup.weight = 1.0f;
            paramsViewGroup.gravity = Gravity.BOTTOM;
            paramsViewGroup.setMarginStart(mBarMarginStart);
            paramsViewGroup.setMarginEnd(mBarMarginEnd);
            viewGroup.setLayoutParams(paramsViewGroup);

            View bar = new View(getContext());
            LinearLayout.LayoutParams paramsBar = new LayoutParams(mBarWidth, (int) (getMeasuredHeight() * (mValues.get(i) / maxValue)));
            bar.setLayoutParams(paramsBar);
            bar.setBackground(mBarDrawable);
            viewGroup.addView(bar);

            TextView text = new TextView(getContext());
            text.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams paramsText = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            text.setLayoutParams(paramsText);
            text.setText(String.format(Locale.getDefault(), "%d", i));
            viewGroup.addView(text);

            addView(viewGroup);
        }
    }

    public ArrayList<Float> getValues() {
        return mValues;
    }

    public void setValues(ArrayList<Float> values) {
        this.mValues = values;
        removeAllViews();
    }
}
