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
        File imgFile = File.createTempFile("JPEG_" + timeStamp + "_", ".jpg", mStorageImgDir);
        return imgFile;
    }

 public static void createTextFile(Context context, String text){
        File appDir = new File(context.getExternalFilesDir(null)+"/RecognizeNote/");
        if (!appDir.exists()) {
            appDir.mkdir();
        }

        String timeStamp = new SimpleDateFormat(("yyyyMMdd_HHmmss")).format(new Date());
        String filename = "Recognized_text"+timeStamp+".txt";
        File textFile = new File(appDir, filename);
        try {
            textFile.createNewFile();
            FileWriter f = new FileWriter(textFile);
            f.write(text);
            f.close();
        }
        catch (Exception ex){
            Log.e("Saving error", ex.getMessage());}

    }
}
