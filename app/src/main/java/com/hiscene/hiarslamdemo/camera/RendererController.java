package com.hiscene.hiarslamdemo.camera;

/**
 * Created by li on 2016/11/17.
 */

public class RendererController {
	private static class SingletonHolder {
		static final RendererController _instance = new RendererController();
	}

	RendererController() {

	}

	public void init() {
		nv21Renderer = new SourceNV21Renderer();
		if (orientation != -1) {
			nv21Renderer.setDeviceOrientation(orientation);
		}
		if (cameraOrientation != -1) {
			nv21Renderer.setCameraOrientation(cameraOrientation);
		}
	}

	private int orientation = -1;
	private int cameraOrientation = -1;
	private SourceNV21Renderer nv21Renderer;

	public static RendererController Instance() {
		return SingletonHolder._instance;
	}

	public void configScreen(int width, int height) {
		synchronized (nv21Renderer) {
			nv21Renderer.configScreen(width, height);
		}
	}

	public void setDeviceOrientation(int ori) {
		orientation = ori;
		if (nv21Renderer != null) {
			nv21Renderer.setDeviceOrientation(ori);
		}
	}

	public void setCameraOrientation(int orientation) {
		cameraOrientation = orientation;
		if (nv21Renderer != null) {
			nv21Renderer.setCameraOrientation(orientation);
		}
	}

	public void drawVideoBackground() {
		synchronized (nv21Renderer) {
			nv21Renderer.draw();
		}
	}

	public void onFrame(byte[] data, int width, int height) {
		synchronized (nv21Renderer) {
			nv21Renderer.setPictureSize(width, height);
			nv21Renderer.setNV21Data(data, width, height);
		}
	}
}
