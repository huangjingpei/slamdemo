package com.hiscene.hiarslamdemo.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by liwenfei on 15/09/2017.
 */

public class SharedPreferencesUtil {
    private static SharedPreferencesUtil _instance = new SharedPreferencesUtil();
    public static SharedPreferencesUtil Instance(){
        return _instance;
    }
    private String KEY = "magicPlugin";
    private SharedPreferences mSharedPreferences;

    public void init(Context context){
        if(mSharedPreferences == null){
            mSharedPreferences= context.getApplicationContext().getSharedPreferences(KEY,0);
        }
    }

    /**
     * str: md5+isTracing
     */
    public void put(String id,String str){
        mSharedPreferences.edit().putString(id,str).commit();
    }

    public String get(String id){
        return mSharedPreferences.getString(id,"");
    }

    public String getMd5(String id){
        String str = mSharedPreferences.getString(id,"");
        if(!"".equals(str)){
            return str.substring(0,str.length()-1);
        }
        return "";
    }

    public int getIsTracing(String id){
        String str = mSharedPreferences.getString(id,"");
        if(str != null&&str.length()>0){
            return Integer.valueOf(str.substring(str.length()-1),str.length());
        }
        return 1;
    }

    public void remove(String id){
        mSharedPreferences.edit().remove(id).commit();
    }

    public Set<String> getIds(){
        return mSharedPreferences.getAll().keySet();
    }
    public void clear(){
        mSharedPreferences.edit().clear().commit();
    }
}
