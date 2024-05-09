package com.hiscene.hiarslamdemo.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jucf on 2017/9/26.
 */

public class LogFileUtil {

    private static boolean isDebugLog = true;

    /**
     * 写文件到sdcard
     *
     * @param fileName
     * @param message
     */
    public static void writeLineMsg2File(String fileName, String message) {
        if(!isDebugLog) return;
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(fileName, true);
            writer.write("[" + getYMDHMS(new Date()) + "] : "
                    + message + "\n");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
/**
     * 写文件到sdcard
     *
     * @param fileName
     * @param message
     */
    public static void saveTmpDataFile(String fileName, String message) {
        if(!isDebugLog) return;
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(fileName, true);
            writer.write( message + "\n");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param date
     * @return yyyy-MM-dd HH:mm:ss
     *
     */
    // TODO: 2018/7/13 未知异常 压测 db shell monkey -p com.hiscene.hiardemo -s 300 --throttle 300
    // --monitor-native-crashes -v -v -v 10000
    public static String getYMDHMS(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }

    /**
     * 写异常信息到sdcard
     * @param e
     * @param path
     */
    public static void writeErrMsg2File(Exception e, String path) {
        if(!isDebugLog) return;
        writeLineMsg2File(path, "Err:");

        PrintStream err = null;
        try {
            err = new PrintStream(new FileOutputStream(path, true));
            e.printStackTrace(err);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
    }
}
