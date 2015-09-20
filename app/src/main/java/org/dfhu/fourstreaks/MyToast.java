package org.dfhu.fourstreaks;


import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * a testable toast class
 */
public class MyToast extends Toast {

    private static CharSequence lastText;

    @Nullable
    public static String getLastText() {
        CharSequence cs = lastText;
        if (cs == null) {
            return null;
        }

        final StringBuilder sb = new StringBuilder(cs.length());
        sb.append(cs);
        return cs.toString();
    }

    public MyToast(Context context) {
        super(context);
    }

    public static Toast makeText(Context context, CharSequence text, int duration) {
        lastText = text;

        return Toast.makeText(context, text, duration);
    }

}
