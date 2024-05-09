package com.hiscene.hiarslamdemo.widget;

import android.opengl.GLES20;

import com.hiscene.hiarslamdemo.Utils.OpenglUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by liwenfei on 2017/5/8.
 */

public class Lines extends Widget {

    private int mProgram;
    private int muMVPMatrixHandle;
    private int maPositionHandle;
    private int maColorHandle;
    private String mVertexShader = "uniform mat4 uMVPMatrix; "
            + "attribute vec3 aPosition;  " + "attribute vec4 aColor; "
            + "varying  vec4 vColor;  " + "void main()  {"
            + "   gl_Position = uMVPMatrix * vec4(aPosition,1); "
            + "   vColor = aColor;" + "}";
    private String mFragmentShader = "precision mediump float;"
            + "varying vec3 vPosition;" + "varying vec4 vColor;"
            + "void main() {  " + "   gl_FragColor = vColor;" + "}";

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mColorBuffer;
    private int vCount = 0;
    float colorR = 0f, colorG = 0f, colorB = 1f, colorA = 1f;
    public static final float UNIT_SIZE = 1f;

    public Lines() {
        initVertexData();
        initShader();
    }

    public void setColor(float r, float g, float b, float a) {
        colorA = a;
        colorB = b;
        colorG = g;
        colorR = r;
    }

    public void initVertexData() {
        // ============================begin============================
        vCount = 6;
        float vertices[] = new float[] {0,0,0,UNIT_SIZE,0,0,
        0,0,0,0,UNIT_SIZE,0,
        0,0,0,0,0,UNIT_SIZE};

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);
        // ============================end============================

        float colors[] = new float[] {
                1.0f, 0.0f, 0.0f, 1f,
                1.0f, 0.0f, 0.0f, 1f,
                0.0f, 1.0f, 0.0f, 1f,
                1.0f, 1.0f, 0.0f, 1f,
                0.0f, 0.0f, 1.0f, 1f,
                0.0f, 0.0f, 1.0f, 1f,
        };
        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        mColorBuffer = cbb.asFloatBuffer();
        mColorBuffer.put(colors);
        mColorBuffer.position(0);
    }

    public void initShader() {
        mProgram = OpenglUtil.createProgramFromShaderSrc(mVertexShader,
                mFragmentShader);
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
        GLES20.glEnableVertexAttribArray(maColorHandle);
        GLES20.glLineWidth(3);
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, vCount);
        GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glUseProgram(0);
    }
}
