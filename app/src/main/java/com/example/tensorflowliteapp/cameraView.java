package com.example.tensorflowliteapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class cameraView extends AppCompatActivity {


    ShowCamera showCamera;
    Button button,gallery;
    FrameLayout frameLayout;
    public static Bitmap bitmap;
    private Camera.PictureCallback mPicture;
    Camera camera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view);
        button = (Button) findViewById(R.id.capture);
        gallery = (Button) findViewById(R.id.gallery);

        frameLayout=(FrameLayout)findViewById(R.id.cameraView);
        try {

            camera = Camera.open();

        } catch (Exception e) {
            Toast.makeText(this,"not working",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

       // camera=Camera.open();
        showCamera=new ShowCamera(this,camera);
        frameLayout.addView(showCamera);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivity(intent);
                if (camera!=null){
                    camera.takePicture(null,null,mPictureCallback);

                }

            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //currentTime = Calendar.getInstance().getTime();
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH_mm_ss");
                String strDate = sdf.format(c.getTime());
                Toast.makeText(cameraView.this,"time:"+strDate,Toast.LENGTH_SHORT).show();
            }
        });

    }

    private File getOutputMediaFile() {
        String state = Environment.getExternalStorageState();
        if (!state.equals(Environment.MEDIA_MOUNTED)){
           return null;
        }
        else {
            File folder_gui = new File(Environment.getExternalStorageDirectory()+File.separator+"GUI");
            if(!folder_gui.exists()){
                folder_gui.mkdir();
            }
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH_mm_ss");
            String strDate = sdf.format(c.getTime());
            File outputFile = new File(folder_gui,strDate+".jpg");
            return outputFile;
        }
    }


    public void onResume() {

        super.onResume();
        if(camera == null) {
            camera = Camera.open();
            camera.setDisplayOrientation(90);
            mPicture = mPictureCallback;
            showCamera.refreshCamera(camera);

        }else {

        }

    }
    @Override
    protected void onPause() {
        super.onPause();
        //when on Pause, release camera in order to be used from other applications
        releaseCamera();

    }

    private void releaseCamera() {
        // stop and release camera
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            //camera.release();
            camera = null;
        }
    }
    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {

            File pictureFile = getOutputMediaFile();
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            if (pictureFile==null){
                return;
            }
            else {
                try{
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    //Toast.makeText(cameraView.this,"fos:"+pictureFile,Toast.LENGTH_SHORT).show();
                    fos.write(bytes);
                    fos.close();
                   //camera.startPreview();
                    Toast.makeText(cameraView.this, "Saved", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(cameraView.this,ViewImage.class);
                    intent.putExtra("path", pictureFile.toString());
                    //camera.release();
                    //releaseCamera();
                    startActivity(intent);

                }catch (IOException e){
                    e.printStackTrace();
                }
            }

        }
    };

}
