package id.dhodi.gamecontrol;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.widget.TextView;

public class Ui {
    public static final int BLUE = 0xFF0381FE;
    public static final int CARD = 0xFF1E1E1E;
    public static final int BG = 0xFF050505;
    public static final int SUB = 0xFF9E9E9E;
    public static final int WHITE = 0xFFFFFFFF;
    public static final int RED = 0xFFFF5252;

    public static int dp(Activity a, float v) {
        return (int) (v * a.getResources().getDisplayMetrics().density + 0.5f);
    }

    public static GradientDrawable card(Activity a, int color) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        g.setCornerRadius(dp(a, 22));
        return g;
    }

    public static TextView text(Activity a, String s, float sp, int color, boolean bold) {
        TextView t = new TextView(a);
        t.setText(s);
        t.setTextSize(sp);
        t.setTextColor(color);
        if (bold) t.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        return t;
    }
}
