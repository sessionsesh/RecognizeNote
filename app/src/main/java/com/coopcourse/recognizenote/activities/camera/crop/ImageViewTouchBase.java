package com.coopcourse.recognizenote.activities.camera.crop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

@SuppressLint("AppCompatCustomView")
abstract class ImageViewTouchBase extends ImageView {
    static final float SCALE_RATE = 1.25F;
    protected Matrix mBaseMatrix = new Matrix();
    protected Matrix mSuppMatrix = new Matrix();
    private final Matrix mDisplayMatrix = new Matrix();
    private final float[] mMatrixValues = new float[9];
    final protected RotateBitmap mBitmapDisplayed = new RotateBitmap(null);
    private Runnable mOnLayoutRunnable = null;

    int mThisWidth = -1, mThisHeight = -1;
    int mLeft, mRight, mTop, mBottom;
    float mMaxZoom;
   // static final float SCALE_RATE = 1.25F;


    public ImageViewTouchBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mLeft = left;
        mRight = right;
        mTop = top;
        mBottom = bottom;
        mThisWidth = right - left;
        mThisHeight = bottom - top;
        Runnable r = mOnLayoutRunnable;
        if (r != null) {
            mOnLayoutRunnable = null;
            r.run();
        }
        if (mBitmapDisplayed.getBitmap() != null) {
            getProperBaseMatrix(mBitmapDisplayed, mBaseMatrix);
            setImageMatrix(getImageViewMatrix());
        }
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        setImageBitmap(bitmap, 0);
    }

    private void setImageBitmap(Bitmap bitmap, int rotation) {
        super.setImageBitmap(bitmap);
        Drawable d = getDrawable();
        if (d != null) {
            d.setDither(true);
        }

        mBitmapDisplayed.setBitmap(bitmap);
        mBitmapDisplayed.setRotation(rotation);

    }

    public void clear() {
        setImageBitmapResetBase(null, true);
    }

    public void setImageBitmapResetBase(final Bitmap bitmap, boolean resetSupp) {
        setImageRotateBitmapResetBase(new RotateBitmap(bitmap), resetSupp);
    }

    public void setImageRotateBitmapResetBase(final RotateBitmap bitmap, boolean resetSupp) {
        final int viewWidth = getWidth();
        if (viewWidth <= 0) {
            mOnLayoutRunnable = new Runnable() {
                public void run() {
                    setImageRotateBitmapResetBase(bitmap, resetSupp);
                }
            };
            return;
        }
        if (bitmap.getBitmap() != null) {
            getProperBaseMatrix(bitmap, mBaseMatrix);
            setImageBitmap(bitmap.getBitmap(), bitmap.getRotation());
        } else {
            mBaseMatrix.reset();
            setImageBitmap(null);
        }

        if (resetSupp) {
            mSuppMatrix.reset();
        }
        setImageMatrix(getImageViewMatrix());
        mMaxZoom = maxZoom();
    }


    protected void center(boolean horizontal, boolean vertical) {
        if (mBitmapDisplayed.getBitmap() == null) {
            return;
        }

        Matrix m = getImageViewMatrix();
        RectF rect = new RectF(0, 0, mBitmapDisplayed.getBitmap().getWidth(), mBitmapDisplayed.getBitmap().getHeight());
        m.mapRect(rect);

        float height = rect.height();
        float width = rect.width();
        float deltaX = 0, deltaY = 0;

        if (vertical) {
            int viewHeight = getHeight();
            if (height < viewHeight) {
                deltaY = (viewHeight - height) / 2 - rect.top;
            } else if (rect.top > 0) {
                deltaY = -rect.top;
            } else if (rect.bottom < viewHeight) {
                deltaY = getHeight() - rect.bottom;
            }
        }

        if (horizontal) {
            int viewWidth = getWidth();
            if (width < viewWidth) {
                deltaX = (viewWidth - width) / 2 - rect.left;
            } else if (rect.left > 0) {
                deltaX = -rect.left;
            } else if (rect.right < viewWidth) {
                deltaX = viewWidth - rect.right;
            }
        }
        mSuppMatrix.postTranslate(deltaX, deltaY);
        setImageMatrix(getImageViewMatrix());
    }

    public ImageViewTouchBase(Context context) {
        super(context);
        init();
    }

    private void init() {
        setScaleType(ScaleType.MATRIX);
    }

    protected float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }


    protected float getScale(Matrix matrix) {
        return getValue(matrix, Matrix.MSCALE_X);
    }

    protected float getScale() {
        return getScale(mSuppMatrix);
    }

    private void getProperBaseMatrix(RotateBitmap bitmap, Matrix matrix) {
        float viewWidth = getWidth(); // получаем параметры элемента отображения
        float viewHeight = getHeight();
        float w = bitmap.getWidth(); //получаем параметры изображения, которое нужно отобразить
        float h = bitmap.getHeight();
        matrix.reset();
        float widthScale = Math.min(viewWidth / w, 2.0f); //если сделать уменьшение больше, чем в 2 раза, будет слишком мелко
        float heightScale = Math.min(viewHeight / h, 2.0f);
        float scale = Math.min(widthScale, heightScale);

        matrix.postConcat(bitmap.getRotateMatrix());
        matrix.postScale(scale, scale);
        matrix.postTranslate((viewWidth - w * scale) / 2F, (viewHeight - h * scale) / 2F);
    }


    protected Matrix getImageViewMatrix() { //объединяем основную и вспомогательные матрицы для отображения
        mDisplayMatrix.set(mBaseMatrix);
        mDisplayMatrix.postConcat(mSuppMatrix);
        return mDisplayMatrix;
    }


    protected float maxZoom() {
        if (mBitmapDisplayed.getBitmap() == null) {
            return 1F;
        }
        float w = (float) mBitmapDisplayed.getWidth() / (float) mThisWidth;
        float h = (float) mBitmapDisplayed.getHeight() / (float) mThisHeight;
        return Math.max(w, h) * 4; //максимально можно увеличить в 4 раза
    }

    protected void zoomTo(float scale, float centerX, float centerY) { //увеличение области картинки
        if (scale > mMaxZoom) {
            scale = mMaxZoom;
        }
        float oldScale = getScale();
        float deltaScale = scale / oldScale;
        mSuppMatrix.postScale(deltaScale, deltaScale, centerX, centerY);
        setImageMatrix(getImageViewMatrix());
        center(true, true); //центрируем получившееся
    }


    protected void zoomIn() {
        zoomIn(SCALE_RATE);
    }

    protected void zoomIn(float rate) {
        if (getScale() >= mMaxZoom) {
            return;
        }
        if (mBitmapDisplayed.getBitmap() == null) {
            return;
        }

        float cx = getWidth() / 2F;
        float cy = getHeight() / 2F;

        mSuppMatrix.postScale(rate, rate, cx, cy);
        setImageMatrix(getImageViewMatrix());
    }

}