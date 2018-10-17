package com.eklanku.otuChat.ui.views.barcode;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.eklanku.otuChat.R;

public class BarCodeScannerOverlay extends ViewGroup {
    private static final int FRAMES_RATE = 7;

    private float left, top, endY;
    private float rectWidth, rectHeight;
    private int framesOrigin;
    private int frames;
    private int framesSlowDown;
    private boolean revAnimation;
    private int lineColor, lineWidth;

    public BarCodeScannerOverlay(Context context) {
        super(context);
    }

    public BarCodeScannerOverlay(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarCodeScannerOverlay(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.BarCodeScannerOverlay,
                0, 0);
        rectWidth = a.getDimension(R.styleable.BarCodeScannerOverlay_square_width, getResources().getInteger(R.integer.barcode_scanner_rect_width));
        rectHeight = a.getDimension(R.styleable.BarCodeScannerOverlay_square_height, getResources().getInteger(R.integer.barcode_scanner_rect_height));
        lineColor = a.getColor(R.styleable.BarCodeScannerOverlay_line_color, ContextCompat.getColor(context, R.color.common_google_signin_btn_text_dark));
        lineWidth = a.getInteger(R.styleable.BarCodeScannerOverlay_line_width, getResources().getInteger(R.integer.barcode_scanner_line_width));
        framesOrigin = a.getInteger(R.styleable.BarCodeScannerOverlay_line_speed, getResources().getInteger(R.integer.barcode_scanner_line_speed));
        framesSlowDown = a.getInteger(R.styleable.BarCodeScannerOverlay_line_speed_slowdown, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        left = (w - rectWidth) / 2;
        top = (h - rectHeight) / 2;
        endY = top;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw transparent rect
        int cornerRadius = 0;
        Paint eraser = new Paint();
        eraser.setAntiAlias(true);
        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));


        RectF rect = new RectF(left, top - getPaddingBottom(), rectWidth + left, rectHeight + top - getPaddingBottom());
        canvas.drawRoundRect(rect, (float) cornerRadius, (float) cornerRadius, eraser);

        // draw horizontal line
        Paint line = new Paint();
        line.setColor(lineColor);
        line.setStrokeWidth(Float.valueOf(lineWidth));

        // adding slowdown speed effect line
        if (framesSlowDown != 0) {
            boolean isNeedLowerSpeedBottom = endY + frames * FRAMES_RATE >= top + rectHeight - frames;
            boolean isNeedLowerSpeedTop = endY - frames * FRAMES_RATE <= top + frames;

            if (isNeedLowerSpeedBottom || isNeedLowerSpeedTop) {
                frames = framesSlowDown;
            } else {
                frames = framesOrigin;
            }
        } else {
            frames = framesOrigin;
        }

        // draw the line to product animation
        if (endY >= top + rectHeight - frames) {
            revAnimation = true;
        } else if (endY == top + frames) {
            revAnimation = false;
        }

        // check if the line has reached to bottom
        if (revAnimation) {
            endY -= frames;
        } else {
            endY += frames;
        }
        canvas.drawLine(left, endY - getPaddingBottom(), left + rectWidth, endY - getPaddingBottom(), line);
        invalidate();
    }
}