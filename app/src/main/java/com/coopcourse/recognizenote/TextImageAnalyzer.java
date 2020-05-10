package com.coopcourse.recognizenote;


import android.content.Context;
import android.net.Uri;
import android.util.Log;


import androidx.room.Room;

import com.coopcourse.recognizenote.database.ItemDataBase;
import com.coopcourse.recognizenote.database.RecViewItemTable;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class TextImageAnalyzer {//implements ImageAnalysis.Analyzer {

    public static Task<FirebaseVisionText> fromFile(Context context, File file) {
        ItemDataBase DB = Room.databaseBuilder(
                context, ItemDataBase.class, "items_db")
                .allowMainThreadQueries()// QUICK FIX [ASYNC TASK NEEDED]
                .build();

        FirebaseVisionImage image;
        Task<FirebaseVisionText> result = null;
        try {
            image = FirebaseVisionImage.fromFilePath(context, Uri.fromFile(file));
        } catch (
                IOException ex) {
            Log.e("Image exception ", ex.getMessage());
            return null;
        }
        FirebaseVisionCloudTextRecognizerOptions options = new FirebaseVisionCloudTextRecognizerOptions.Builder()
                .setLanguageHints(Arrays.asList("en", "ru"))
                .setModelType(FirebaseVisionCloudDetectorOptions.LATEST_MODEL)
                .build();

        FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance().getCloudTextRecognizer(options);
        result = recognizer.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        DB.itemDao().insertItem(new RecViewItemTable(firebaseVisionText.getText()));
                        Log.e("Hhh", firebaseVisionText.getText());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("Exception in recognizer", e.getMessage());
                    }
                });
        return result;
    }
}
