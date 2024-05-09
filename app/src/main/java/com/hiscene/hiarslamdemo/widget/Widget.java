package com.hiscene.hiarslamdemo.widget;

import android.opengl.Matrix;

/**
 * Created by li on 2016/10/19.
 */

public abstract class Widget {
	private float[] mMVPMatrix = new float[16];
	protected float width = 1.0f;
	protected float height = 1.0f;
	private float[] parentMVMatirx = new float[16];
	private float[] projectMatrix;
	private boolean fitToDraw = false;

	public Widget() {
		fitToDraw = false;
	}

	public void setFitToDraw(boolean draw) {
		fitToDraw = draw;
	}

	public boolean fitToDraw() {
		return fitToDraw;
	}

	public void setParentMVMatirx(float[] matrix) {
		parentMVMatirx = matrix;
	}

	public void setProjectMatrix(float[] projectMatrix) {
		this.projectMatrix = projectMatrix;
	}

	public void setWidth(float w) {
		width = w;
	}

	public void setHeight(float h) {
		height = h;
	}

	float[] getFinalMatrix() {
		Matrix.setIdentityM(mMVPMatrix, 0);
		Matrix.scaleM(mMVPMatrix, 0, width, height, width);
		Matrix.multiplyMM(mMVPMatrix, 0, parentMVMatirx, 0, mMVPMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, projectMatrix, 0, mMVPMatrix, 0);
		return mMVPMatrix;
	}

	abstract public void draw();
}
