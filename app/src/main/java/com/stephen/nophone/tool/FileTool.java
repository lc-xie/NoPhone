package com.stephen.nophone.tool;

import android.os.Environment;
import android.util.Log;

import com.stephen.nophone.MyApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by stephen on 18-11-5.
 * 文件存储工具类
 * 主要存储使用时间详细记录
 */

public class FileTool {
    private static final String TAG = FileTool.class.getSimpleName();

    private static File file1 = null;
    private static File file2 = null;

    public static final String APP_STORAGE_DIR = Environment.getExternalStorageDirectory() + "/NoPhone";
    private static final String DETAIL_RECORD_FILE = APP_STORAGE_DIR + "/detailRecords1.txt";
    private static final String DETAIL_RECORD_FILE_2 = APP_STORAGE_DIR + "/detailRecords2.txt";

    private static volatile FileTool tool;
    private FileTool(){
        init();
    }

    public static FileTool getFileTool() {
        if (tool == null) {
            synchronized (FileTool.class) {
                if (tool == null) {
                    tool = new FileTool();
                }
            }
        }
        return tool;
    }

    private void init() {
        File dir = new File(APP_STORAGE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        file1 = new File(DETAIL_RECORD_FILE);
        file2 = new File(DETAIL_RECORD_FILE_2);
        try {
            if (!file1.exists()) {
                file1.createNewFile();
                Log.e(TAG, "-------1");
            }
            if (!file2.exists()) {
                file2.createNewFile();
                Log.e(TAG, "-------2");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void writeRecordFile1(String s) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file1, true);
            fileWriter.append(s);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void writeRecordFile2(String s) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file2, true);
            fileWriter.append(s);
            fileWriter.append('\n');
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
