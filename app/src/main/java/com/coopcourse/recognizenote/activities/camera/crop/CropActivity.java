package com.coopcourse.recognizenote.activities.camera.crop;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.coopcourse.recognizenote.R;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CropActivity extends AppCompatActivity {


    public static Uri mSaveUri = null;
    private Bitmap.CompressFormat mOutputFormat = Bitmap.CompressFormat.JPEG;
    private ContentResolver mContentResolver;
    private int mAspectX, mAspectY, mOutputX, mOutputY;
    private boolean mScale;
    private float mScaleImg;
    private CropImageView mImageView;
    private Bitmap mBitmap;
    private View mProgressPanel;
    private Matrix mImageMatrix;
    private String mImagePath;
    public static final String BITMAP_EXTRA = "bitmap";
    public static final String IMAGE_PATH = "image-path";
    public static final String SCALE = "scale";
    public static final String ACTION_INLINE_DATA = "inline-data";
    private int mImageMaxHeight;
    private int mImageMaxWidth;
    private CropFigureView mCrop;
    private boolean mCircleCrop = false;
    private boolean mScaleUp = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContentResolver = getContentResolver();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_crop);
        mImageView = findViewById(R.id.image_crop);
        mProgressPanel = findViewById(R.id.progress_panel);
        mProgressPanel.setVisibility(View.INVISIBLE);
        mScaleImg = 1;
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        mImageMaxHeight = displaymetrics.heightPixels;
        mImageMaxWidth = displaymetrics.widthPixels;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {

            mImagePath = extras.getString(IMAGE_PATH);
            mSaveUri = getImageUri(mImagePath);
            mBitmap = getBitmap(mImagePath);
            mScale = extras.getBoolean(SCALE, true);
        }

        if (mBitmap == null) {
            finish();
            return;
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        findViewById(R.id.cancel).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                });

        findViewById(R.id.save).setOnClickListener(
                view ->
                {
                    mProgressPanel.setVisibility(View.VISIBLE);
                    try {
                        onSaveClicked();
                    } catch (Exception e) {
                        finish();
                    }
                /*new View.OnClickListener() {
            public void onClick(View v) {
                //  view ->mProgressPanel.setVisibility(View.VISIBLE);
                try {
                    onSaveClicked();
                } catch (Exception e) {
                    finish();
                }
            }*/
                });

        checkRotation();
        mImageView.setImageBitmapResetBase(mBitmap, true);
        makeDefault();
    }


    private Uri getImageUri(String path) {
        return Uri.fromFile(new File(path));
    }

    private Bitmap getBitmap(String path) {
        Uri uri = getImageUri(path);
        InputStream in = null;
        try {
            in = mContentResolver.openInputStream(uri);
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();

            int scale = 1;
            if (o.outHeight > mImageMaxHeight || o.outWidth > mImageMaxWidth) {
                scale = Math.round(Math.max((mImageMaxHeight / o.outHeight), (mImageMaxWidth / o.outWidth)));
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            in = mContentResolver.openInputStream(uri);
            Bitmap b = BitmapFactory.decodeStream(in, null, o2);
            in.close();

            return b;
        } catch (Exception ex) {
            Log.e("Ex occured", ex.getMessage());
        }
        return null;
    }


    private void onSaveClicked() throws Exception {
        if (mCrop == null) {
            return;
        }
        Rect r = mCrop.getCropRect();

        int width = r.width();
        int height = r.height();

        Bitmap croppedImage;
        try {
            croppedImage = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        } catch (Exception e) {
            throw e;
        }
        {
            Canvas canvas = new Canvas(croppedImage);
            Rect dstRect = new Rect(0, 0, width, height);
            canvas.drawBitmap(mBitmap, r, dstRect, null);
        }
        saveOutput(croppedImage); //сохраняем картинку по адресу
        Bundle extras = new Bundle();
         /* TextImageAnalyzer.fromFile(this, file);
       /* Intent intent = new Intent(this, PreviewActivity.class);
        intent.putExtras(extras);
        intent.putExtra(IMAGE_PATH, mImagePath);
        startActivity(intent);*/
        finish();
        //тут надо будет положить картиночку дальше

        //  startActivity(intent);
        //       finish();
    }

    private void closeStream(Closeable stream) {
        try {
            stream.close();
        } catch (IOException ex) {
            Log.e("Stream ex", ex.getMessage());
        }
    }

    private void saveOutput(Bitmap croppedImage) {
        if (mSaveUri != null) {
            OutputStream outputStream = null;
            try {
                outputStream = mContentResolver.openOutputStream(mSaveUri);
                if (outputStream != null) {
                    croppedImage.compress(mOutputFormat, 80, outputStream);
                }
            } catch (IOException ex) {
                Log.e("Ex opening", "Cannot open file: " + mSaveUri, ex);

                setResult(RESULT_CANCELED);
                finish();
                return;
            } finally {
                closeStream(outputStream);
            }
        } else {
            Log.e("Cant save", "Sorry");
        }
        croppedImage.recycle();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBitmap != null) {
            mBitmap.recycle();
        }
    }

    private void checkRotation() { // смотрим, нужен ли поворот и поворачиваем
        try {
            ExifInterface exif = new ExifInterface(mImagePath);
            String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;

            int rotationAngle = 0;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

            Matrix matrix = new Matrix();
            matrix.postRotate(rotationAngle);
            mBitmap = Bitmap.createBitmap(mBitmap, mOutputX, mOutputY, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
        } catch (Exception e) {
            Log.e("Exception rotation", "can not rotate file");
        }
    }

    private void makeDefault() {
        CropFigureView hv = new CropFigureView(mImageView);
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();

        Rect imageRect = new Rect(0, 0, width, height);
        int cropWidth = Math.min(width, height) * 4 / 5;
        int cropHeight = cropWidth;

        if (mAspectX != 0 && mAspectY != 0) {

            if (mAspectX > mAspectY) {
                cropHeight = cropWidth * mAspectY / mAspectX;
            } else {

                cropWidth = cropHeight * mAspectX / mAspectY;
            }
        }

        int x = (width - cropWidth) / 2;
        int y = (height - cropHeight) / 2;
        RectF cropRect = new RectF(x, y, x + cropWidth, y + cropHeight);
        hv.setup(mImageMatrix, imageRect, cropRect, mAspectX != 0 && mAspectY != 0);

        mImageView.mCropFigureView = hv;
        mCrop = hv;
    }

    private Bitmap prepareBitmap() {
        if (mBitmap == null) {
            return null;
        }

        if (mBitmap.getWidth() > 256) {
            mScaleImg = 256.0F / mBitmap.getWidth();
        }

        Matrix matrix = new Matrix();
        matrix.setScale(mScaleImg, mScaleImg);
        return Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
    }


    private Bitmap rotateImage(Bitmap src, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }


}
