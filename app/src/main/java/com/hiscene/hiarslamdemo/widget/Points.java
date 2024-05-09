package com.hiscene.hiarslamdemo.widget;

import android.opengl.GLES20;

import com.hiscene.hiarslamdemo.Utils.OpenglUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Points extends Widget {

    private int mProgram;
    private int muMVPMatrixHandle;
    private int maPositionHandle;
    private String mVertexShader = "uniform mat4 uMVPMatrix; "
            + "attribute vec3 aPosition;  " + "attribute vec4 aColor; "
            + "varying  vec4 vColor;  " + "void main()  {"
            + "   gl_Position = uMVPMatrix * vec4(aPosition,1); "
            + "   gl_PointSize = 5.0; vColor = vec4(0.0f, 1.0f, 0.0f, 1.0f);" + "}";
    private String mFragmentShader = "precision mediump float;"
            + "varying vec3 vPosition;" + "varying vec4 vColor;"
            + "void main() {  " + "   gl_FragColor = vColor;" + "}";

    private FloatBuffer mVertexBuffer;
	private float[] vertices;

    public Points() {
        initShader();
    }

    public void setPoints(float[] points){
		vertices = points;
		initVertexData();
	}

    public void initVertexData() {
        // ============================begin============================


        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);
        // ============================end============================
    }

    public void initShader() {
        mProgram = OpenglUtil.createProgramFromShaderSrc(mVertexShader, mFragmentShader);
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    public void draw() {
        GLES20.glUseProgram(mProgram);
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false,
                getFinalMatrix(), 0);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT,
                false, 3 * 4, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, vertices.length/3);
        GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glUseProgram(0);
    }
}
