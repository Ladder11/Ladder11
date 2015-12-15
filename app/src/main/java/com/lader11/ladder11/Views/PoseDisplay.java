package com.lader11.ladder11.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.content.res.TypedArray;

import com.lader11.ladder11.R;

/**
 * Created by jordanbrobots on 12/14/15.
 */
//#TODO make things look a little nicer
//#TODO make the text fit inside the circle
//#TODO change the circle to an outline
//#TODO change the gyro bubble to outline, and layer on top
public class PoseDisplay extends View {
    private Paint mTextPaint;
    private Paint mCirclePaint;
    private Paint mOdoPaint;
    private Paint mGyroPaint;

    private int circleColor = 0xFF0000;     //Default to red
    private int labelColor = 0x000000;      //Default to black
    private String labelText = "";  //Default to empty
    private String labelXText = "X: 000.00\"";
    private float labelXHeight;
    private String labelYText = "Y: 000.00\"";
    private float labelYHeight;
    private String labelOdoText = "Head: 345.67\u00b0";
    private float labelOdoHeight;
    private String labelGyroText = "Gyro: 234.57\u00b0";
    private float labelGyroHeight;
    private int odoBubbleColor = 0xFF0000;
    private int gyroBubbleColor = 0x00FF00;

    private float odoHead = 0.0f;
    private float gyroHead = 90.0f;
    private float odoX;
    private float odoY;
    private float gyroX;
    private float gyroY;
    private float xPos;
    private float yPos;

    private int viewWidthHalf;      //Center of the view
    private int viewHeightHalf;     //Center of the view
    private int radius;             //Radius of the circle
    private float bubbleSpace;      //Size of space for the heading and gyro bubbles



    public PoseDisplay(Context context) {
        super(context);
        init();
    }

    public PoseDisplay(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PoseDisplay, 0, 0);

        try {
            //get the colors specified in the layout
            circleColor = a.getColor(R.styleable.PoseDisplay_circleColor, 0x0000FF);
            labelColor = a.getColor(R.styleable.PoseDisplay_textColor, 0);
            odoBubbleColor = a.getColor(R.styleable.PoseDisplay_odoBubbleColor, odoBubbleColor);
            gyroBubbleColor = a.getColor(R.styleable.PoseDisplay_gyroBubbleColor, gyroBubbleColor);
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
        mOdoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGyroPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        //Set the attributes for the text
        mTextPaint.setColor(labelColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(50);

        //Set the attributes for the circle
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setColor(circleColor);
        mCirclePaint.setStrokeWidth(8);

        //Set the attributes for the bubbles
        mOdoPaint.setStyle(Paint.Style.FILL);
        mOdoPaint.setColor(odoBubbleColor);
        mOdoPaint.setShadowLayer(3, 0, 3, R.color.shadow);
        mGyroPaint.setStyle(Paint.Style.STROKE);
        mGyroPaint.setColor(gyroBubbleColor);
        mGyroPaint.setStrokeWidth(7);
        mGyroPaint.setShadowLayer(3,0,3,R.color.shadow);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        viewWidthHalf = this.getMeasuredWidth()/2;
        viewHeightHalf = this.getMeasuredHeight()/2;

        radius = 0;
        if(viewWidthHalf > viewHeightHalf) {
            bubbleSpace = (0.1f*viewHeightHalf);
            radius = viewHeightHalf-10- (int)bubbleSpace;
        } else {
            bubbleSpace = (0.1f*viewWidthHalf);
            radius = viewWidthHalf-10- (int)bubbleSpace;
        }

        //Update bubble positions
        calculateBubblePositions();

        //Calculate the positions of the label text
        //Ascent returns - values, and descent returns + values.  Y values increase down the screen
        labelXHeight = viewHeightHalf + 2*mTextPaint.ascent() - mTextPaint.descent();
        labelYHeight = viewHeightHalf + mTextPaint.ascent();
        labelOdoHeight = viewHeightHalf + mTextPaint.descent();
        labelGyroHeight = viewHeightHalf - (2*mTextPaint.ascent()) + mTextPaint.descent();
    }

    private void calculateBubblePositions() {
        //#TODO fix cos stuff to work with positive y moves down the screen
        //#TODO find out if the bubbles should be on the line
        float hLength = radius + bubbleSpace/2;
        double dradius = (double) radius;
        odoX = (float) (viewWidthHalf + (Math.cos(Math.toRadians(-odoHead-90.0f)) * dradius));
        odoY = (float) (viewHeightHalf + (Math.sin(Math.toRadians(-odoHead-90.0f)) * dradius));
        gyroX = (float) (viewWidthHalf + (Math.cos(Math.toRadians(-gyroHead-90.0f)) * dradius));
        gyroY = (float) (viewHeightHalf + (Math.sin(Math.toRadians(-gyroHead-90.0f)) * dradius));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Draw the circle!
        canvas.drawCircle(viewWidthHalf, viewHeightHalf, radius, mCirclePaint);

        //Draw the text!
        canvas.drawText(labelXText, viewWidthHalf, labelXHeight, mTextPaint);
        canvas.drawText(labelYText, viewWidthHalf, labelYHeight, mTextPaint);
        canvas.drawText(labelOdoText, viewWidthHalf, labelOdoHeight, mTextPaint);
        canvas.drawText(labelGyroText, viewWidthHalf, labelGyroHeight, mTextPaint);

        //Draw the heading bubbles
        canvas.drawCircle(gyroX, gyroY, bubbleSpace, mGyroPaint);
        canvas.drawCircle(odoX, odoY, bubbleSpace, mOdoPaint);
    }

    public void setGyroHeading(float heading) {
        gyroHead = heading;
        labelGyroText = String.format("Gyro: %f.2\u00b0", gyroHead);
        calculateBubblePositions();
        invalidate();
    }

    public void setOdometryHeading(float heading) {
        odoHead = heading;
        labelOdoText = String.format("Head: %f.2\u00b0", odoHead);
        calculateBubblePositions();
        invalidate();
    }

    public void setHeadings(float odometryHeading, float gyroHeading) {
        odoHead = odometryHeading;
        gyroHead = gyroHeading;
        labelOdoText = String.format("Head: %f.2\u00b0", odoHead);
        labelGyroText = String.format("Gyro: %f.2\u00b0", gyroHead);
        calculateBubblePositions();
        invalidate();
    }

    public void setPosition(float x, float y) {
        xPos = x;
        yPos = y;
        labelXText = String.format("X: %f.2\"", xPos);
        labelYText = String.format("Y: %f.2\"", yPos);
        invalidate();
    }

    public void setRobotPose(float x, float y, float odometryHeading) {
        xPos = x;
        yPos = y;
        odoHead = odometryHeading;
        labelXText = String.format("X: %f.2\"", xPos);
        labelYText = String.format("Y: %f.2\"", yPos);
        labelOdoText = String.format("Head: %f.2\u00b0", odoHead);
        calculateBubblePositions();
        //labelText = x+", "+y;
        invalidate();
    }

}
