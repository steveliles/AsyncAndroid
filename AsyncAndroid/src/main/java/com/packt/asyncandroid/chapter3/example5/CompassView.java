package com.packt.asyncandroid.chapter3.example5;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class CompassView extends View {

    private Paint pn, ps;
    private Path north, south;
    private float width, height, cx, cy, angle;

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);

        pn = new Paint();
        pn.setColor(Color.RED);

        ps = new Paint();
        ps.setColor(Color.BLACK);

        setCommonPaintStyle(pn);
        setCommonPaintStyle(ps);

        angle = 0f;
    }

    private void setCommonPaintStyle(Paint paint) {
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(5);
        paint.setAntiAlias(true);
    }

    /**
     * Note: no synchronisation - this method must
     * always called from the UI thread.
     */
    public void updateDirection(float angle) {
        this.angle = angle;
        this.invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(width, height);

        // if the measurements of the canvas have changed,
        // e.g. if we just got created, do some pre-computation
        // to set up the paths that we'll use in onDraw.
        if ((width != this.width) && (height != this.height)) {
            this.width = width;
            this.height = height;

            cx = width/2f;
            cy = height/2f;

            // use the smaller of width or height
            // so that our compass needle always
            // fits the screen.
            float w = Math.min(cx, cy)/6f;
            float h = Math.min(cx, cy)-30f;

            // set up a path for the north part
            // of the pointer, which we'll paint red
            // in ondraw.
            north = new Path();
            north.lineTo(-w, 0);
            north.lineTo(0, h);
            north.lineTo(w, 0);
            north.close();

            // set up a path for the south part
            // of the pointer, which we'll paint
            // black in ondraw.
            south = new Path();
            south.lineTo(-w, 0);
            south.lineTo(0, -h);
            south.lineTo(w, 0);
            south.close();
        }
    }

    /**
     * draw the compass!
     */
    protected void onDraw(Canvas canvas) {
        canvas.save();

        // move the canvas so that the origin (0,0) is
        // actually at the center of the canvas
        canvas.translate(cx, cy);

        // rotate the canvas in the opposite direction around
        // the centre of the canvas
        canvas.rotate(-angle);

        // draw our compass pointer pointing directly
        // upwards on our now-rotated canvas. We already
        // pre-computed a north centred around 0,0, so
        // we can just dump that north onto the canvas.
        canvas.drawPath(north, pn);
        canvas.drawPath(south, ps);

        // restore the original canvas position and orientation,
        // which moves our pointer back to the middle and turns
        // it to point in the direction of angle.
        canvas.restore();
    }
}
