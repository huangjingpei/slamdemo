package com.hiscene.hiarslamdemo;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

import com.hiar.sdk.vslam.AlgWrapper;
import com.hiar.sdk.vslam.HiarSlamImageFormat;
import com.hiar.sdk.vslam.HiarSlamInitType;
import com.hiar.sdk.vslam.HiarSlamPose;
import com.hiar.sdk.vslam.HiarSlamState;
import com.hiscene.hiarslamdemo.camera.CameraSource;
import com.hiscene.hiarslamdemo.camera.RendererController;
import com.hiscene.hiarslamdemo.listener.NewFrameListener;
import com.hiscene.hiarslamdemo.listener.ProcessListener;

public class ProcessRunnable implements NewFrameListener {

	HiarSlamPose pose = new HiarSlamPose();
	Activity activity;
	AlgWrapper slam;
	TextView fpsText,trackingStateText,processTimeText;
	ProcessListener processListener;
	boolean needProcess = false;
	int nSlamInitType;

	public ProcessListener getProcessListener() {
		return processListener;
	}

	public void setProcessListener(ProcessListener processListener) {
		this.processListener = processListener;
	}

	public float[] getMatRT() {
		return pose.extrinsics;
	}

	public void setSlamInitType(int nSlamInitType){
		this.nSlamInitType = nSlamInitType;
	}

	public boolean isNeedProcess() {
		return needProcess;
	}

	public void setNeedProcess(boolean needProcess) {
		this.needProcess = needProcess;
	}

	public ProcessRunnable(Activity activity_, AlgWrapper slam_, TextView fps_, TextView trackingState_, TextView processTime_) {
		activity = activity_;
		slam = slam_;
		fpsText = fps_;
		trackingStateText = trackingState_;
		processTimeText = processTime_;
		CameraSource.Instance().setNewFrameListener(this);
	}

	private String getStatusString(int nRtn, int state) {
		String trackingState;

		if (nRtn < 0)
			trackingState = "Error in calling Process";
		else
		{
			switch (state)
			{
				case HiarSlamState.WAIT_FOR_RECOG_IMAGE:
					trackingState = "Wait for Recog";
					break;
				case HiarSlamState.START:
					trackingState = "Not initialized";
					break;
				case HiarSlamState.SUCCESSFUL:
					trackingState = "Tracking";
					break;
				case HiarSlamState.LOST:
					trackingState = "Lost";
					break;
				default:
					trackingState = "<wrong>";
					break;
			}
		}
		return trackingState;
	}

	@Override
	public void onNewFrame(byte[] data, int width, int height) {

		if (needProcess) {
			boolean bRtn;
			int nRtn;

			Long time_begin = System.currentTimeMillis();
			//处理当前帧，获取设备姿态信息
			Log.d("SLAM","hello,"+pose.extrinsics[0]+","+pose.extrinsics[1]+","+pose.extrinsics[2]+","+pose.extrinsics[3]
					+","+pose.extrinsics[4] +","+pose.extrinsics[5]+","+pose.extrinsics[6]+","+pose.extrinsics[7]
					+","+pose.extrinsics[8] +","+pose.extrinsics[9]+","+pose.extrinsics[10]+","+pose.extrinsics[11]
					+","+pose.extrinsics[12] +","+pose.extrinsics[13]+","+pose.extrinsics[14]+","+pose.extrinsics[15]);
			nRtn = slam.ProcessFrame(data, HiarSlamImageFormat.NV21, pose);
			final float processTime = System.currentTimeMillis() - time_begin;
			final int fps = (int)(1000.0f / processTime);
			final String trackingState = getStatusString(nRtn, pose.getTrackingState());

			if(nSlamInitType == HiarSlamInitType.INIT_2DRECOG){
				int[] index = new int[1];
				int[] imageWidth = new int[1];
				int[] imageHeight = new int[1];
				slam.GetInitModelInfo(index, imageWidth, imageHeight);
				processListener.setWidthAndHeight(imageWidth[0], imageHeight[0]);
			}

			bRtn = (nRtn == 0);
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					fpsText.setText("Fps:" + fps);
					processTimeText.setText("processTime:"+ processTime);
					if(trackingState.equals("Lost")) {
						trackingStateText.setTextColor(Color.RED);
					} else {
						trackingStateText.setTextColor(Color.GREEN);
					}
					trackingStateText.setText(trackingState);
				}
			});

			//将姿态信息转换为OpenGL坐标系下的ModelView Matrix
			nRtn = slam.ConvertCameraExtrinsicsToGL(pose.extrinsics, pose.viewMatrix);
			processListener.onProcess(pose.viewMatrix, pose.getTrackingState());
		}

		float[] points = slam.GetPointCloudPoints();
		if (points != null && pose.getTrackingState() == HiarSlamState.SUCCESSFUL) {
			processListener.onPointCloud(points, true);
		} else {
			processListener.onPointCloud(null, false);
		}

		RendererController.Instance().onFrame(data, width, height);
	}

}
