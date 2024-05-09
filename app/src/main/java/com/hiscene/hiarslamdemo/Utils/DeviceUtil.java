package com.hiscene.hiarslamdemo.Utils;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

/**
 * Created by lichong on 2018/7/4.
 *
 * @ Email lichongmac@163.com
 */
public class DeviceUtil {

    public static String getDeviceID(Context context) {
        String tac = "";
        try {
            final TelephonyManager manager = (TelephonyManager) context.getSystemService(Context
                    .TELEPHONY_SERVICE);
            if (manager.getDeviceId() == null || manager.getDeviceId().equals("")) {
                if (Build.VERSION.SDK_INT >= 23) {
                    tac = manager.getDeviceId(0);
                }
            } else {
                tac = manager.getDeviceId();
            }
        } catch (Exception e) {


        }
        return tac;
    }
}
