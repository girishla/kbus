package com.bigmantra.natco.helpers;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.bigmantra.natco.main.EApplication;
import com.bigmantra.natco.service.font.Font;

/**
 * Created by Girish Lakshmanan on 9/4/19.
 */

public class ETextView extends TextView {

    public ETextView(Context context) {
        super(context);
        initialize();
    }

    public ETextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public ETextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    private void initialize() {
        if (!isInEditMode()) {
            setTypeface(EApplication.getInstance().getTypeface(Font.LIGHT));
        }
    }
}
