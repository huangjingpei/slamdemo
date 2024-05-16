package com.hiscene.hiarslamdemo;

import static com.mediasoup.msclient.Utils.getRandomString;

import static org.webrtc.ContextUtils.getApplicationContext;

import android.Manifest;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;


import com.mediasoup.msclient.ExternalVideoCapture;
import com.mediasoup.msclient.PeerConnectionUtils;
import com.mediasoup.msclient.RoomClient;
import com.mediasoup.msclient.RoomOptions;
import com.mediasoup.msclient.lv.RoomStore;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.mediasoup.droid.Logger;
import org.mediasoup.droid.MediasoupClient;
import org.webrtc.ContextUtils;
import org.webrtc.VideoFrame;

import java.io.File;

public class MSClient {
    private static String TAG = "MSClient";

    private String mRoomId, mPeerId, mDisplayName;
    private boolean mForceH264, mForceVP9;

    private RoomOptions mOptions;
    private RoomStore mRoomStore;
    private RoomClient mRoomClient;

    private ExternalVideoCapture mExternalVideoCapture;

    public void create() {
        createRoom();
    }

    public void destroy() {
        if (mRoomClient != null) {
            mRoomClient.close();
            mRoomClient = null;
        }
        if (mRoomStore != null) {
            mRoomStore = null;
        }
    }

    private void createRoom() {
        mOptions = new RoomOptions();
        loadRoomConfig();

        mRoomStore = new RoomStore();
        initRoomClient();
        checkPermission();

    }


    private void loadRoomConfig() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ContextUtils.getApplicationContext());

        // Room initial config.
        mRoomId = preferences.getString("roomId", "");
        mPeerId = preferences.getString("peerId", "");
        mDisplayName = preferences.getString("displayName", "");
        mForceH264 = preferences.getBoolean("forceH264", false);
        mForceVP9 = preferences.getBoolean("forceVP9", false);
        if (TextUtils.isEmpty(mRoomId)) {
            mRoomId = "ylpq6zan";//getRandomString(8);
            preferences.edit().putString("roomId", mRoomId).apply();
        }
        if (TextUtils.isEmpty(mPeerId)) {
            mPeerId = getRandomString(8);
            preferences.edit().putString("peerId", mPeerId).apply();
        }
        if (TextUtils.isEmpty(mDisplayName)) {
            mDisplayName = getRandomString(8);
            preferences.edit().putString("displayName", mDisplayName).apply();
        }

        // Room action config.
        mOptions.setProduce(preferences.getBoolean("produce", true));
        mOptions.setConsume(preferences.getBoolean("consume", true));
        mOptions.setForceTcp(preferences.getBoolean("forceTcp", false));
        mOptions.setUseDataChannel(preferences.getBoolean("dataChannel", true));

        // Device config.
        String camera = preferences.getString("camera", "front");
        PeerConnectionUtils.setPreferCameraFace(camera);

        // Display version number.
        Log.d(TAG, "MediasoupClient version:  " + String.valueOf(MediasoupClient.version()));

    }
    private void initRoomClient() {
        String videoInputFile = null;
        mRoomClient =new RoomClient(
                ContextUtils.getApplicationContext(), mRoomStore, mRoomId, mPeerId, mDisplayName, mForceH264, mForceVP9, mOptions, null);
    }

    public void sendData(String msg) {
        mRoomClient.sendChatMessage(msg);
    }
    private PermissionHandler permissionHandler =
            new PermissionHandler() {
                @Override
                public void onGranted() {
                    Logger.d(TAG, "permission granted");
                    if (mRoomClient != null) {
                        mRoomClient.join();
                    }
                }
            };

    private void checkPermission() {
        String[] permissions = {
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        String rationale = "Please provide permissions";
        Permissions.Options options =
                new Permissions.Options().setRationaleDialogTitle("Info").setSettingsDialogTitle("Warning");
        Permissions.check(ContextUtils.getApplicationContext(), permissions, rationale, options, permissionHandler);
    }

}
