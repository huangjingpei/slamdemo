package com.hiscene.hiarslamdemo.Utils;

import android.content.Context;

import java.io.File;

/**
 * Created by liwenfei on 31/08/2017.
 */

public class DiskCacheManager {
    private double CAPACITY = 200;//200M
    private static DiskCacheManager _instance = new DiskCacheManager();
    public static DiskCacheManager Instance(){
        return _instance;
    }
    DiskCacheManager(){}

    public double getCacheSize(){
        double modelSize = FileSizeUtil.getFileOrFilesSize(FilePath.Instance().getModelPath(),FileSizeUtil.SIZETYPE_MB);
//        double musicSize = FileSizeUtil.getFileOrFilesSize(FilePath.Instance().getMusicPath(),FileSizeUtil.SIZETYPE_MB);
//        double videoSize = FileSizeUtil.getFileOrFilesSize(FilePath.Instance().getVideoPath(),FileSizeUtil.SIZETYPE_MB);
        double mediaSize = FileSizeUtil.getFileOrFilesSize(FilePath.Instance().getMediaPath(),FileSizeUtil.SIZETYPE_MB);
        double imageSize = FileSizeUtil.getFileOrFilesSize(FilePath.Instance().getImgPath(),FileSizeUtil.SIZETYPE_MB);
        double keySize = FileSizeUtil.getFileOrFilesSize(FilePath.Instance().getKeyPath(),FileSizeUtil.SIZETYPE_MB);
        double jsonSize = FileSizeUtil.getFileOrFilesSize(FilePath.Instance().getJsonPath(),FileSizeUtil.SIZETYPE_MB);
        return modelSize+keySize+jsonSize+mediaSize+imageSize;
    }

    public void clearCache(Context context){
        FilePath.Instance().initRootPath(context.getExternalCacheDir().getPath());
        FileUtil.deleteFiles(FilePath.Instance().getModelPath());
//        FileUtil.deleteFiles(FilePath.Instance().getVideoPath());
//        FileUtil.deleteFiles(FilePath.Instance().getMusicPath());
        FileUtil.deleteFiles(FilePath.Instance().getMediaPath());
        FileUtil.deleteFiles(FilePath.Instance().getImgPath());
        FileUtil.deleteFiles(FilePath.Instance().getKeyPath());
        FileUtil.deleteFiles(FilePath.Instance().getJsonPath());
        SharedPreferencesUtil.Instance().init(context);
        SharedPreferencesUtil.Instance().clear();
    }

    public void autoClearChche(){
        while (getCacheSize() > CAPACITY){
            LogUtil.Logi("cache size: "+getCacheSize());
            File jsonDir = new File(FilePath.Instance().getJsonPath());
            File[] jsonFiles = jsonDir.listFiles();
            sortByTime(jsonFiles);
            if(jsonFiles.length > 0){
                for(int i = 0;i<(jsonFiles.length+1)/2;i++){
                    String fileName = jsonFiles[i].getName();
                    String targetId = fileName.substring(0,fileName.indexOf(".json"));
                    SharedPreferencesUtil.Instance().remove(targetId);
                    File keyFile = new File(FilePath.Instance().getKeyPath()+targetId+".db");
                    if(keyFile.exists()){
                        keyFile.delete();
                    }
                    jsonFiles[i].delete();
                }
            }

            File modelDir = new File(FilePath.Instance().getModelPath());
            clearDir(modelDir);
//            File videoDir = new File(FilePath.Instance().getVideoPath());
//            clearDir(videoDir);
//            File musicDir = new File(FilePath.Instance().getMusicPath());
//            clearDir(musicDir);
            File mediaDir = new File(FilePath.Instance().getMediaPath());
            clearDir(mediaDir);
            File imageDir = new File(FilePath.Instance().getImgPath());
            clearDir(imageDir);
        }
    }

    private void clearDir(File dir){
        File[] files = dir.listFiles();
        sortByTime(files);
        if(files.length > 0){
            for(int i = 0;i<(dir.length()+1)/2;i++){
                files[i].delete();
            }
        }
    }

    private void sortByTime(File[] files){
        for(int i =0,j=files.length;i<j;i++){
            File temp = files[i];
            int left = 0;
            int right = i-1;
            int mid = 0;
            while(left<=right){
                mid = (left+right)/2;
                if(temp.lastModified() < files[mid].lastModified()){
                    right = mid -1;
                }else{
                    left = mid +1;
                }
            }
            for(int k = i-1;k>=left;k--){
                files[k+1] = files[k];
            }
            if(left != i){
                files[left] = temp;
            }
        }
    }
}

