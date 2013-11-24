package com.packt.asyncandroid.chapter4.example4;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

public class PrimeView extends TextView {

    public PrimeView(Context context) {
        super(context);

        setGravity(Gravity.CENTER);
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 40);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // make our view square!
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }

    public void setValue(String value) {
        setText(value);
    }
}
