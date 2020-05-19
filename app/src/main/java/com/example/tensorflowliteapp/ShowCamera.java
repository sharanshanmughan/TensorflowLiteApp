package com.example.tensorflowliteapp;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

public class ShowCamera extends SurfaceView implements SurfaceHolder.Callback {
    Camera camera;
    SurfaceHolder holder;
    public ShowCamera(Context context,Camera camera){
        super(context);
        this.camera = camera;
        holder = getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        refreshCamera(camera);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        camera.stopPreview();
        camera.release();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> sizes=parameters.getSupportedPictureSizes();
        Camera.Size msize = null;
        for (Camera.Size size : sizes){
            msize = size;
        }
        if(this.getResources().getConfiguration().orientation!= Configuration.ORIENTATION_LANDSCAPE){
            parameters.set("orientation","portrait");
            camera.setDisplayOrientation(90);
            parameters.setRotation(90);
        }
        else{
            parameters.set("orientation","landscape");
            camera.setDisplayOrientation(0);
            parameters.setRotation(0);
        }
        parameters.setPictureSize(msize.width,msize.height);
        camera.setParameters(parameters);
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void refreshCamera(Camera camera){
        if (holder.getSurface()==null){
            return;
        }
        try{
            camera.stopPreview();
        }catch (Exception e){}
        setCamera(camera);
        try{
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        }catch (Exception e){}
    }
    public void setCamera(Camera mcamera) {
        //method to set a camera instance
        camera = mcamera;
    }
}
