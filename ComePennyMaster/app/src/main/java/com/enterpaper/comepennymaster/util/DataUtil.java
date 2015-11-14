package com.enterpaper.comepennymaster.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Kim on 2015-08-04.
 */
public class DataUtil {
    //파일에서 데이터 가져오는거
    public static String getAppPreferences(Context context, String key){
        String returnValue = null;
        SharedPreferences pref = null;
        pref = context.getSharedPreferences("comepenny",0);
        returnValue = pref.getString(key,"");
        return returnValue;
    }

    //파일에 저장하는거
    public static void setAppPreferences(Context context, String key, String value){
        SharedPreferences pref = null;
        pref = context.getSharedPreferences("comepenny",0);
        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.putString(key, value);
        prefEditor.commit();

    }

}
