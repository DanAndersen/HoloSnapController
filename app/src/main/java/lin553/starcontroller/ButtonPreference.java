package lin553.starcontroller;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;

public class ButtonPreference extends Preference {

    public ButtonPreference(Context context) {
        this(context, null);
    }

    public ButtonPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.editTextPreferenceStyle);
    }

    public ButtonPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ButtonPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setLayoutResource(R.layout.pref_button);
    }

}
