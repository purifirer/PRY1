package unadeca.net.formulariopry.database;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowManager;

public class PersonApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(this);
    }
}
