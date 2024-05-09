package com.hiscene.hiarslamdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hiar.sdk.vslam.AlgWrapper;
import com.hiscene.hiarslamdemo.Utils.ExceptionUtil;
import com.hiscene.hiarslamdemo.Utils.FileUtil;
import com.hiscene.hiarslamdemo.bean.EdgeTrackerMePara;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingParamsActivity extends AppCompatActivity {

    public static void startActivityForResult(Activity activity) {
        activity.startActivityForResult(new Intent(activity, SettingParamsActivity.class), 10);
    }

    public static void startActivityForResult(Context context, String caoName) {
        Intent intent = new Intent(context, SettingParamsActivity.class);
        intent.putExtra("CaoName", caoName);
        context.startActivity(intent);
    }

    EdgeTrackerMePara edgeTrackerMePara;


    @BindView(R.id.tv_cao_name)
    TextView nameTv;

    @BindView(R.id.sb_mask_size)
    SeekBar maskSizeSb;
    @BindView(R.id.et_mask_size)
    EditText maskSizeEt;

    @BindView(R.id.sb_mask_number)
    SeekBar maskNumberSb;
    @BindView(R.id.et_mask_number)
    EditText maskNumberEt;

    @BindView(R.id.sb_range)
    SeekBar rangeSb;
    @BindView(R.id.et_range)
    EditText rangeEt;

    @BindView(R.id.sb_threshold)
    SeekBar thresholdSb;
    @BindView(R.id.et_threshold)
    EditText thresholdEt;

    @BindView(R.id.sb_mu1)
    SeekBar mu1Sb;
    @BindView(R.id.et_mu1)
    EditText mu1Et;

    @BindView(R.id.sb_mu2)
    SeekBar mu2Sb;
    @BindView(R.id.et_mu2)
    EditText mu2Et;

    @BindView(R.id.sb_sample_step)
    SeekBar sampleStepSb;
    @BindView(R.id.et_sample_step)
    EditText sampleStepEt;

    @BindView(R.id.sb_percentage_gd)
    SeekBar percentageGdSb;
    @BindView(R.id.et_percentage_gd)
    EditText percentageGdEt;

    @BindView(R.id.sb_percentage_pet)
    SeekBar percentagePetSb;
    @BindView(R.id.et_percentage_pet)
    EditText percentagePetEt;

    private String caoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_params);
        ButterKnife.bind(this);

        caoName = getIntent().getStringExtra("CaoName");
        if (TextUtils.isEmpty(caoName))
            ExceptionUtil.throwRunnintException("caoName is null!");

        initView();

        initData();

        showData();

    }


    private void initView() {

        nameTv.setText(caoName);
        maskSizeSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                maskSizeEt.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        maskNumberSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                maskNumberEt.setText(String.valueOf(i * 10));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        rangeSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                rangeEt.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        thresholdSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                thresholdEt.setText(String.valueOf(i * 10000));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mu1Sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mu1Et.setText(String.valueOf(i / 10f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mu2Sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mu2Et.setText(String.valueOf(i / 10f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sampleStepSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                sampleStepEt.setText(String.valueOf(i * 10));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        percentageGdSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                percentageGdEt.setText(String.valueOf(i / 10.0f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        percentagePetSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                percentagePetEt.setText(String.valueOf(i / 10.0f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    private void initData() {
        FileUtil.createNewFile(getSaveFilePath());
        edgeTrackerMePara = new EdgeTrackerMePara.Builder().build();
        AlgWrapper.ReadYamlFile(getSaveFilePath(), edgeTrackerMePara);

    }

    private void showData() {
        if (edgeTrackerMePara == null)
            return;

        maskSizeEt.setText(edgeTrackerMePara.getMaskSize() + "");
        maskNumberEt.setText(edgeTrackerMePara.getMaskNumber() + "");
        rangeEt.setText(edgeTrackerMePara.getRange() + "");
        thresholdEt.setText(edgeTrackerMePara.getThreshold() + "");
        mu1Et.setText(edgeTrackerMePara.getMu1() + "");
        mu2Et.setText(edgeTrackerMePara.getMu2() + "");
        sampleStepEt.setText(edgeTrackerMePara.getSampleStep() + "");
        percentageGdEt.setText(edgeTrackerMePara.getPercentageGd() + "");
        percentagePetEt.setText(edgeTrackerMePara.getPercentagePet()+"");

    }


    public void back(View view) {
        finish();
    }

    public void save(View view) {
        String maskSize = maskSizeEt.getText().toString().trim();
        String maskNumber = maskNumberEt.getText().toString().trim();
        String range = rangeEt.getText().toString().trim();
        String threshold = thresholdEt.getText().toString().trim();
        String mu1 = mu1Et.getText().toString().trim();
        String mu2 = mu2Et.getText().toString().trim();
        String sampleStep = sampleStepEt.getText().toString().trim();
        String percentageGd = percentageGdEt.getText().toString().trim();
        String percentagePet = percentagePetEt.getText().toString().trim();

        edgeTrackerMePara = new EdgeTrackerMePara.Builder()
                .setName(caoName)
                .setMaskSize(Integer.valueOf(maskSize))
                .setMaskNumber(Integer.valueOf(maskNumber))
                .setRange(Integer.valueOf(range))
                .setThreshold(Integer.valueOf(threshold))
                .setMu1(Double.valueOf(mu1))
                .setMu2(Double.valueOf(mu2))
                .setSampleStep(Integer.valueOf(sampleStep))
                .setPercentageGd(Double.valueOf(percentageGd))
                .setPercentagePet(Double.valueOf(percentagePet))
                .build();


        AlgWrapper.WriteYamlFile(getSaveFilePath(), edgeTrackerMePara);

        finish();
    }

    @NonNull
    private String getSaveFilePath() {
        if (caoName.contains("."))
            caoName = caoName.substring(0, caoName.indexOf("."));
        return Contants.slamResPath + File.separator + caoName + ".yaml";
    }
}
