package customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by applify on 2/5/2018.
 */

public class LightTextView extends TextView {

    public LightTextView(Context context) {
        super(context);
        init();
    }

    public LightTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LightTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface typeface = Typeface.createFromAsset(getContext()
                    .getAssets(), "fonts/light.otf");
            setTypeface(typeface);
        }
    }
}
