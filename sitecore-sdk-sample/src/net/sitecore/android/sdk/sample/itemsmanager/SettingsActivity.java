package net.sitecore.android.sdk.sample.itemsmanager;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.sitecore.android.sdk.api.RequestQueueProvider;
import net.sitecore.android.sdk.api.ScApiSession;
import net.sitecore.android.sdk.api.ScApiSessionFactory;
import net.sitecore.android.sdk.api.ScPublicKey;
import net.sitecore.android.sdk.sample.R;

public class SettingsActivity extends PreferenceActivity {
    private Prefs mPrefs;
    private EditTextPreference mUrlPref;
    private EditTextPreference mLoginPref;
    private EditTextPreference mPassPref;
    private CheckBoxPreference mAuthPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);

        Button validateButton = new Button(this);

        validateButton.setText("Validate & Save");
        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });

        getListView().addFooterView(validateButton);

        mPrefs = Prefs.from(this);
        mUrlPref = (EditTextPreference) findPreference(getString(R.string.key_url));
        mLoginPref = (EditTextPreference) findPreference(getString(R.string.key_login));
        mPassPref = (EditTextPreference) findPreference(getString(R.string.key_password));
        mAuthPref = (CheckBoxPreference) findPreference(getString(R.string.key_is_auth));

        mUrlPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (URLUtil.isValidUrl((String) newValue)) {
                    mPrefs.put(R.string.key_url, (String) newValue);
                    preference.setSummary((String) newValue);
                    return true;
                } else {
                    return false;
                }

            }
        });
        mLoginPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (!TextUtils.isEmpty((String) newValue)) {
                    mPrefs.put(R.string.key_login, (String) newValue);
                    preference.setSummary((String) newValue);
                    return true;
                } else {
                    return false;
                }
            }
        });
        mPassPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (!TextUtils.isEmpty((String) newValue)) {
                    mPrefs.put(R.string.key_password, (String) newValue);
                    String password = PasswordTransformationMethod.getInstance().getTransformation(
                            (String) newValue, null).toString();
                    preference.setSummary(password);
                    return true;
                } else {
                    return false;
                }
            }
        });

        mAuthPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mPrefs.put(R.string.key_is_auth, (Boolean) newValue);
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        String url = mPrefs.getUrl();
        String name = mPrefs.getLogin();
        String password = PasswordTransformationMethod.getInstance().getTransformation(
                mPrefs.getPassword(), null).toString();
        boolean isAuth = mPrefs.isAuth();

        mUrlPref.setSummary(url);
        mLoginPref.setSummary(name);
        mPassPref.setSummary(password);
        mAuthPref.setChecked(isAuth);
    }

    private void validate() {
        boolean isAuth = mPrefs.isAuth();
        final String url = mPrefs.getUrl();

        final Response.Listener<Boolean> onSuccess = new Response.Listener<Boolean>() {
            @Override
            public void onResponse(Boolean isSucces) {
                Toast.makeText(SettingsActivity.this, isSucces ? "Connection OK" : "Connection failure",
                        Toast.LENGTH_LONG).show();
            }
        };

        if (isAuth) {
            final String login = mPrefs.getLogin();
            final String password = mPrefs.getPassword();

            Response.ErrorListener onError = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(SettingsActivity.this, Utils.getMessageFromError(error), Toast.LENGTH_LONG).show();
                }
            };

            Request request = ScApiSessionFactory.buildPublicKeyRequest(url, new Response.Listener<ScPublicKey>() {
                @Override
                public void onResponse(ScPublicKey key) {
                    mPrefs.savePublicKey(key);
                    ScApiSessionFactory.newSession(url, key, login, password).
                            checkCredentialsRequest(SettingsActivity.this, onSuccess);
                }
            }, onError);

            RequestQueueProvider.getRequestQueue(this).add(request);
        } else {
            ScApiSession session = ScApiSessionFactory.newAnonymousSession(url);
            session.checkCredentialsRequest(this, onSuccess);
        }
    }
}