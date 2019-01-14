package lin553.starcontroller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.sourceforge.opencamera.MainActivity;

public class SetupActivity extends AppCompatActivity {

    private SharedPreferences mPreference;
    private AutoCompleteTextView mIPView;
    private EditText mPortView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        mIPView = (AutoCompleteTextView) findViewById(R.id.hololens_ip);
        // TODO
        mPreference = getPreferences(Context.MODE_PRIVATE);
        String ip = mPreference.getString(getString(R.string.param_ip), null);
        if (ip != null) {
            mIPView.append(ip);
        }
        TextView.OnEditorActionListener imeListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                validateAndConnect(ControllerMainActivity.class);
                return true;
            }
        };
        mIPView.setOnEditorActionListener(imeListener);
        mPortView = (EditText) findViewById(R.id.hololens_port);
        mPortView.setOnEditorActionListener(imeListener);
        Button connectButton = (Button) findViewById(R.id.connect_button);
        connectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndConnect(ControllerMainActivity.class);
            }
        });
        Button debugButton = (Button) findViewById(R.id.debug_button);
        debugButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetupActivity.this, ControllerMainActivity.class);
                startActivity(intent);
            }
        });
        Button openCameraButton = (Button) findViewById(R.id.open_camera_button);
        openCameraButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndConnect(MainActivity.class);
            }
        });
    }

    private void validateAndConnect(Class destinationActivityClass) {
        // Reset errors.
        mIPView.setError(null);
        mPortView.setError(null);

        // Store values at the time of the login attempt.
        String ip = mIPView.getText().toString();
        String port = mPortView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!isIPValid(ip)) {
            mIPView.setError(getString(R.string.error_invalid_email));
            focusView = mIPView;
            cancel = true;
        } else if (!isPortValid(port)) {
            mPortView.setError(getString(R.string.error_invalid_port));
            focusView = mPortView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            mPreference
                    .edit()
                    .putString(getString(R.string.param_ip), ip)
                    /*.putString(getString(R.string.param_port), port)*/
                    .apply();
            Intent intent = new Intent(SetupActivity.this, destinationActivityClass);
            intent.putExtra(getString(R.string.param_ip), ip);
            intent.putExtra(getString(R.string.param_port), TextUtils.isEmpty(port) ? "12345" : port);
            startActivity(intent);
        }
    }

    private boolean isIPValid(String ip) {
        String[] segments = ip.split("\\.");
        if (segments.length != 4)
            return false;
        try {
            for (String segment : segments) {
                int s = Integer.parseInt(segment);
                if (s < 0 || s > 255)
                    return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isPortValid(String port) {
        if (TextUtils.isEmpty(port))
            return true;
        try {
            int p = Integer.parseInt(port);
            return (p >= 0 && p < 65536);
        } catch (Exception e) {
            return false;
        }
    }
}

