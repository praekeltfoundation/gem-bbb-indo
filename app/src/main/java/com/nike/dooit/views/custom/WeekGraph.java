package com.nike.dooit.views.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nike.dooit.R;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;


/**
 * TODO: document your custom view class.
 */
public class WeekGraph extends LinearLayout {
    private int mBarWidth = 0, mBarMarginStart = 0, mBarMarginEnd = 0, mBarTextHeight = 0;
    private Drawable mBarDrawable;
    private LinkedHashMap<String, Float> mValues = new LinkedHashMap<>();
    private Float maxValue = 0f;

    int parentWidth, parentHeight;

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
        mBarTextHeight = a.getDimensionPixelSize(
                R.styleable.WeekGraph_gw_bar_textHeight,
                mBarTextHeight);
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
            mValues = new LinkedHashMap() {{
                put("1", 1908.0f);
                put("2", 5329.0f);
                put("3", 7696.0f);
                put("4", 4389.0f);
                put("5", 4089.0f);
                put("6", 7648.0f);
                put("7", 3788.0f);
                put("8", 6025.0f);
                put("9", 6488.0f);
                put("10", 8907.0f);
                put("11", 6262.0f);
                put("12", 7305.0f);
                put("13", 2209.0f);
                put("14", 2498.0f);
                put("15", 8069.0f);
                put("16", 5342.0f);
            }};
        }

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                removeAllViews();
                createViews();

                if (mValues.size() > 0)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void createViews() {
        for (Map.Entry<String, Float> value : mValues.entrySet()) {
            if (value.getValue() > maxValue)
                maxValue = value.getValue();
        }

        for (Map.Entry<String, Float> value : mValues.entrySet()) {

            LinearLayout viewGroup = new LinearLayout(getContext());
            viewGroup.setOrientation(VERTICAL);
            LinearLayout.LayoutParams paramsViewGroup = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            paramsViewGroup.weight = 1.0f;
            paramsViewGroup.gravity = Gravity.BOTTOM;
            paramsViewGroup.setMarginStart(mBarMarginStart);
            paramsViewGroup.setMarginEnd(mBarMarginEnd);
            viewGroup.setLayoutParams(paramsViewGroup);

            View bar = new View(getContext());
            LinearLayout.LayoutParams paramsBar = new LayoutParams(mBarWidth, (int) ((parentHeight - mBarTextHeight) * (value.getValue() / maxValue)));
            bar.setLayoutParams(paramsBar);
            bar.setBackground(mBarDrawable);
            viewGroup.addView(bar);

            TextView text = new TextView(getContext());
            text.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams paramsText = new LayoutParams(LayoutParams.MATCH_PARENT, mBarTextHeight);
            text.setLayoutParams(paramsText);
            text.setText(String.format(Locale.getDefault(), "%s", value.getKey()));
            viewGroup.addView(text);

            addView(viewGroup);
        }
    }

    public LinkedHashMap<String, Float> getValues() {
        return mValues;
    }

    public void setValues(LinkedHashMap<String, Float> values) {
        this.mValues = values;
    }
}
