package com.hiscene.hiarslamdemo.widget;

import android.opengl.GLES20;

import com.hiscene.hiarslamdemo.Utils.OpenglUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

//颜色立方体
public class Cube extends Widget {
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

	public Cube() {
		// 初始化顶点坐标与着色数据
		initVertexData();
		// 初始化shader
		initShader();

	}

	private float[] flatten(float[][] a)
	{
		int size = 0;
		for (int k = 0; k < a.length; k += 1) {
			size += a[k].length;
		}
		float[] l = new float[size];
		int offset = 0;
		for (int k = 0; k < a.length; k += 1) {
			System.arraycopy(a[k], 0, l, offset, a[k].length);
			offset += a[k].length;
		}
		return l;
	}

	// 初始化顶点坐标与着色数据的方法
	public void initVertexData() {
		// 顶点坐标数据的初始化================begin============================
		vCount = 12 * 6;

		float vertices[] = new float[]{
				//bottom
				0,0,0,
				UNIT_SIZE,-UNIT_SIZE,0,
				-UNIT_SIZE,-UNIT_SIZE,0,
				0,0,0,
				UNIT_SIZE,UNIT_SIZE,0,
				UNIT_SIZE,-UNIT_SIZE,0,
				0,0,0,
				-UNIT_SIZE,UNIT_SIZE,0,
				UNIT_SIZE,UNIT_SIZE,0,
				0,0,0,
				-UNIT_SIZE,-UNIT_SIZE,0,
				-UNIT_SIZE,UNIT_SIZE,0,
				//top
				0,0,UNIT_SIZE*2,
				-UNIT_SIZE,-UNIT_SIZE,UNIT_SIZE*2,
				UNIT_SIZE,-UNIT_SIZE,UNIT_SIZE*2,
				0,0,UNIT_SIZE*2,
				UNIT_SIZE,-UNIT_SIZE,UNIT_SIZE*2,
				UNIT_SIZE,UNIT_SIZE,UNIT_SIZE*2,
				0,0,UNIT_SIZE*2,
				UNIT_SIZE,UNIT_SIZE,UNIT_SIZE*2,
				-UNIT_SIZE,UNIT_SIZE,UNIT_SIZE*2,
				0,0,UNIT_SIZE*2,
				-UNIT_SIZE,UNIT_SIZE,UNIT_SIZE*2,
				-UNIT_SIZE,-UNIT_SIZE,UNIT_SIZE*2,
				//front
				0,-UNIT_SIZE,UNIT_SIZE,
				-UNIT_SIZE,-UNIT_SIZE,0,
				UNIT_SIZE,-UNIT_SIZE,0,

				0,-UNIT_SIZE,UNIT_SIZE,
				UNIT_SIZE,-UNIT_SIZE,0,
				UNIT_SIZE,-UNIT_SIZE,UNIT_SIZE*2,

				0,-UNIT_SIZE,UNIT_SIZE,
				UNIT_SIZE,-UNIT_SIZE,UNIT_SIZE*2,
				-UNIT_SIZE,-UNIT_SIZE,UNIT_SIZE*2,

				0,-UNIT_SIZE,UNIT_SIZE,
				-UNIT_SIZE,-UNIT_SIZE,UNIT_SIZE*2,
				-UNIT_SIZE,-UNIT_SIZE,0,
				//back
				0,UNIT_SIZE,UNIT_SIZE,
				UNIT_SIZE,UNIT_SIZE,0,
				-UNIT_SIZE,UNIT_SIZE,0,

				0,UNIT_SIZE,UNIT_SIZE,
				UNIT_SIZE,UNIT_SIZE,UNIT_SIZE*2,
				UNIT_SIZE,UNIT_SIZE,0,

				0,UNIT_SIZE,UNIT_SIZE,
				-UNIT_SIZE,UNIT_SIZE,UNIT_SIZE*2,
				UNIT_SIZE,UNIT_SIZE,UNIT_SIZE*2,

				0,UNIT_SIZE,UNIT_SIZE,
				-UNIT_SIZE,UNIT_SIZE,0,
				-UNIT_SIZE,UNIT_SIZE,UNIT_SIZE*2,
				//left
				-UNIT_SIZE,0,UNIT_SIZE,
				-UNIT_SIZE,UNIT_SIZE,0,
				-UNIT_SIZE,-UNIT_SIZE,0,

				-UNIT_SIZE,0,UNIT_SIZE,
				-UNIT_SIZE,-UNIT_SIZE,0,
				-UNIT_SIZE,-UNIT_SIZE,UNIT_SIZE*2,

				-UNIT_SIZE,0,UNIT_SIZE,
				-UNIT_SIZE,-UNIT_SIZE,UNIT_SIZE*2,
				-UNIT_SIZE,UNIT_SIZE,UNIT_SIZE*2,

				-UNIT_SIZE,0,UNIT_SIZE,
				-UNIT_SIZE,UNIT_SIZE,UNIT_SIZE*2,
				-UNIT_SIZE,UNIT_SIZE,0,
				//right
				UNIT_SIZE,0,UNIT_SIZE,
				UNIT_SIZE,-UNIT_SIZE,0,
				UNIT_SIZE,UNIT_SIZE,0,

				UNIT_SIZE,0,UNIT_SIZE,
				UNIT_SIZE,-UNIT_SIZE,UNIT_SIZE*2,
				UNIT_SIZE,-UNIT_SIZE,0,

				UNIT_SIZE,0,UNIT_SIZE,
				UNIT_SIZE,UNIT_SIZE,UNIT_SIZE*2,
				UNIT_SIZE,-UNIT_SIZE,UNIT_SIZE*2,

				UNIT_SIZE,0,UNIT_SIZE,
				UNIT_SIZE,UNIT_SIZE,0,
				UNIT_SIZE,UNIT_SIZE,UNIT_SIZE*2,
		};

		// 创建顶点坐标数据缓冲
		// vertices.length*4是因为一个整数四个字节
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());// 设置字节顺序
		mVertexBuffer = vbb.asFloatBuffer();// 转换为Float型缓冲
		mVertexBuffer.put(vertices);// 向缓冲区中放入顶点坐标数据
		mVertexBuffer.position(0);// 设置缓冲区起始位置
		// 顶点坐标数据的初始化================end============================

		// 顶点颜色值数组，每个顶点4个色彩值RGBA
		float colors[] = new float[]{
				// 前面
				1, 1, 1, 0, // 中间为白色
				1, 0, 0, 0,
				1, 0, 0, 0,
				1, 1, 1, 0, // 中间为白色
				1, 0, 0, 0,
				1, 0, 0, 0,
				1, 1, 1, 0, // 中间为白色
				1, 0, 0, 0,
				1, 0, 0, 0,
				1, 1, 1, 0, // 中间为白色
				1, 0, 0, 0,
				1, 0, 0, 0,
				// 后面
				1, 1, 1, 0, // 中间为白色
				0, 0, 1, 0,
				0, 0, 1, 0,
				1, 1, 1, 0, // 中间为白色
				0, 0, 1, 0,
				0, 0, 1, 0,
				1, 1, 1, 0, // 中间为白色
				0, 0, 1, 0,
				0, 0, 1, 0,
				1, 1, 1, 0, // 中间为白色
				0, 0, 1, 0,
				0, 0, 1, 0,
				// 左面
				1, 1, 1, 0, // 中间为白色
				1, 0, 1, 0,
				1, 0, 1, 0,
				1, 1, 1, 0, // 中间为白色
				1, 0, 1, 0,
				1, 0, 1, 0,
				1, 1, 1, 0, // 中间为白色
				1, 0, 1, 0,
				1, 0, 1, 0,
				1, 1, 1, 0, // 中间为白色
				1, 0, 1, 0,
				1, 0, 1, 0,
				// 右面
				1, 1, 1, 0, // 中间为白色
				1, 1, 0, 0,
				1, 1, 0, 0,
				1, 1, 1, 0, // 中间为白色
				1, 1, 0, 0,
				1, 1, 0, 0,
				1, 1, 1, 0, // 中间为白色
				1, 1, 0, 0,
				1, 1, 0, 0,
				1, 1, 1, 0, // 中间为白色
				1, 1, 0, 0,
				1, 1, 0, 0,
				// 上面
				1, 1, 1, 0, // 中间为白色
				0, 1, 0, 0,
				0, 1, 0, 0,
				1, 1, 1, 0, // 中间为白色
				0, 1, 0, 0,
				0, 1, 0, 0,
				1, 1, 1, 0, // 中间为白色
				0, 1, 0, 0,
				0, 1, 0, 0,
				1, 1, 1, 0, // 中间为白色
				0, 1, 0, 0,
				0, 1, 0, 0,
				// 下面
				1, 1, 1, 0, // 中间为白色
				0, 1, 1, 0,
				0, 1, 1, 0,
				1, 1, 1, 0, // 中间为白色
				0, 1, 1, 0,
				0, 1, 1, 0,
				1, 1, 1, 0, // 中间为白色
				0, 1, 1, 0,
				0, 1, 1, 0,
				1, 1, 1, 0,
				0, 1, 1, 0,
				0, 1, 1, 0,};
		// 创建顶点着色数据缓冲
		ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
		cbb.order(ByteOrder.nativeOrder());// 设置字节顺序
		mColorBuffer = cbb.asFloatBuffer();// 转换为Float型缓冲
		mColorBuffer.put(colors);// 向缓冲区中放入顶点着色数据
		mColorBuffer.position(0);// 设置缓冲区起始位置
		// 特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
		// 转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
		// 顶点着色数据的初始化================end============================
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
		// 绘制立方体
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
	}
}
