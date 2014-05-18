package com.lifeofmle.drinksy.app.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.lifeofmle.drinksy.app.activities.PreferenceKeys;

import java.lang.reflect.Type;

/**
 * Created by mle on 2014-04-29.
 */
public class PreferencesManager {

    public static String getPreferences(Context context, String key){
        SharedPreferences settings = context.getSharedPreferences(PreferenceKeys.APP_PREFERENCES, Context.MODE_PRIVATE);

        String value = settings.getString(key, "");

        return value;
    }

    public static Object getPreferences(Context context, String key, Type type){
        Object prefObj = null;

        try{
            String value = getPreferences(context, key);
            prefObj = GsonSerializer.stringToObject(value, type);
        }catch(Exception ex){
            Log.e("Preferences Manager", "Error converting string to object");
        }

        return prefObj;
    }

    public static String setPreferences(Context context, String key, Object value){
        String strValue = "";

        try{
            SharedPreferences settings = context.getSharedPreferences(PreferenceKeys.APP_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();

            strValue = GsonSerializer.objectToString(value);
            editor.putString(key, strValue);
            editor.commit();
        }catch(Exception e){
            Log.e("Preferences Manager", "Error setting preferences");
        }

        return strValue;
    }
}
