package com.coopcourse.recognizenote.activities.camera;

import com.coopcourse.recognizenote.R;
import com.coopcourse.recognizenote.database.ItemDataBase;
import com.google.firebase.FirebaseApp;

import android.content.Intent;

import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import java.io.File;
import java.io.IOException;

public class CameraActivity extends AppCompatActivity {
    private CameraManager mCamera;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

    }

    @Override
    protected void onStart() {
        super.onStart();

        TextureView textureView = findViewById(R.id.texture_view);
        mCamera = new CameraManager(this, textureView);
        mButton = findViewById(R.id.camera_button);
        mButton.setOnClickListener(view -> {
            try {
                mCamera.startThisCamera();
                mCamera.captureImage(FileManager.get().createImageFile());
            } catch (IOException ex) {
                Toast.makeText(this, "Can't save", Toast.LENGTH_SHORT);
            }
        });
        mCamera.start();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("CameraActivity","onBackPressed");
        finish();
    }
}