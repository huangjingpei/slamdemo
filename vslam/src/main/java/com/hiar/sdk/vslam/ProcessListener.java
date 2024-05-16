package com.hiar.sdk.vslam;

/**
 * Created by li on 2017/3/13.
 */

public interface ProcessListener {
	void donProcessFps(int fps);
	void onProcessTime(float processTimeMillis);
	void onTrackingState(String state);
	void onProcess(float[] pose, int state);
	void onPointCloud(float[] points, boolean state);
	void setWidthAndHeight(int width, int height);
}
