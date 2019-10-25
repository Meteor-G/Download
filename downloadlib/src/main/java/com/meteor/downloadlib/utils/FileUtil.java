package com.meteor.downloadlib.utils;

import android.content.Context;

import com.meteor.downloadlib.DownloaderAppliaction;

import java.io.File;
import java.io.FileInputStream;

/**
 * @Author Gongll
 * @Date 2019/4/29 17:57
 * @Description
 */
public class FileUtil {
    private static String storagePath = "";
    private static final File parentPath = DownloaderAppliaction.getContent().getExternalFilesDir(null);
    private static String DST_FOLDER_NAME = "JDownload";

    public static String getDownloadDir(Context context) {
        if (storagePath.equals("")) {
            storagePath = parentPath.getAbsolutePath() + File.separator + DST_FOLDER_NAME;
            File f = new File(storagePath);
            if (!f.exists()) {
                f.mkdir();
            }
        }
        return storagePath;
    }

    public static boolean isExistsFile(String filepath) {
        try {
            File f = new File(filepath);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static int getFileSize(String filepath) {
        File file = new File(filepath);
        int size = 0;
        try {
            if (file.exists()) {
                FileInputStream fis = null;
                fis = new FileInputStream(file);
                size = fis.available();
            } else {
                file.createNewFile();
            }
        } catch (Exception e) {
            return 0;
        }
        return size;
    }
}
