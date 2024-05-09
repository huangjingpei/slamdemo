package com.hiscene.hiarslamdemo.listener;

/**
 * Created by li on 2017/3/13.
 */

public interface ProcessListener {
	void onProcess(float[] pose, int state);
	void onPointCloud(float[] points, boolean state);
	void setWidthAndHeight(int width, int height);
}
