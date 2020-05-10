package com.coopcourse.recognizenote.activities.camera;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileManager { //синглетный класс
    private File mStorageImgDir;
    private static FileManager mFileManager;//ссылка на созданный объект менеджера

    private FileManager() {
    }//приватный конструктор

    public static FileManager get() {//вернуть ссылку на объект класса
        if (mFileManager == null) {
            mFileManager = new FileManager();
        }
        return mFileManager;
    }

    public void setStorageDir(File dir) {
        mStorageImgDir = dir;
    }

    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat(("yyyyMMdd_HHmmss")).format(new Date());
        File storageDir = mStorageImgDir; //this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imgFile = File.createTempFile("JPEG_" + timeStamp + "_", ".jpg", mStorageImgDir);
        // mCurrentPhotoPath = imgFile.getAbsolutePath();
        return imgFile;
    }


}