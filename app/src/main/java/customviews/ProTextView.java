package customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by applify on 2/5/2018.
 */

public class ProTextView extends android.support.v7.widget.AppCompatTextView {

    public ProTextView(Context context) {
        super(context);
        init();
    }

    public ProTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface typeface = Typeface.createFromAsset(getContext()
                    .getAssets(), "fonts/regular.otf");
            setTypeface(typeface);
        }
    }
}
