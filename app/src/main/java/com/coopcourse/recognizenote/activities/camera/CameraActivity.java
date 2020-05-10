package com.coopcourse.recognizenote.activities.camera;

import com.coopcourse.recognizenote.R;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
        mButton.setVisibility(View.VISIBLE);
        mButton.setOnClickListener(view -> {
            try {
                mCamera.startThisCamera();
                mCamera.captureImage(FileManager.get().createImageFile());
            }
            catch (IOException ex){
                Toast.makeText(this, "Can't save", Toast.LENGTH_SHORT).show();
            }
        });
        mCamera.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}