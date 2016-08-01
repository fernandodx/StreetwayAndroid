package br.com.dias.streetway;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.firebase.client.Firebase;

/**
 * Created by FernandoDias on 23/02/16.
 */
public class StreetwayApplication extends Application {

    private static final String TAG = StreetwayApplication.class.getSimpleName();


    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(false);
        FacebookSdk.sdkInitialize(this);
        AppEventsLogger.activateApp(this);

    }

}
