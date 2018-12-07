package lin553.starcontroller;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

public class NumberPreferenceFragmentCompat extends PreferenceDialogFragmentCompat {
    EditText mEditText;

    public static NumberPreferenceFragmentCompat newInstance(String key) {
        final NumberPreferenceFragmentCompat fragment = new NumberPreferenceFragmentCompat();
        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        NumberPreference preference = (NumberPreference) getPreference();
        mEditText = (EditText) view.findViewById(R.id.edit);
        mEditText.setText(preference.getText());
        mEditText.setSelection(0, preference.getText().length());
    }

    @Override
    protected  boolean needInputMethod() {
        return true;
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            NumberPreference preference = (NumberPreference) getPreference();
            String text = mEditText.getText().toString();
            if (preference.callChangeListener(text)) {
                preference.setText(text);
                preference.setSummary(text);
            }
        }
    }
}
