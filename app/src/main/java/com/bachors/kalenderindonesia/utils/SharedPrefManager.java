package com.bachors.kalenderindonesia.utils;

import android.content.Context;
import android.content.SharedPreferences;

/*
    https://github.com/bachors/Android-Kalender-Indonesia
*/

public class SharedPrefManager {

    public static final String SP_SS_APP = "spKALENDER";

    public static final String SP_TAHUN = "spTahun";
    public static final String SP_LIBUR = "spLibur";

    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    public SharedPrefManager(Context context){
        sp = context.getSharedPreferences(SP_SS_APP, Context.MODE_PRIVATE);
        spEditor = sp.edit();
    }

    public void saveSPString(String keySP, String value){
        spEditor.putString(keySP, value);
        spEditor.commit();
    }

    public String getSpTahun(){
        return sp.getString(SP_TAHUN, "");
    }

    public String getSpLibur(){
        return sp.getString(SP_LIBUR, "{}");
    }

}
