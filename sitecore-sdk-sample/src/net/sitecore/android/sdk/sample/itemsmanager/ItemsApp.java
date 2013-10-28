package net.sitecore.android.sdk.sample.itemsmanager;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.sitecore.android.sdk.api.ScApiSession;

import static com.android.volley.Response.Listener;

public class ItemsApp extends Application implements Listener<ScApiSession>, Response.ErrorListener {

    private Prefs mPrefs;

    private ScApiSession mSession;

    public static ItemsApp from(Context context) {
        return (ItemsApp) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPrefs = Prefs.from(this);

        initSession();
    }

    private void initSession() {
        boolean isAuth = mPrefs.isAuth();
        String url = mPrefs.getUrl();
        String login = mPrefs.getLogin();
        String pass = mPrefs.getPassword();

        if (isAuth) {
            ScApiSession.getSession(this, url, login, pass, this, this);
        } else {
            ScApiSession.getAnonymousSession(url, this);
        }
    }

    public ScApiSession getSession() {
        return mSession;
    }

    public void setSession(ScApiSession session) {
        mSession = session;
    }

    @Override
    public void onResponse(ScApiSession session) {
        mSession = session;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, Utils.getMessageFromError(error), Toast.LENGTH_LONG).show();
    }
}
