package com.hiscene.hiarslamdemo.widget;

import android.opengl.GLES20;

import com.hiscene.hiarslamdemo.Utils.OpenglUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * 框
 * Created by YI on 2017/9/14.
 */

public class Frame extends Widget {
    private int mProgram;// 自定义渲染管线着色器程序id
    private int muMVPMatrixHandle;// 总变换矩阵引用
    private int maPositionHandle; // 顶点位置属性引用
    private int maColorHandle; // 顶点颜色属性引用
    private String mVertexShader = "uniform mat4 uMVPMatrix; //\n" +
            "attribute vec3 aPosition;  //\n" +
            "attribute vec4 aColor;    //\n" +
            "varying  vec4 vColor;  //\n" +
            "void main()  {\n" +
            "   gl_Position = uMVPMatrix * vec4(aPosition,1); //\n" +
            "   vColor = aColor;//\n" +
            "}";// 顶点着色器代码脚本
    private String mFragmentShader = "precision mediump float;\n" +
            "varying  vec4 vColor; //\n" +
            "varying vec3 vPosition;//\n" +
            "void main() {  \n" +
            "   gl_FragColor = vColor;//\n" +
            "}";// 片元着色器代码脚本

    private FloatBuffer mVertexBuffer;// 顶点坐标数据缓冲
    private FloatBuffer mColorBuffer;// 顶点着色数据缓冲
    private int vCount = 0;
    public static final float UNIT_SIZE = 0.5f;
    float colorR = 0f,colorG = 0f,colorB = 1f,colorA =1f;
    public Frame() {
        // 初始化顶点坐标与着色数据
        initVertexData();
        // 初始化shader
        initShader();

    }

    private void initVertexData() {
        vCount = 4;
        final float UNIT_SIZE = 0.5f;
        float vertices[] = new float[]
                {
                        -UNIT_SIZE, UNIT_SIZE, 0,
                        -UNIT_SIZE, -UNIT_SIZE, 0,
                        UNIT_SIZE, -UNIT_SIZE, 0,
                        UNIT_SIZE, UNIT_SIZE, 0,
                };
        // 创建顶点坐标数据缓冲
        // vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());// 设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();// 转换为Float型缓冲
        mVertexBuffer.put(vertices);// 向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);// 设置缓冲区起始位置

        float colors[] = new float[]{
                colorR,colorG,colorB,colorA,
                colorR,colorG,colorB,colorA,
                colorR,colorG,colorB,colorA,
                colorR,colorG,colorB,colorA
        };
        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length*4);
        cbb.order(ByteOrder.nativeOrder());
        mColorBuffer = cbb.asFloatBuffer();
        mColorBuffer.put(colors);
        mColorBuffer.position(0);
    }

    // 初始化shader
    public void initShader() {
        // 基于顶点着色器与片元着色器创建程序
        mProgram = OpenglUtil.createProgramFromShaderSrc(mVertexShader, mFragmentShader);
        // 获取程序中顶点位置属性引用id
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        // 获取程序中顶点颜色属性引用id
        maColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        // 获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }
    @Override
    public void draw() {
        GLES20.glUseProgram(mProgram);
        // 将最终变换矩阵传入shader程序
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, getFinalMatrix(), 0);

        // 为画笔指定顶点位置数据
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, mVertexBuffer);
        // 为画笔指定顶点着色数据
        GLES20.glVertexAttribPointer(maColorHandle, 4, GLES20.GL_FLOAT, false, 4 * 4, mColorBuffer);
        // 允许顶点位置数据数组
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(maColorHandle);
        GLES20.glLineWidth(3);
        // 绘制边框
        GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, vCount);
        GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glUseProgram(0);
    }
}
