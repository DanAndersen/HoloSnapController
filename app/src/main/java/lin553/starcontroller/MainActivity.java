package lin553.starcontroller;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {
    private View mView;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Tab1ParamsFragment mParamsFragment;
    private Tab2LogFragment mLogFragment;
    private Tab3StatusFragment mStatusFragment;

    private static SocketTask mTask;
    private static Socket mSocket;
    private static BufferedReader mReader;
    private static BufferedWriter mWriter;
    //private static String mFABCommand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mView = findViewById(android.R.id.content);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(mSectionsPagerAdapter.getCount());
        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (mSocket.isConnected() && mFABCommand != null) {
                        mWriter.write(String.format(getString(R.string.socket_fab_format), mFABCommand));
                        mWriter.flush();
                    }
                } catch (Exception e) {
                }
            }
        });*/
        mTask = new SocketTask();
        mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected void onStop() {
        mTask.cancel(true);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mParamsFragment = new Tab1ParamsFragment();
                case 1:
                    return mLogFragment = new Tab2LogFragment();
                case 2:
                    return mStatusFragment =  new Tab3StatusFragment();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.title_params);
                case 1:
                    return getString(R.string.title_logs);
                case 2:
                    return getString(R.string.title_status);
            }
            return null;
        }
    }

    public class SocketTask extends AsyncTask<Void, String, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                String ip = bundle.getString(getString(R.string.param_ip));
                String port = bundle.getString(getString(R.string.param_port));
                try {
                    while (true) {
                        try {
                            mSocket = new Socket();
                            mSocket.connect(new InetSocketAddress(ip, Integer.parseInt(port)), 1000);
                            mReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                            mWriter = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));
                            while (true) {
                                String type = mReader.readLine();
                                StringBuilder sb = new StringBuilder();
                                String message = mReader.readLine();
                                sb.append(message);
                                while ((message = mReader.readLine()).compareTo(getString(R.string.socket_end)) != 0) {
                                    sb.append('\n');
                                    sb.append(message);
                                }
                                message = sb.toString();
                                publishProgress(type, message);
                            }
                        } catch (SocketTimeoutException e) {
                        } catch (SocketException e) {
                            publishProgress(getString(R.string.socket_disconnect));
                            Snackbar.make(mView, "Disconnected!", Snackbar.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                }
            } else {
                publishProgress("CONTROL", "Checker_X:9;Checker_Y:6;Checker_Size:0.05;Visual_Checkerboard:True;Visual_HololensCamera:False;Button_TakePicture:;Button_Test:;Visual_InformationPane:True;Visual_SpatialMap:False;AnnotationServer_IP:128.46.125.52;AnnotationServer_Port:8988;Visual_AnnotationRays:False;AnnotationTool_Scale:1;AnnotationTool_Offset:0;AnnotationPolyline_LineWidth:0.015;AnnotationPolyline_BrightnessMultiplier:1;Annotation_Anchor:True;Annotation_Update:True;Annotation_DummyDiagonal:0;Annotation_DummyDense:300;Visual_TopdownCamera:False;Visual_FPSCounter:True;");
                publishProgress("LOG", "testtest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\ntest\n");
            }
            return null;
        }

        @Override
        protected  void onProgressUpdate(String... values) {
            String key = values[0];
            if (key.compareTo(getString(R.string.socket_disconnect)) == 0) {
                mParamsFragment.set(null);
                mLogFragment.set(null);
                mStatusFragment.set(null);
            } else {
                String value = values[1];
                if (key.compareTo(getString(R.string.socket_control)) == 0) {
                    mParamsFragment.set(value);
                } else if (key.compareTo(getString(R.string.socket_log)) == 0) {
                    mLogFragment.set(value);
                } else if (key.compareTo(getString(R.string.socket_status)) == 0) {
                    mStatusFragment.set(value);
                }
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                mSocket.close();
            } catch (Exception e) {
            }
        }

        @Override
        protected void onCancelled() {
            try {
                mSocket.close();
            } catch (Exception e) {
            }
        }
    }

    public static class Tab1ParamsFragment extends PreferenceFragmentCompat implements TabFragment, Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

        private PreferenceScreen mPreference;
        private Context mContext;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.preferences);
            mPreference = getPreferenceScreen();
            mContext = mPreference.getContext();
        }

        @Override
        public void onDisplayPreferenceDialog(Preference preference) {
            DialogFragment dialogFragment = null;
            if (preference instanceof NumberPreference) {
                dialogFragment = NumberPreferenceFragmentCompat.newInstance(preference.getKey());
            }
            // if custom dialog
            if (dialogFragment != null) {
                dialogFragment.setTargetFragment(this, 0);
                dialogFragment.show(this.getFragmentManager(), "android.support.v7.preference.PreferenceFragment.DIALOG");
            } else {
                super.onDisplayPreferenceDialog(preference);
            }
        }

        protected String[] splitCamel(String s) {
            return s.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
        }

        protected String camel2Space(String s) {
            String[] camel = splitCamel(s);
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (String str : camel) {
                if (first) {
                    result.append(str);
                    first = false;
                } else {
                    result.append(" ").append(str.toLowerCase());
                }
            }
            return result.toString();
        }

        @Override
        public void set(String s) {
            if (s == null) {
                int count = mPreference.getPreferenceCount();
                for (int i = 0; i < count; ++i) {
                    Preference p = mPreference.getPreference(i);
                    p.setEnabled(false);
                }
            } else {
                mPreference.removeAll();
                // seperate strings to lines
                String[] lines = s.split(";");
                Arrays.sort(lines);
                // group preferences
                Map<String, List<Pair<String, String>>> preferences = new TreeMap<>();
                for (String line : lines) {
                    // pair[0] is key, pair[1] is value
                    String[] pair = line.split(":");
                    // key[0] is category, key[1] is name
                    String[] key = pair[0].split("_");
                    //if (key.length == 1) {
                        //mFABCommand = key[0];
                    //} else {
                    {
                        if (!preferences.containsKey(key[0]))
                            preferences.put(key[0], new ArrayList<Pair<String, String>>());
                        String value = pair.length == 1 ? "" : pair[1];
                        preferences.get(key[0]).add(new Pair<>(key[1], value));
                    }
                }
                // generate preference widgets accordingly
                for (Map.Entry<String, List<Pair<String, String>>> c : preferences.entrySet()) {
                    PreferenceCategory category = new PreferenceCategory(mContext);
                    category.setTitle(camel2Space(c.getKey()));
                    mPreference.addPreference(category);
                    for (Pair<String, String> pair : c.getValue()) {
                        String value = pair.second.toLowerCase();
                        Preference p;
                        if (value.equals("")) {
                            ButtonPreference _p = new ButtonPreference(mContext);
                            _p.setOnPreferenceClickListener(this);
                            p = _p;
                        } else if (value.equals("true") || value.equals("false")) {
                            SwitchPreferenceCompat _p = new SwitchPreferenceCompat(mContext);
                            _p.setChecked(Boolean.parseBoolean(value));
                            _p.setSummary(value);
                            p = _p;
                        } else {
                            NumberPreference _p = new NumberPreference(mContext);
                            //EditTextPreference _p = new EditTextPreference(mContext);
                            _p.setDialogTitle(pair.first);
                            _p.setSummary(pair.second);
                            _p.setText(pair.second);
                            p = _p;
                        }
                        p.setOnPreferenceChangeListener(this);
                        p.setKey(c.getKey() + "_" + pair.first);
                        p.setTitle(camel2Space(pair.first));
                        p.setPersistent(false);
                        category.addPreference(p);
                    }
                }
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            try {
                String key = preference.getKey();
                String value = newValue.toString();
                preference.setSummary(value);
                if (mSocket.isConnected()) {
                    mWriter.write(String.format(getString(R.string.socket_format), key, value));
                    mWriter.flush();
                }
            } catch (Exception e) {
            }
            return true;
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            try {
                String key = preference.getKey();
                if (mSocket.isConnected()) {
                    mWriter.write(String.format(getString(R.string.socket_button_format), key));
                    mWriter.flush();
                }
            } catch (Exception e) {
            }
            return true;
        }
    }

    public static class Tab2LogFragment extends Fragment implements TabFragment {

        protected TextView mText;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_tab2_logs, container, false);
            mText = (TextView) view.findViewById(R.id.logs);
            return view;
        }

        @Override
        public void set(String s) {
            if (s == null) {
                mText.setEnabled(false);
            } else {
                mText.setEnabled(true);
                mText.setText(s);
            }
        }
    }

    public static class Tab3StatusFragment extends Fragment implements TabFragment {

        protected TextView mText;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_tab3_status, container, false);
            mText = (TextView) view.findViewById(R.id.status);
            return view;
        }

        @Override
        public void set(String s) {
            if (s == null) {
                mText.setEnabled(false);
            } else {
                mText.setEnabled(true);
                mText.setText(s);
            }
        }
    }
}
