package org.gem.indo.dooit.views.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import org.gem.indo.dooit.R;

/**
 * Created by wsche on 2016/11/30.
 */

public class ClipedRelativeLayout extends RelativeLayout {

    Paint paint = new Paint();
    Path path = new Path();
    Rect rect = new Rect();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ClipedRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public ClipedRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ClipedRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClipedRelativeLayout(Context context) {
        super(context);
        init();
    }

    private void init() {
        paint.setColor(ContextCompat.getColor(getContext(), R.color.green));
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Snackbar.make(this,canvas.getClipBounds().bottom,canvas.getClipBounds().right).show();
        path.addRoundRect(new RectF(canvas.getClipBounds()), 300, 300, Path.Direction.CW);
        canvas.clipPath(path);

        //super.onDraw(canvas);

    }
}
