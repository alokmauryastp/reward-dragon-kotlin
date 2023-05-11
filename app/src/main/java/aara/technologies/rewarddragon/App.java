package aara.technologies.rewarddragon;

import android.app.Application;

import aara.technologies.rewarddragon.utils.SharedPrefManager;

public class App extends Application {

    public static String token="";
    public  static SharedPrefManager sharedPrefManager;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPrefManager = SharedPrefManager.Companion.getInstance(this);
    }
}
