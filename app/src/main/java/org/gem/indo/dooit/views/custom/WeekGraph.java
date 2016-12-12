package org.gem.indo.dooit.views.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.gem.indo.dooit.models.goal.Goal;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;


/**
 * TODO: document your custom view class.
 */
public class WeekGraph extends FrameLayout {
    int parentWidth, parentHeight;
    private int mBarWidth = 0, mBarMarginStart = 0, mBarMarginEnd = 0, mBarTextHeight = 0, mBarAverageHeight = 0, mBarTargetHeight = 0;
    private Drawable mBarDrawable;
    private LinkedHashMap<String, Float> mValues = new LinkedHashMap<>();
    private double mMaxValue = 0d, mWeeklyTarget = 0d, mWeeklyAverage = 0d;
    private Goal mGoal;

    @ColorInt
    private int mBarTargetColor = Color.GREEN, mBarAverageColor = Color.YELLOW;

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
                attrs, org.gem.indo.dooit.R.styleable.WeekGraph, defStyle, 0);

        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mBarWidth = a.getDimensionPixelSize(
                org.gem.indo.dooit.R.styleable.WeekGraph_gw_bar_width,
                mBarWidth);
        mBarTextHeight = a.getDimensionPixelSize(
                org.gem.indo.dooit.R.styleable.WeekGraph_gw_bar_textHeight,
                mBarTextHeight);
        mBarMarginStart = a.getDimensionPixelSize(
                org.gem.indo.dooit.R.styleable.WeekGraph_gw_bar_marginStart,
                mBarMarginStart);
        mBarMarginEnd = a.getDimensionPixelSize(
                org.gem.indo.dooit.R.styleable.WeekGraph_gw_bar_marginEnd,
                mBarMarginEnd);
        mBarAverageHeight = a.getDimensionPixelSize(
                org.gem.indo.dooit.R.styleable.WeekGraph_gw_bar_avgHeight,
                mBarAverageHeight);
        mBarTargetHeight = a.getDimensionPixelSize(
                org.gem.indo.dooit.R.styleable.WeekGraph_gw_bar_targetHeight,
                mBarTargetHeight);
        mBarTargetColor = a.getColor(
                org.gem.indo.dooit.R.styleable.WeekGraph_gw_bar_targetColor,
                mBarTargetColor);
        mBarAverageColor = a.getColor(
                org.gem.indo.dooit.R.styleable.WeekGraph_gw_bar_avgColor,
                mBarAverageColor);

        if (a.hasValue(org.gem.indo.dooit.R.styleable.WeekGraph_gw_bar_drawable)) {
            mBarDrawable = a.getDrawable(
                    org.gem.indo.dooit.R.styleable.WeekGraph_gw_bar_drawable);
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

                if (mValues.size() > 0) {
                    createViews();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
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
        removeAllViews();
        Float totalValue = 0f;
        for (Map.Entry<String, Float> value : mValues.entrySet()) {
            if (value.getValue() > mMaxValue)
                mMaxValue = value.getValue();

            totalValue += value.getValue();
        }

        if(mWeeklyAverage > mMaxValue)
            mMaxValue = mWeeklyAverage;

        if(mWeeklyTarget > mMaxValue)
            mMaxValue = mWeeklyTarget;

        mMaxValue *= 1.1;

        LinearLayout viewContainer = new LinearLayout(getContext());
        LinearLayout.LayoutParams paramsViewContainer = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        viewContainer.setOrientation(LinearLayout.HORIZONTAL);
        viewContainer.setLayoutParams(paramsViewContainer);
        for (Map.Entry<String, Float> value : mValues.entrySet()) {

            LinearLayout viewGroup = new LinearLayout(getContext());
            viewGroup.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams paramsViewGroup = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            paramsViewGroup.weight = 1.0f;
            paramsViewGroup.gravity = Gravity.BOTTOM;
            paramsViewGroup.setMarginStart(mBarMarginStart);
            paramsViewGroup.setMarginEnd(mBarMarginEnd);
            viewGroup.setLayoutParams(paramsViewGroup);

            View bar = new View(getContext());
            LinearLayout.LayoutParams paramsBar = new LinearLayout.LayoutParams(mBarWidth, (int) ((parentHeight - mBarTextHeight) * (value.getValue() / mMaxValue)));
            bar.setLayoutParams(paramsBar);
            bar.setBackground(mBarDrawable);
            viewGroup.addView(bar);

            TextView text = new TextView(getContext());
            text.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams paramsText = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, mBarTextHeight);
            text.setLayoutParams(paramsText);
            text.setText(String.format(Locale.getDefault(), "%s", value.getKey()));
            viewGroup.addView(text);

            viewContainer.addView(viewGroup);
        }

        View weeklyAverage = new View(getContext());
        FrameLayout.LayoutParams paramsAvarage = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, mBarAverageHeight);
        paramsAvarage.gravity = Gravity.BOTTOM;
        paramsAvarage.bottomMargin = (int) ((parentHeight - mBarTextHeight) * (mWeeklyAverage / mMaxValue)) + mBarTextHeight;
        weeklyAverage.setBackgroundColor(mBarAverageColor);
        weeklyAverage.setLayoutParams(paramsAvarage);
        addView(weeklyAverage);

        View weeklyTarget = new View(getContext());
        FrameLayout.LayoutParams paramsGoal = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, mBarTargetHeight);
        paramsGoal.gravity = Gravity.BOTTOM;
        paramsGoal.bottomMargin = (int) ((parentHeight - mBarTextHeight) * (mWeeklyTarget / mMaxValue)) + mBarTextHeight;
        weeklyTarget.setBackgroundColor(mBarTargetColor);
        weeklyTarget.setLayoutParams(paramsGoal);
        addView(weeklyTarget);

        addView(viewContainer);

    }

    public Goal getGoal() {
        return mGoal;
    }

    public void setGoal(Goal mGoal) {
        this.mGoal = mGoal;

        mValues = mGoal.getWeeklyTotals();
        mMaxValue = 0d;
        mWeeklyTarget = mGoal.getWeeklyTarget();
        mWeeklyAverage = mGoal.getWeeklyAverage();

        createViews();
        requestLayout();
    }
}
