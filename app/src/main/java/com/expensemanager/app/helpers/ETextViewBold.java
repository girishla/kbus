package com.expensemanager.app.helpers;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.expensemanager.app.main.EApplication;
import com.expensemanager.app.service.font.Font;

/**
 * Created by Girish Lakshmanan on 9/18/19.
 */

public class ETextViewBold extends TextView {

    public ETextViewBold(Context context) {
        super(context);
        initialize();
    }

    public ETextViewBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public ETextViewBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    private void initialize() {
        if (!isInEditMode()) {
            setTypeface(EApplication.getInstance().getTypeface(Font.BOLD));
        }
    }
}
