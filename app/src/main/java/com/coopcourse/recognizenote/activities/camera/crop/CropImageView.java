package com.coopcourse.recognizenote.activities.camera.crop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;



public class CropImageView extends ImageViewTouchBase {
    CropFigureView mCropFigureView;
    CropFigureView mMotionCropFigureView = null;
    float mLastX, mLastY;
    int mMotionEdge;
    private Context mContext;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mBitmapDisplayed.getBitmap() != null) {
                mCropFigureView.mMatrix.set(getImageMatrix());
                mCropFigureView.invalidate();
                if (mCropFigureView.mIsFocused) {
                    centerBasedOnHighlightView(mCropFigureView);
                }
            }
        }

    public CropImageView(Context context) {
        super(context);
        this.mContext = context;}

    public CropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    @Override
    protected void zoomTo(float scale, float centerX, float centerY) {
        super.zoomTo(scale, centerX, centerY);
            mCropFigureView.mMatrix.set(getImageMatrix());
            mCropFigureView.invalidate();
    }

    @Override
    protected void zoomIn() {
        super.zoomIn();
        mCropFigureView.mMatrix.set(getImageMatrix());
        mCropFigureView.invalidate();
    }

    protected void postTranslate(float deltaX, float deltaY) {
        mSuppMatrix.postTranslate(deltaX, deltaY);
        mCropFigureView.mMatrix.postTranslate(deltaX, deltaY);
        mCropFigureView.invalidate();
        }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        CropActivity cropActivity = (CropActivity) mContext;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                        CropFigureView hv = mCropFigureView;
                        int edge = hv.getHit(event.getX(), event.getY());
                        if (edge != CropFigureView.GROW_NONE) {
                            mMotionEdge = edge;
                            mMotionCropFigureView = hv;
                            mLastX = event.getX();
                            mLastY = event.getY();
                            mMotionCropFigureView.setMode((edge == CropFigureView.MOVE) ? CropFigureView.ModifyMode.Move : CropFigureView.ModifyMode.Grow);
                            break;
                    }
                break;
            case MotionEvent.ACTION_UP:
             if (mMotionCropFigureView != null) {
                    centerBasedOnHighlightView(mMotionCropFigureView);
                    mMotionCropFigureView.setMode(CropFigureView.ModifyMode.None);
                }
                mMotionCropFigureView = null;
                break;
            case MotionEvent.ACTION_MOVE:
                 if (mMotionCropFigureView != null) {
                    mMotionCropFigureView.handleMotion(mMotionEdge, event.getX() - mLastX, event.getY() - mLastY);
                    mLastX = event.getX();
                    mLastY = event.getY();
                    ensureVisible(mMotionCropFigureView);
                }
                break;
        }
        center(true, true);
        return true;
    }


    private void ensureVisible(CropFigureView hv) { //проверка, виден ли квадрат обрезки
        Rect r = hv.mDrawRect;

        int panDeltaX1 = Math.max(0, mLeft - r.left);
        int panDeltaX2 = Math.min(0, mRight - r.right);

        int panDeltaY1 = Math.max(0, mTop - r.top);
        int panDeltaY2 = Math.min(0, mBottom - r.bottom);

        int deltaX = panDeltaX1 != 0 ? panDeltaX1 : panDeltaX2;
        int deltaY = panDeltaY1 != 0 ? panDeltaY1 : panDeltaY2;


        if (deltaX != 0 || deltaY != 0) {
            mSuppMatrix.postTranslate(deltaX, deltaY);
            setImageMatrix(getImageViewMatrix());
        }
    }


    private void centerBasedOnHighlightView(CropFigureView hv) {
        Rect drawRect = hv.mDrawRect;

        float width = drawRect.width();
        float height = drawRect.height();

        float thisWidth = getWidth();
        float thisHeight = getHeight();

        float z1 = thisWidth / width * .6F;
        float z2 = thisHeight / height * .6F;

        float zoom = Math.min(z1, z2);
        zoom = zoom * this.getScale();
        zoom = Math.max(1F, zoom);
        if ((Math.abs(zoom - getScale()) / zoom) > .1) {
            float[] coordinates = new float[]{hv.mCropRect.centerX(),
                    hv.mCropRect.centerY()};
            getImageMatrix().mapPoints(coordinates);
            zoomTo(zoom, coordinates[0], coordinates[1]);
        }
        ensureVisible(hv);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
         mCropFigureView.draw(canvas);
        }

}