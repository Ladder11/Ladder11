package com.ladder11.ladder11.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.content.res.TypedArray;

import com.lader11.ladder11.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jordanbrobots on 12/14/15.
 */
public class PoseDisplay extends View {
    private List<Heading> mHeadings = new ArrayList<Heading>();

    private class Heading {
        public String mLabel;
        public float mValue;
        public int mColor;

        // computed values
        public int mStartAngle;
        public int mEndAngle;

        public int mHighlight;
        public Shader mShader;
    }

    private Paint mTextPaint;
    private Paint mCirclePaint;
    private Paint mHeadingPaint;

    private int circleColor,
                labelColor;
    private String labelText;


    public PoseDisplay(Context context) {
        super(context);
        init();
    }

    public PoseDisplay(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PoseDisplay, 0, 0);

        try {
            //get the text and colors specified in the layout
            labelText = a.getString(R.styleable.PoseDisplay_circleLabel);
            circleColor = a.getColor(R.styleable.PoseDisplay_circleColor, 0x0000FF);
            labelColor = a.getColor(R.styleable.PoseDisplay_textColor, 0);
        } finally {
            a.recycle();
        }
        init();
    }

    /**
     * Create all of the resources
     */
    private void init() {
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHeadingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //#TODO bad idea... but it works...
        int viewWidthHalf = this.getMeasuredWidth()/2;
        int viewHeightHalf = this.getMeasuredHeight()/2;

        int radius = 0;
        if(viewWidthHalf > viewHeightHalf) {
            radius = viewHeightHalf-10;
        } else {
            radius = viewWidthHalf-10;
        }

        //#TODO again... bad doggy
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setAntiAlias(true);
        //Set the paint color to the specified color
        mCirclePaint.setColor(circleColor);

        //Draw the circle!
        canvas.drawCircle(viewWidthHalf, viewHeightHalf, radius, mCirclePaint);

        //#TODO yeah, yeah, yeah...
        mTextPaint.setColor(labelColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(50);

        //Draw the text!
        canvas.drawText(labelText, viewWidthHalf, viewHeightHalf, mTextPaint);

    }

}
