package com.coopcourse.recognizenote.activities.camera;

import android.content.Context;
import android.graphics.Matrix;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.lifecycle.LifecycleOwner;


import com.coopcourse.recognizenote.TextImageAnalyzer;
import java.io.File;
import java.util.concurrent.Executors;

public class CameraManager  {
    private TextImageAnalyzer mImageAnalyzer;
    private ImageCapture mImageCapture;
    private Preview mPreview;
    private boolean mIsCompleted;
    private Context mContext;
    private TextureView mCameraView;
    private AppCompatActivity mActivity;

    CameraManager(AppCompatActivity activity, TextureView cameraView) {

        mContext=activity;
        mActivity=activity;
        mCameraView = cameraView;
    }

    public void start()
    {mCameraView.post(this::startThisCamera);}

    public void clear() {
        CameraX.unbindAll();
    }

    private void updateTransform(Size textureSize) {
        //настройка вида камеры
        Matrix matrix = new Matrix();

        // Compute the center of the view finder
        float centerX = mCameraView.getWidth() / 2f;
        float centerY = mCameraView.getHeight() / 2f;

        int rotation;
        switch (mCameraView.getDisplay().getRotation()) {
            case Surface.ROTATION_0:
                rotation = 0;
                break;
            case Surface.ROTATION_90:
                rotation = 90;
                break;
            case Surface.ROTATION_180:
                rotation = 180;
                break;
            case Surface.ROTATION_270:
                rotation = 270;
                break;
            default:
                return;
        }

        int previewHeight = Math.max(textureSize.getWidth(), textureSize.getHeight());
        int previewWidth = Math.min(textureSize.getWidth(), textureSize.getHeight());

        int outputWidth, outputHeight;
        if (previewWidth * mCameraView.getHeight() > previewHeight * mCameraView.getWidth()) {
            outputWidth = mCameraView.getWidth() * previewHeight / mCameraView.getHeight();
            outputHeight = previewHeight;
        } else {
            outputWidth = previewWidth;
            outputHeight = mCameraView.getHeight() * previewWidth / mCameraView.getWidth();
        }
        matrix.setScale(previewWidth * 1F / outputWidth, previewHeight * 1F / outputHeight, centerX, centerY);
        matrix.postRotate(-rotation, centerX, centerY);

        // Finally, apply transformations to our TextureView
        mCameraView.setTransform(matrix);
    }

    private void createImageCapture() {
        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder()
                .setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
                .setLensFacing(CameraX.LensFacing.BACK)
                .build();
        mImageCapture = new ImageCapture(imageCaptureConfig);
    }


    private void createImagePreview(){
        PreviewConfig previewConfig = new PreviewConfig.Builder()
                .setLensFacing(CameraX.LensFacing.BACK)
                .build();
        mPreview = new Preview(previewConfig);
        mPreview.setOnPreviewOutputUpdateListener(output -> { //на обновлении превью
            ViewGroup parent = (ViewGroup) mCameraView.getParent();
            parent.removeView(mCameraView); // удаляем старый превью
            parent.addView(mCameraView, 0); //добавляем превью заново
            mCameraView.setSurfaceTexture(output.getSurfaceTexture());
            updateTransform(output.getTextureSize());
        });
    }


    void startThisCamera(){
        CameraX.unbindAll();
        if (!(mActivity.isFinishing()) && !(mActivity.isDestroyed()))
        {
            createImagePreview();
            createImageCapture();
            CameraX.bindToLifecycle((LifecycleOwner)mActivity, mPreview, mImageCapture);
        }
    }


    void captureImage(File file){
        if (mImageCapture != null) {
            mImageCapture.takePicture(file, Executors.newSingleThreadExecutor(),
                    new ImageCapture.OnImageSavedListener() {
                        @Override
                        public void onImageSaved(@NonNull File file) {
                            Log.e("File saved", file.toString());
                            TextImageAnalyzer.fromFile((Context)mActivity, file);
                            //TODO(add crop)
//                            Intent intent = new Intent(mContext, CropActivity.class);
//                            intent.putExtra(CropActivity.IMAGE_PATH, file.getPath());
//                            intent.putExtra(CropActivity.SCALE, true);
//                            mActivity.startActivity(intent);
                        }
                        @Override
                        public void onError(@NonNull ImageCapture.ImageCaptureError imageCaptureError, @NonNull String message, @Nullable Throwable cause) {
                            Toast.makeText(
                                    mActivity,
                                    message,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
            );
        }




    }
}
