package br.com.dias.streetway.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by FernandoDias on 23/06/16.
 */
public class PrefsUtil {

    public static final String PREF_ID = "StreetWayPref";

    public static void setString(Context context, String key , String str) {
        SharedPreferences pref = context.getSharedPreferences(PREF_ID, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, str);
        editor.commit();
    }

    public static void setLong(Context context, String key , Long numero) {
        SharedPreferences pref = context.getSharedPreferences(PREF_ID, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(key, numero);
        editor.commit();
    }

    public static void setBoolean(Context context, String key , boolean valor) {
        SharedPreferences pref = context.getSharedPreferences(PREF_ID, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, valor);
        editor.commit();
    }

    public static String getString(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences(PREF_ID, 0);
        return pref.getString(key,"");
    }

    public static Long getLong(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences(PREF_ID, 0);
        return pref.getLong(key, 0);
    }

    public static Boolean getBoolean(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences(PREF_ID, 0);
        return pref.getBoolean(key, false);
    }

    public static void removerPref(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences(PREF_ID, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, null);
        editor.commit();
    }

}
