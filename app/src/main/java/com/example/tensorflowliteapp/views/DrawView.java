package com.example.tensorflowliteapp.views;

/*
   Copyright 2016 Narrative Nights Inc. All Rights Reserved.
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
   http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * Changed by marianne-linhares on 20/04/17.
 */

public class DrawView extends View {

    private Paint mPaint = new Paint();
    private DrawModel mModel;

    // 28x28 pixel Bitmap
    private Bitmap mOffscreenBitmap;
    private Canvas mOffscreenCanvas;

    private Matrix mMatrix = new Matrix();
    private Matrix mInvMatrix = new Matrix();
    private int mDrawnLineSize = 0;
    private boolean mSetuped = false;

    private float mTmpPoints[] = new float[2];

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setModel(DrawModel model) {
        this.mModel = model;
    }

    //reset the view, so empty the drawing (set everything to white and redraw the 28x28
    //rectangle
    public void reset() {
        mDrawnLineSize = 0;
        if (mOffscreenBitmap != null) {
            mPaint.setColor(Color.WHITE);
            int width = mOffscreenBitmap.getWidth();
            int height = mOffscreenBitmap.getHeight();
            mOffscreenCanvas.drawRect(new Rect(0, 0, width, height), mPaint);
        }
    }

    //create the view, for a given length and width
    private void setup() {
        mSetuped = true;

        // View size
        float width = getWidth();
        float height = getHeight();

        // Model (bitmap) size
        float modelWidth = mModel.getWidth();
        float modelHeight = mModel.getHeight();

        float scaleW = width / modelWidth;
        float scaleH = height / modelHeight;

        float scale = scaleW;
        if (scale > scaleH) {
            scale = scaleH;
        }

        float newCx = modelWidth * scale / 2;
        float newCy = modelHeight * scale / 2;
        float dx = width / 2 - newCx;
        float dy = height / 2 - newCy;

        mMatrix.setScale(scale, scale);
        mMatrix.postTranslate(dx, dy);
        mMatrix.invert(mInvMatrix);
        mSetuped = true;
    }

    @Override
    //when the user begins drawing, initialize
    //the model renderer class and draw it on the canvas
    public void onDraw(Canvas canvas) {
        if (mModel == null) {
            return;
        }
        if (!mSetuped) {
            setup();
        }
        if (mOffscreenBitmap == null) {
            return;
        }

        int startIndex = mDrawnLineSize - 1;
        if (startIndex < 0) {
            startIndex = 0;
        }

        DrawRenderer.renderModel(mOffscreenCanvas, mModel, mPaint, startIndex);
        canvas.drawBitmap(mOffscreenBitmap, mMatrix, mPaint);

        mDrawnLineSize = mModel.getLineSize();
    }

    /**
     * Convert screen position to local pos (pos in bitmap)
     */
    //calculates the position of the finger
    public void calcPos(float x, float y, PointF out) {
        mTmpPoints[0] = x;
        mTmpPoints[1] = y;
        mInvMatrix.mapPoints(mTmpPoints);
        out.x = mTmpPoints[0];
        out.y = mTmpPoints[1];
    }

    public void onResume() {
        createBitmap();
    }

    public void onPause() {
        releaseBitmap();
    }

    //to draw the canvas we need the bitmap
    private void createBitmap() {
        if (mOffscreenBitmap != null) {
            mOffscreenBitmap.recycle();
        }
        mOffscreenBitmap = Bitmap.createBitmap(mModel.getWidth(), mModel.getHeight(), Bitmap.Config.ARGB_8888);
        mOffscreenCanvas = new Canvas(mOffscreenBitmap);
        reset();
    }

    private void releaseBitmap() {
        if (mOffscreenBitmap != null) {
            mOffscreenBitmap.recycle();
            mOffscreenBitmap = null;
            mOffscreenCanvas = null;
        }
        reset();
    }

    /**
     * Get 28x28 pixel data for tensorflow input.
     */
    public float[] getPixelData() {
        if (mOffscreenBitmap == null) {
            return null;
        }

        int width = mOffscreenBitmap.getWidth();
        int height = mOffscreenBitmap.getHeight();

        // Get 28x28 pixel data from bitmap
        int[] pixels = new int[width * height];
        mOffscreenBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        float[] retPixels = new float[pixels.length];
        for (int i = 0; i < pixels.length; ++i) {
            // Set 0 for white and 255 for black pixel
            int pix = pixels[i];
            int b = pix & 0xff;
            retPixels[i] = (float)((0xff - b)/255.0);
        }
        return retPixels;
    }
}
