package com.hiscene.hiarslamdemo.widget;

import android.opengl.GLES20;

import com.hiscene.hiarslamdemo.Utils.OpenglUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Features extends Widget {

    private int mProgram;
    private int muMVPMatrixHandle;
    private int maPositionHandle;
    private int maColorHandle;
    private String mVertexShader = "uniform mat4 uMVPMatrix; "
            + "attribute vec3 aPosition;  " + "attribute vec4 aColor; "
            + "varying  vec4 vColor;  " + "void main()  {"
            + "   gl_Position = uMVPMatrix * vec4(aPosition,1); "
            + "   gl_PointSize = 5.0; vColor = aColor;" + "}";
    private String mFragmentShader = "precision mediump float;"
            + "varying vec3 vPosition;" + "varying vec4 vColor;"
            + "void main() {  " + "   gl_FragColor = vColor;" + "}";

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mColorBuffer;
    private int vCount = 0;
	private float[] vertices;
    private float[] mfKeyPoints;
    private boolean[] mbMappeds;

    public Features() {
        initShader();
    }

    public void setFeatures(float[] keypoints, boolean[] mappeds){
        mfKeyPoints = keypoints;
        mbMappeds = mappeds;
		initVertexData();
	}

    public void initVertexData() {
        // ============================begin============================
        vCount = mbMappeds.length;
        vertices = new float[vCount * 3];
        for(int i = 0; i < vCount; ++i){
            vertices[3 * i] = mfKeyPoints[2 * i];
            vertices[3 * i + 1] = mfKeyPoints[2 * i + 1];
            vertices[3 * i + 2] = 0.f;
        }

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);
        // ============================end============================

        float colors_original[] = new float[] {
                0.0f, 1.0f, 0.0f, 1f, //green
                1.0f, 0.0f, 0.0f, 1f, //red
        };

        float colors[] =new float[vCount * 4];
        for(int i = 0; i < vCount; ++i)
        {
            if(mbMappeds[i]) {
                for (int j = 0; j < 4; ++j) {
                    colors[i * 4 + j] = colors_original[j];
                }
            }
            else {
                for (int j = 0; j < 4; ++j) {
                    colors[i * 4 + j] = colors_original[j + 4];
                }
            }
        }
        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        mColorBuffer = cbb.asFloatBuffer();
        mColorBuffer.put(colors);
        mColorBuffer.position(0);
    }

    public void initShader() {
        mProgram = OpenglUtil.createProgramFromShaderSrc(mVertexShader, mFragmentShader);
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        maColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    public void draw() {
        GLES20.glUseProgram(mProgram);
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false,
                getFinalMatrix(), 0);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT,
                false, 3 * 4, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glVertexAttribPointer(maColorHandle, 4, GLES20.GL_FLOAT, false,
                4 * 4, mColorBuffer);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, vertices.length/3);
        GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glUseProgram(0);
    }
}
