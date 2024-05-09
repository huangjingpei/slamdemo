package com.hiscene.hiarslamdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.hiar.sdk.vslam.AlgWrapper;
import com.hiar.sdk.vslam.HiarSlamInitType;
import com.hiar.sdk.vslam.HiarSlamMode;
import com.hiar.sdk.vslam.HiarSlamResultCode;
import com.hiscene.hiarslamdemo.Utils.TimeUtils;
import com.hiscene.hiarslamdemo.bean.EdgeTrackerMePara;
import com.hiscene.hiarslamdemo.camera.CameraSource;
import com.hiscene.hiarslamdemo.view.MyGLSurfaceView;

import java.io.File;

import androidx.annotation.Nullable;

public class CameraActivity extends Activity {

    private static String TAG = "CameraActivity";

    public static CameraActivity instance = null;

    MyGLSurfaceView myGLSurfaceView;
    ProcessRunnable processRunnable;
    AlgWrapper slam;
    int nSlamInitType = HiarSlamInitType.INIT_SINGLE; //选择sdk初始化方法
    int nSlamMode = HiarSlamMode.BALANCE; //选择sdk工作模式

    TextView splashText;
    TextView fpsText, trackingStateText, processTimeText;
    Button btnSave, btnReset, btnShowPoints, btnSavePose;
    FrameLayout frameLayout;

    String caoName;
    String dbFileName;
    String datFileName;

    public static void actionStart(Context context, int nInitType,
                                   int nMode, String caoName, String dbFileName ,String datFileName) {
        Log.d(TAG, "actionStart() called with: context = [" + context + "], nInitType = [" + nInitType + "], nMode = [" + nMode + "], caoName = [" + caoName + "], dbFileName = [" + dbFileName + "], datFileName = [" + datFileName + "]");
        Intent intent = new Intent(context, CameraActivity.class);
        intent.putExtra("slamInitType", nInitType);
        intent.putExtra("slamMode", nMode);
        intent.putExtra("caoName", caoName);
        intent.putExtra("dbFileName", dbFileName);
        intent.putExtra("datFileName", datFileName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 无title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); // 全屏

        instance = this;
        myGLSurfaceView = new MyGLSurfaceView(this);
        myGLSurfaceView.openCamera();

        splashText = new TextView(this);
        frameLayout = new FrameLayout(this);
        frameLayout.addView(myGLSurfaceView);

        splashText.setText("点击屏幕开始");
        splashText.setTextSize(32);
        splashText.setTextColor(Color.RED);
        frameLayout.addView(splashText);
        fpsText = new TextView(this);
        fpsText.setTextSize(20);
        fpsText.setX(0);
        fpsText.setY(150);
        fpsText.setTextColor(Color.GREEN);
        frameLayout.addView(fpsText);
        processTimeText = new TextView(this);
        processTimeText.setTextSize(20);
        processTimeText.setTextColor(Color.GREEN);
        processTimeText.setX(0);
        processTimeText.setY(250);
        frameLayout.addView(processTimeText);
        trackingStateText = new TextView(this);
        trackingStateText.setTextSize(20);
        trackingStateText.setTextColor(Color.GREEN);
        trackingStateText.setX(0);
        trackingStateText.setY(350);
        frameLayout.addView(trackingStateText);
        btnSave = new Button(this);
        btnSave.setText("Save");
        btnSave.setX(0);
        btnSave.setY(0);
        btnSave.setLayoutParams(new TextSwitcher.LayoutParams(210, 150));
        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String timeStr =  "t"+ TimeUtils.millis2Date(System.currentTimeMillis());
                slam.SaveAreaDesc(Contants.slamResPath+File.separator+
                        "point_cloud"+File.separator+
                        timeStr+".dat");
            }
        });
        frameLayout.addView(btnSave);
        btnReset = new Button(this);
        btnReset.setText("Reset");
        btnReset.setX(250);
        btnReset.setY(0);
        btnReset.setLayoutParams(new TextSwitcher.LayoutParams(210, 150));
        btnReset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                slam.Reset();
            }
        });
        frameLayout.addView(btnReset);
        btnShowPoints = new Button(this);
        btnShowPoints.setText("Points");
        btnShowPoints.setX(500);
        btnShowPoints.setY(0);
        btnShowPoints.setLayoutParams(new TextSwitcher.LayoutParams(300, 150));
        btnShowPoints.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (myGLSurfaceView.isShowPoints()) {
                    myGLSurfaceView.setShowPoints(false);
                } else {
                    myGLSurfaceView.setShowPoints(true);
                }
            }
        });
        frameLayout.addView(btnShowPoints);

        btnSavePose = new Button(this);
        btnSavePose.setText("SavePose");
        btnSavePose.setX(800);
        btnSavePose.setY(0);
        btnSavePose.setLayoutParams(new TextSwitcher.LayoutParams(400, 150));
        btnSavePose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(nSlamInitType == HiarSlamInitType.INIT_MODEL) {
                    String name = caoName;
                    if (name.contains(".")) {
                        name = name.substring(0, name.indexOf("."));
                    }
                    name = Contants.slamResPath + File.separator + name + ".yaml";
                    Log.d(TAG,"name=="+name);
                    EdgeTrackerMePara me = new EdgeTrackerMePara.Builder().build();
                    float[] matRt = processRunnable.getMatRT();
                    double[] pose = new double[12];
                    if (matRt.length > 0) {
                        for (int i = 0; i < 12; ++i) {
                            pose[i] = matRt[i];
                        }
                        slam.ReadYamlFile(name, me);
                        slam.WriteYamlFileWithPose(name, me, pose);
                    }
                }
            }
        });
        frameLayout.addView(btnSavePose);

        setContentView(frameLayout);
        splashText.setVisibility(View.GONE);
        fpsText.setVisibility(View.GONE);
        btnSave.setVisibility(View.GONE);
        btnReset.setVisibility(View.GONE);
        btnShowPoints.setVisibility(View.GONE);
        btnSavePose.setVisibility(View.GONE);
        trackingStateText.setVisibility(View.GONE);
        slam = new AlgWrapper();
        processRunnable = new ProcessRunnable(this, slam, fpsText, trackingStateText,
                processTimeText);
        processRunnable.setProcessListener(myGLSurfaceView);

        //processThread = new Thread(processRunnable);
        nSlamInitType = getIntent().getIntExtra("slamInitType", HiarSlamInitType.INIT_SINGLE);
        nSlamMode = getIntent().getIntExtra("slamMode", HiarSlamMode.BALANCE);
        caoName = getIntent().getStringExtra("caoName");
        dbFileName = getIntent().getStringExtra("dbFileName");
        datFileName=getIntent().getStringExtra("datFileName");
        processRunnable.setSlamInitType(nSlamInitType);
        myGLSurfaceView.setSlamInitType(nSlamInitType);
        //获取HiarSdk版本号
        slam.GetVersion();

        slam.GetPreferredSystemCameraIntrinsics(CameraSource.Instance().getPreviewWidth(), CameraSource.Instance().getPreviewHeight());

        splashText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!slam.bInit) {
                    int nRtn;
                    String initFilePath = Contants.slamResPath + File.separator +
                            "HiARRecog.db";

                    switch (nSlamInitType) {
                        case HiarSlamInitType.INIT_SINGLE:
                        case HiarSlamInitType.INIT_DOUBLE:
                            initFilePath = null;
                            break;
                        case HiarSlamInitType.INIT_2DRECOG:
                            initFilePath = Contants.slamResPath + File.separator + dbFileName;
                            break;
                        case HiarSlamInitType.INIT_USE_AREA_DESC:
                            initFilePath=Contants.slamResPath+File.separator+"point_cloud"
                                    +File.separator+datFileName;
                            break;
                        case HiarSlamInitType.INIT_MODEL:
                            initFilePath = Contants.slamResPath + File.separator +  caoName;
                            break;
                    }
                    slam.SetVocFilePath(Contants.slamResPath + File.separator + "HiARVoc.dat");
                    //初始化HiarSdk
                    nRtn = slam.Create(nSlamInitType, initFilePath, nSlamMode);
                    if (nRtn == HiarSlamResultCode.OK) {
                        //HiarSdk初始化成功
                        //获取OpenGL坐标系下的Projection Matrix
                        slam.ConvertCameraIntrinsicsToGL(myGLSurfaceView.mfRenderingNearPlane, myGLSurfaceView
                                .mfRenderingFarPlane, myGLSurfaceView.mfRenderingProjectionMatrix);
                        myGLSurfaceView.setProjection();
                        Get3DLine();
                    }
                }
                
                processRunnable.setNeedProcess(true);
                // processThread.start();
                splashText.setVisibility(View.GONE);
                //fpsText.setVisibility(View.VISIBLE);
                btnSave.setVisibility(View.VISIBLE);
                btnReset.setVisibility(View.VISIBLE);
                //btnShowPoints.setVisibility(View.VISIBLE);
                //btnSavePose.setVisibility(View.VISIBLE);
                trackingStateText.setVisibility(View.VISIBLE);

                if(nSlamInitType == HiarSlamInitType.INIT_MODEL) {
                    btnSavePose.setVisibility(View.VISIBLE);
                }
            }
        });
        splashText.setVisibility(View.VISIBLE);
    }

    private void Get3DLine() {
        if(nSlamInitType == HiarSlamInitType.INIT_MODEL) {
            int[] lineNum = new int[1];
            slam.Get3DLineCount(lineNum);
            float[] lineArray = new float[lineNum[0] * 6];
            slam.Get3DLines(lineArray, lineNum);
            myGLSurfaceView.setLineArray(lineArray, lineNum);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        processRunnable.setNeedProcess(false);
        slam.Destroy();
        processRunnable.setProcessListener(null);
        //processThread.interrupt();
    }
}
