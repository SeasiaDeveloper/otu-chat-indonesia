package com.eklanku.otuChat.ui.views;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.eklanku.otuChat.R;

public class ARTextView extends TextView {

    public ARTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public ARTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);

    }

    public ARTextView(Context context) {
        super(context);
        init(null);
    }

    private void init(AttributeSet attrs) {
        if (attrs!=null) {
            Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/arial.ttf");
            setTypeface(myTypeface);
        }
    }

}
