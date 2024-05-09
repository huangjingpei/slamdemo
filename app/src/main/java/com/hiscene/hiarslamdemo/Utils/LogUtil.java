package com.hiscene.hiarslamdemo.Utils;

import android.app.Activity;
import android.util.Log;

import java.io.File;
import java.util.Date;

/**
 * Created by li on 2016/8/31.
 */
public class LogUtil {
    private final static String TAG = "HiARSDK";

    private static boolean showARLog = true;
    private static boolean saveLog2Local = true;

    private static String localLogPath;

    public static void showARLog(boolean show){
        showARLog = show;
    }

    public static void setSaveLog2Local(boolean save){
        saveLog2Local = save;
    }

    /**
     * 启动一次建立一个log文件
     * @param activity
     */
    public static void init(Activity activity) {
        String logPath = activity.getExternalCacheDir().getPath();
        String time = LogFileUtil.getYMDHMS(new Date());
        time = time.replace(" ","_");
        localLogPath = logPath+ File.separator+"log/log_"+time+".txt";
    }

    public static void Logi(String message) {
        if(showARLog){
            Log.i(TAG, message);
        }
        if(saveLog2Local && localLogPath != null){
            LogFileUtil.writeLineMsg2File(localLogPath,"info:"+message);
        }
    }

    /**
     * info
     * @param TAG
     * @param message
     */
    public static void Logi(String TAG,String message) {
        if(showARLog){
            Log.i(TAG, message);
        }
        if(saveLog2Local && localLogPath != null){
            LogFileUtil.writeLineMsg2File(localLogPath,TAG+","+"info:"+message);
        }
    }

    public static void Loge(String message){
        Log.e(TAG,message);
        if(saveLog2Local && localLogPath != null){
            LogFileUtil.writeLineMsg2File(localLogPath,"err:"+message);
        }
    }

    /**
     * error
     * @param TAG
     * @param message
     */
    public static void Loge(String TAG,String message){
        Log.e(TAG,message);
        if(saveLog2Local && localLogPath != null){
            LogFileUtil.writeLineMsg2File(localLogPath,TAG+","+"err:"+message);
        }
    }

    /**
     * debug
     * @param TAG
     * @param msg
     */
    public static void Logd(String TAG,String msg){
        if(showARLog) Log.d(TAG,msg);
        if(saveLog2Local && localLogPath != null){
            LogFileUtil.writeLineMsg2File(localLogPath,TAG+","+"debug:"+msg);
        }
    }

    /**
     * 异常信息
     * @param e
     */
    public static void LogException(String TAG,Exception e){
        if(showARLog) Log.e(TAG,"--------- 异常信息\n"+e.toString());
        if(saveLog2Local && localLogPath != null){
            LogFileUtil.writeErrMsg2File(e,localLogPath);
        }
    }

}
