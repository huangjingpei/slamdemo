package com.hiscene.hiarslamdemo;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.hiar.sdk.vslam.HiarSlamInitType;
import com.hiscene.hiarslamdemo.Utils.FilePath;
import com.hiscene.hiarslamdemo.Utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by liwenfei on 2017/5/4.
 */

public class GuideActivity extends Activity {
    Button cameraSoure;
    Spinner slamInitType, slamMode;
    String assetDirName = "slam_res";
    int nInitType = 0, nMode = 0;

    @BindView(R.id.ll_edge_tracker)
    LinearLayout edgeTrackerLayout;

    @BindView(R.id.spinner_cao)
    Spinner caosSpinner;
    private String caoName;
    private List<String> caoList= new ArrayList<>();

    private String dbFileName;
    @BindView(R.id.spinner_2d_dbs)
    Spinner dbsSpineer;
    private List<String> dbsList=new ArrayList<>();

    private String datFileName;
    @BindView(R.id.spinner_dat_points)
    Spinner datsSpinner;
    private List<String> datsList=new ArrayList<>();
    private ArrayAdapter caosAdapter;
    private ArrayAdapter dbsAdapter;
    private ArrayAdapter datsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);

        Contants.slamResPath = getExternalCacheDir().getPath() + File.separator + assetDirName;
        FilePath.createDirFilePath(Contants.slamResPath);

        cameraSoure = (Button) findViewById(R.id.camera);
        slamInitType = (Spinner) findViewById(R.id.SlamInitType);
        slamMode = (Spinner) findViewById(R.id.SlamMode);
        slamInitType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                nInitType = i;
                edgeTrackerLayout.setVisibility(View.GONE);
                dbsSpineer.setVisibility(View.GONE);
                datsSpinner.setVisibility(View.GONE);
                switch (i){
                    case HiarSlamInitType.INIT_SINGLE:
                    case HiarSlamInitType.INIT_DOUBLE:
                    case HiarSlamInitType.INIT_ATLAS:
                        break;
                    case HiarSlamInitType.INIT_2DRECOG:
                        dbsSpineer.setVisibility(View.VISIBLE);
                        break;
                    case HiarSlamInitType.INIT_USE_AREA_DESC:
                        datsSpinner.setVisibility(View.VISIBLE);
                        break;
                    case HiarSlamInitType.INIT_MODEL:
                        edgeTrackerLayout.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        slamMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                nMode = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        new InitTask().execute();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initView() {

        caosAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item, caoList);
            caosSpinner.setAdapter(caosAdapter);
            caosSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    caoName = caoList.get(i);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }

            });

        dbsAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item, dbsList);
            dbsSpineer.setAdapter(dbsAdapter);
            dbsSpineer .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    dbFileName = dbsList.get(i);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }

            });

        datsAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,datsList);
            datsSpinner.setAdapter(datsAdapter);
            datsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    datFileName=datsList.get(i);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

    }

    private void initFilePath() {
        datsList.clear();
        caoList.clear();
        dbsList.clear();
        addFileList(Contants.slamResPath);
        if (caosAdapter==null)
            return;
        caosAdapter.notifyDataSetChanged();
        dbsAdapter.notifyDataSetChanged();
        datsAdapter.notifyDataSetChanged();

    }

    private void addFileList(String dirPath) {
        String[] files = new File(dirPath).list();
        if (files==null||0==files.length)
            return;
        for (int i = 0; i < files.length; i++) {
            if (files[i].endsWith(".ham")) {
                caoList.add(files[i]);
            }else if (files[i].endsWith(".db")){
                dbsList.add(files[i]);
            }else if (dirPath.contains("point_cloud")&&files[i].endsWith(".dat")){
                datsList.add(files[i]);
            }else if(new File(Contants.slamResPath+File.separator+files[i]).isDirectory()){
                addFileList(Contants.slamResPath+File.separator+files[i]);
            }
        }
    }

    public void CameraSource(View view) {
        CameraActivity.actionStart(this,nInitType,nMode,caoName,dbFileName,datFileName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    class InitTask extends AsyncTask {

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            cameraSoure.setVisibility(View.VISIBLE);

            String[] slamInitTypes = {"SINGLE", "2D_RECOG", "USE_AREA_DESC", "DOUBLE", "ATLAS", "MODEL"};
            ArrayAdapter InitTypeAdapter = new ArrayAdapter<String>(GuideActivity.this, android.R
                    .layout.simple_spinner_item, slamInitTypes);
            InitTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            slamInitType.setAdapter(InitTypeAdapter);
            slamInitType.setVisibility(View.VISIBLE);

            String[] slamModes = {"DEFAULT(暂不可用)", "HIGH_PRECISION", "BALANCE", "PERFORMANCE"};
            ArrayAdapter ModeAdapter = new ArrayAdapter<String>(GuideActivity.this, android.R
                    .layout.simple_spinner_item, slamModes);
            ModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            slamMode.setAdapter(ModeAdapter);
            slamMode.setVisibility(View.VISIBLE);
            slamMode.setSelection(2);
        }


        @Override
        protected Object doInBackground(Object[] objects) {
            FileUtil.CopyAssets2SDcard(GuideActivity.this,
                    assetDirName, Contants.slamResPath+File.separator, false);
            initFilePath();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initView();
                }
            });
            return null;
        }
    }

    public void go2SettingParams(View view) {
        SettingParamsActivity.startActivityForResult(this, caoName);
    }
}
