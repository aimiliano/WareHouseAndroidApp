package mystore.stormeco.gr.appmystore;

import android.app.Application;
import android.content.SharedPreferences;

import com.securepreferences.SecurePreferences;

public class MysStoreApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Get context
        ApplicationClassInstance = this;
    }

    private static MysStoreApp ApplicationClassInstance;
    public static MysStoreApp getInstance() {
        return ApplicationClassInstance;
    }

    public  void setLocalPref(String key,String value){
        SharedPreferences prefs = new SecurePreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key,value);
        //Log.e("saving_____-> for context->"+ctx.toString(),value);
        editor.commit();

    }

    public String getLocalPref(String key){
        SharedPreferences prefs = new SecurePreferences(getApplicationContext());
        String value = prefs.getString(key, "");
        //Log.e("Readng__________-> for context->"+ctx.toString(),value);
        return value;
    }

}
