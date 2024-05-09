package com.hiscene.hiarslamdemo.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * 权限管理工具
 * Created by jucf on 2017/6/23.
 */
public class MyPermissionManager {

    private static MyPermissionManager INSTANCE = new MyPermissionManager();

    public final static int PERMISSION_REQUEST_CODE_CAMERA = 10;    // 摄像头

    public final static int PERMISSION_REQUEST_CODE_RECORD_AUDIO = 20;  // 录音

    public final static int PERMISSION_REQUEST_CODE_RECORD_AUDIO_PRE = 21;  // 录音

    public final static int PERMISSION_REQUEST_CODE_EXTERNAL_STORAGE = 30;  // 存储

    private int flagStoragePermission =-1;    // 存储权限标志位，用于区分哪个功能申请的权限

    private Object extral;  // 其他参数

    public static MyPermissionManager instance() {
        return INSTANCE;
    }

    /**
     * 23 以上版本才需要动态申请权限
     *
     * @return
     */
    public boolean isNeedDynamicRequestPermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 检查是否拥有指定的所有权限
     */
    public boolean checkPermissionAllGranted(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }

    public void requestPermission(Activity activity, String[] permissions, int requestCode) {
        // 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
        ActivityCompat.requestPermissions(
                activity, permissions, requestCode);
    }

    /**
     * 检测摄像头权限
     * @param activity
     * @return
     */
    public boolean checkCameraPermission(Activity activity){
        if (MyPermissionManager.instance().isNeedDynamicRequestPermission()) {

            String[] permissions = new String[] {Manifest.permission.CAMERA};
            /**
             * 第 1 步: 检查是否有相应的权限
             */
            boolean isAllGranted = MyPermissionManager.instance().checkPermissionAllGranted(activity,permissions);
            if (isAllGranted) {
                return true;
            }else{
                /**
                 * 第 2 步: 请求权限
                 */
                MyPermissionManager.instance().requestPermission(activity,permissions,
                        MyPermissionManager.PERMISSION_REQUEST_CODE_CAMERA);
                return false;
            }
        }else{
           return true;
        }
    }

    /**
     * 提前检测检查音频权限
     *
     * @return
     */
    public boolean checkPreRecordAudioPermission(Activity activity) {
        // 检测音频权限
        if (MyPermissionManager.instance().isNeedDynamicRequestPermission()) {
            String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO};
            boolean isAllGranted = MyPermissionManager.instance().checkPermissionAllGranted(activity, permissions);
            if (isAllGranted) {
                return true;
            } else {
                MyPermissionManager.instance().requestPermission(activity, permissions,
                        MyPermissionManager.PERMISSION_REQUEST_CODE_RECORD_AUDIO_PRE);
                return false;
            }
        } else {
            return true;
        }
    }

    /**
     * 检查音频权限
     *
     * @return
     */
    public boolean checkRecordAudioPermission(Activity activity) {
        // 检测音频权限
        if (MyPermissionManager.instance().isNeedDynamicRequestPermission()) {
            String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
            boolean isAllGranted = MyPermissionManager.instance().checkPermissionAllGranted(activity, permissions);
            if (isAllGranted) {
                return true;
            } else {
                MyPermissionManager.instance().requestPermission(activity, permissions,
                        MyPermissionManager.PERMISSION_REQUEST_CODE_RECORD_AUDIO);
                return false;
            }
        } else {
            return true;
        }
    }

    /**
     * 检查存储权限
     *
     * @return
     */
    public boolean checkStoragePermission(Activity activity, int flagStoragePermission) {
        this.flagStoragePermission = flagStoragePermission;
        if (MyPermissionManager.instance().isNeedDynamicRequestPermission()) {
            String[] permissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
            boolean isAllGranted = MyPermissionManager.instance().checkPermissionAllGranted(activity, permissions);
            if (isAllGranted) {
                return true;
            } else {
                requestPermission(activity, permissions,
                        MyPermissionManager.PERMISSION_REQUEST_CODE_EXTERNAL_STORAGE);
                return false;
            }
        } else {
            return true;
        }
    }

    /**
     * 检查存储权限
     *
     * @return
     */
    public boolean checkStoragePermission(Activity activity, int flagStoragePermission, Object extral) {
        this.flagStoragePermission = flagStoragePermission;
        this.extral = extral;
        if (MyPermissionManager.instance().isNeedDynamicRequestPermission()) {
            String[] permissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
            boolean isAllGranted = MyPermissionManager.instance().checkPermissionAllGranted(activity, permissions);
            if (isAllGranted) {
                return true;
            } else {
                requestPermission(activity, permissions,
                        MyPermissionManager.PERMISSION_REQUEST_CODE_EXTERNAL_STORAGE);
                return false;
            }
        } else {
            return true;
        }
    }


    public boolean isAllGranted(int[] grantResults) {
        boolean isAllGranted = true;
        // 判断是否所有的权限都已经授予了
        for (int grant : grantResults) {
            if (grant != PackageManager.PERMISSION_GRANTED) {
                isAllGranted = false;
                break;
            }
        }
        if (isAllGranted) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 打开 APP 的详情设置
     */
    public void openAppDetails(final Context context, String permissionDescription) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("此功能需要访问 "+permissionDescription+"，请到 “应用信息 -> 权限” 中授予！");
        builder.setPositiveButton("手动授权", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                context.startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }



    public Object getExtral() {
        return extral;
    }

    public int getFlagStoragePermission() {
        return flagStoragePermission;
    }
}
