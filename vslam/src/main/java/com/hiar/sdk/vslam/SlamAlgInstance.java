package com.hiar.sdk.vslam;

import android.util.Log;

public class SlamAlgInstance {

    HiarSlamPose pose = new HiarSlamPose();
    AlgWrapper slam;
    ProcessListener processListener;
    boolean needProcess = false;
    int nSlamInitType;

    public ProcessListener getProcessListener() {
        return processListener;
    }

    public void setAlgo(AlgWrapper algo) {
        slam = algo;
    }
    public int GetVersion() {
        if (slam != null) {
            return slam.GetVersion();
        }
        return -1;
    }
    public int GetPreferredSystemCameraIntrinsics(int width, int height) {
        return slam.GetPreferredSystemCameraIntrinsics(width, height);
    }
    public int Get3DLineCount(int[] lineNum){
        return slam.Get3DLineCount(lineNum);
    }
    public int Get3DLines(float[] lineArray, int[] lineNum){
        return slam.Get3DLines(lineArray, lineNum);
    }
    public void setProcessListener(ProcessListener processListener) {
        this.processListener = processListener;
    }

    public float[] getMatRT() {
        return pose.extrinsics;
    }

    public float[] GetPointCloudPoints() {
        return slam.GetPointCloudPoints();
    }

    public int SaveAreaDesc(String areaDescFilePath) {
        return slam.SaveAreaDesc(areaDescFilePath);
    }
    public int ReadYamlFile(String yamlFileName, Object mePara) {
        return AlgWrapper.ReadYamlFile(yamlFileName,mePara);
    }

    public static int WriteYamlFileWithPose(String yamlFileName, Object mePara, double[] pose) {
        return AlgWrapper.WriteYamlFileWithPose(yamlFileName, mePara, pose);
    }
    public int Reset() {
        return slam.Reset();
    }
    public void setSlamInitType(int nSlamInitType){
        this.nSlamInitType = nSlamInitType;
    }

    public boolean isNeedProcess() {
        return needProcess;
    }

    public void setNeedProcess(boolean needProcess) {
        this.needProcess = needProcess;
    }

    public void SlamAlgInstance(AlgWrapper slam_) {
        slam = slam_;
        //CameraSource.Instance().setNewFrameListener(this);
    }

    private String getStatusString(int nRtn, int state) {
        String trackingState;

        if (nRtn < 0)
            trackingState = "Error in calling Process";
        else
        {
            switch (state)
            {
                case HiarSlamState.WAIT_FOR_RECOG_IMAGE:
                    trackingState = "Wait for Recog";
                    break;
                case HiarSlamState.START:
                    trackingState = "Not initialized";
                    break;
                case HiarSlamState.SUCCESSFUL:
                    trackingState = "Tracking";
                    break;
                case HiarSlamState.LOST:
                    trackingState = "Lost";
                    break;
                default:
                    trackingState = "<wrong>";
                    break;
            }
        }
        return trackingState;
    }

    public void pushVideoFrame(byte[] data, int width, int height) {
//        VideoFrame.Buffer frameBuffer = new NV21Buffer(data, width, height, null);
//        VideoFrame frameNew= new VideoFrame(frameBuffer,0,System.currentTimeMillis()*1000000);
//        ExternalVideoCapture.getInstance().PushVideoFrame(frameNew);

        if (needProcess) {
            boolean bRtn;
            int nRtn;

            Long time_begin = System.currentTimeMillis();
            //处理当前帧，获取设备姿态信息
            Log.d("SLAM","hello,"+pose.extrinsics[0]+","+pose.extrinsics[1]+","+pose.extrinsics[2]+","+pose.extrinsics[3]
                    +","+pose.extrinsics[4] +","+pose.extrinsics[5]+","+pose.extrinsics[6]+","+pose.extrinsics[7]
                    +","+pose.extrinsics[8] +","+pose.extrinsics[9]+","+pose.extrinsics[10]+","+pose.extrinsics[11]
                    +","+pose.extrinsics[12] +","+pose.extrinsics[13]+","+pose.extrinsics[14]+","+pose.extrinsics[15]);
            nRtn = slam.ProcessFrame(data, HiarSlamImageFormat.NV21, pose);
            final float processTime = System.currentTimeMillis() - time_begin;
            final int fps = (int)(1000.0f / processTime);
            final String trackingState = getStatusString(nRtn, pose.getTrackingState());

            if(nSlamInitType == HiarSlamInitType.INIT_2DRECOG){
                int[] index = new int[1];
                int[] imageWidth = new int[1];
                int[] imageHeight = new int[1];
                slam.GetInitModelInfo(index, imageWidth, imageHeight);
                processListener.setWidthAndHeight(imageWidth[0], imageHeight[0]);
            }
            bRtn = (nRtn == 0);
            if (processListener != null) {
                processListener.donProcessFps(fps);
                processListener.onProcessTime(processTime);
                //trackingState can be 'Lost' or other meanings. please refer to slam algo.
                processListener.onTrackingState(trackingState);
            }

            //将姿态信息转换为OpenGL坐标系下的ModelView Matrix
            nRtn = slam.ConvertCameraExtrinsicsToGL(pose.extrinsics, pose.viewMatrix);
            processListener.onProcess(pose.viewMatrix, pose.getTrackingState());
        }

        float[] points = slam.GetPointCloudPoints();
        if (points != null && pose.getTrackingState() == HiarSlamState.SUCCESSFUL) {
            processListener.onPointCloud(points, true);
        } else {
            processListener.onPointCloud(null, false);
        }
    }

    public void crateAlgo() {
        if (slam == null) {
            slam = new AlgWrapper();
        }
    }

    public void destroyAlgo() {
        if (slam != null) {
            slam.Destroy();
        }
    }

    private static class LazyHolder {
        private static final SlamAlgInstance INSTANCE = new SlamAlgInstance();
    }
    public static SlamAlgInstance getInstance() {
        return LazyHolder.INSTANCE;
    }

}
