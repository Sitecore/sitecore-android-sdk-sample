package net.sitecore.android.sdk.sample.itemsmanager;

import android.app.Application;
import android.content.Context;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import net.sitecore.android.sdk.api.ScApiSession;
import net.sitecore.android.sdk.api.ScApiSessionFactory;
import net.sitecore.android.sdk.api.ScPublicKey;

public class ItemsApp extends Application {

    private Prefs mPrefs;

    public static ItemsApp from(Context context) {
        return (ItemsApp) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPrefs = Prefs.from(this);
    }

    public ScApiSession getSession() {
        if (mPrefs.isAuth()) {
            String keyValue = mPrefs.getPublicKeyValue();
            ScPublicKey key;
            try {
                key = new ScPublicKey(keyValue);
            } catch (InvalidKeySpecException e) {
                throw new RuntimeException("Cannot read public key");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Cannot read public key");
            }
            return ScApiSessionFactory.newSession(mPrefs.getUrl(), key, mPrefs.getLogin(), mPrefs.getPassword());
        } else {
            return ScApiSessionFactory.newAnonymousSession(mPrefs.getUrl());
        }
    }
}
