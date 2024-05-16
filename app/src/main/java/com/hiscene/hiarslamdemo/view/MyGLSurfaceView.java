package com.hiscene.hiarslamdemo.view;

import android.content.Context;
import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.SurfaceHolder;

import com.hiar.sdk.vslam.HiarSlamInitType;
import com.hiar.sdk.vslam.HiarSlamState;
import com.hiar.sdk.vslam.ProcessListener;
import com.hiscene.hiarslamdemo.camera.CameraSource;
import com.hiscene.hiarslamdemo.camera.RendererController;
import com.hiscene.hiarslamdemo.widget.Cube;
import com.hiscene.hiarslamdemo.widget.Frame;
import com.hiscene.hiarslamdemo.widget.ModelLines;
import com.hiscene.hiarslamdemo.widget.Points;
import com.hiscene.hiarslamdemo.widget.Widget;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by li on 2017/3/13.
 */

public class MyGLSurfaceView extends GLSurfaceView implements ProcessListener {
	MyRenderer mRenderer;
	int nSlamInitType;
	private Activity activity;

	private static String TAG = "MyGLSurfaceView";

	public MyGLSurfaceView(Context context) {
		super(context);
		setEGLContextClientVersion(2);
		mRenderer = new MyRenderer();
		this.activity = (Activity) context;
		setEGLConfigChooser(new MSAAConfigChooser());
		setRenderer(mRenderer);
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		super.surfaceCreated(holder);
	}

	/**
	 * 开启相机
	 */
	public void openCamera(){
		CameraSource.Instance().openCamera(activity, CameraSource.CAMERA_DIRECTION_BACK);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		super.surfaceDestroyed(holder);
		CameraSource.Instance().closeCamera();
	}

	@Override
	public void donProcessFps(int fps) {
		Log.w(TAG, "donProcessFps: " + fps);
	}

	@Override
	public void onProcessTime(float processTimeMillis) {
		Log.w(TAG, "onProcessTime: " + processTimeMillis);

	}

	@Override
	public void onTrackingState(String state) {
		Log.w(TAG, "onTrackingState: " + state);
	}

	@Override
	public void onProcess(float[] pose, int state) {
		mfRenderingModelviewMatrix = pose;
		mbDrawModel = (state == HiarSlamState.SUCCESSFUL);
        mnState = state;
	}

	@Override
	public void onPointCloud(float[] points, boolean state){
		mfPoints = points;
		mbDrawPoints = state;
	}

	@Override
	public void setWidthAndHeight(int width, int height){
		mRenderer.setWidthAndHeight(width, height);
	}

	public void setLineArray(float[] lineArray, int[] lineNum) {
		this.lineArray = lineArray;
		this.lineNum = lineNum[0];
		mbDrawModelLines = true;
	}

	public void setSlamInitType(int nSlamInitType){
		this.nSlamInitType = nSlamInitType;
	}

	private boolean showPoints = false;
	public void setShowPoints(boolean show){
		showPoints = show;
	}
	public boolean isShowPoints(){return showPoints;}

	public float mfRenderingNearPlane = 0.1f;
	public float mfRenderingFarPlane = 10000.0f;
	public float[] mfRenderingProjectionMatrix = new float[16];
	private float[] mfRenderingModelviewMatrix = new float[16];

	public float[] mfPoints;

	float[] lineArray;
	int lineNum = 0;

	public boolean mbDrawModel = false;
	public boolean mbDrawPoints = false;
	public boolean mbDrawModelLines = false;
	public int     mnState = 0;

	private static void resizeProjectMatrix(int picW, int picH, int sW, int sH,
			float[] pro) {
		int screenWidth = sW > sH ? sW : sH;
		int screenHeight = sW > sH ? sH : sW;
		float screenRatio = screenWidth * 1.0f / screenHeight;
		float picRatio = picW * 1.0f / picH;
		float ratio = 1.0f;
		if (screenRatio > picRatio) {
			float tempHeight = picH * screenWidth * 1.0f / picW;
			ratio = tempHeight * 1.0f / screenHeight;
			pro[5] *= ratio;
			pro[9] *= ratio;
		} else if (screenRatio < picRatio) {
			float tempWidth = picW * screenHeight * 1.0f / picH;
			ratio = tempWidth * 1.0f / screenWidth;
			pro[0] *= ratio;
			pro[8] *= ratio;
		}
	}

	public void setProjection() {
		resizeProjectMatrix(CameraSource.Instance().getPreviewWidth(),
				CameraSource.Instance().getPreviewHeight(), this.getWidth(),
				this.getHeight(), mfRenderingProjectionMatrix);
	}

	class MyRenderer implements Renderer {
		Widget widget;
		Points points;
		ModelLines modelLines;
		Frame frame;

		public void setWidthAndHeight(int width, int height){
			widget.setWidth(width/3);
			widget.setHeight(height/3);
			frame.setWidth(width);
			frame.setHeight(height);
		}

		@Override
		public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
			RendererController.Instance().init();
			widget = new Cube();
			widget.setWidth(100);
			widget.setHeight(100);

			if(nSlamInitType == HiarSlamInitType.INIT_DOUBLE){
				widget.setWidth(0.1f);
				widget.setHeight(0.1f);
			}
			points = new Points();
			modelLines = new ModelLines();
			frame = new Frame();
			frame.setWidth(500);
			frame.setHeight(500);
		}

		@Override
		public void onSurfaceChanged(GL10 gl10, int width, int height) {
			RendererController.Instance().configScreen(width, height);
		}

		@Override
		public void onDrawFrame(GL10 gl10) {
			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT
					| GLES20.GL_DEPTH_BUFFER_BIT);
			RendererController.Instance().drawVideoBackground();

			if (mbDrawModel && nSlamInitType != HiarSlamInitType.INIT_MODEL) {
				GLES20.glEnable(GLES20.GL_DEPTH_TEST);
				widget.setParentMVMatirx(mfRenderingModelviewMatrix);
				widget.setProjectMatrix(mfRenderingProjectionMatrix);
				widget.draw();
				GLES20.glDisable(GLES20.GL_DEPTH_TEST);
				GLES20.glFinish();

				if(nSlamInitType == HiarSlamInitType.INIT_2DRECOG){
					frame.setParentMVMatirx(mfRenderingModelviewMatrix);
					frame.setProjectMatrix(mfRenderingProjectionMatrix);
					frame.draw();
				}
			}

			if(mbDrawPoints && showPoints) {
				GLES20.glEnable(GLES20.GL_DEPTH_TEST);
				points.setParentMVMatirx(mfRenderingModelviewMatrix);
				points.setProjectMatrix(mfRenderingProjectionMatrix);
				points.setPoints(mfPoints);
				points.draw();
				GLES20.glDisable(GLES20.GL_DEPTH_TEST);
				GLES20.glFinish();
			}

			if(mbDrawModelLines && mnState != HiarSlamState.LOST) {
				GLES20.glEnable(GLES20.GL_DEPTH_TEST);
				modelLines.setParentMVMatirx(mfRenderingModelviewMatrix);
				modelLines.setProjectMatrix(mfRenderingProjectionMatrix);
                modelLines.setModelLines(lineArray, lineNum, mnState);
				modelLines.draw();
				GLES20.glDisable(GLES20.GL_DEPTH_TEST);
				GLES20.glFinish();
			}

		}
	}
}
