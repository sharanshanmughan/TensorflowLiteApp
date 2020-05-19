package com.example.tensorflowliteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.PointF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.MotionEvent;
//This class represents the basic building block for user interface components.
// A View occupies a rectangular area on the screen and is responsible for drawing
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import com.bumptech.glide.Glide;
import com.example.tensorflowliteapp.views.DrawModel;
import com.example.tensorflowliteapp.views.DrawView;
public class MainActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 1000;
    private static final int code= 100;
    TextView splash;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        splash = (TextView) findViewById(R.id.gifImage);

         //Glide.with(MainActivity.this).load(R.drawable.img4).into(imageView);
         if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                 == PackageManager.PERMISSION_DENIED) {


             requestPermission();

         }
         else{
             //Toast.makeText(this, "Permission Already Granted", Toast.LENGTH_SHORT).show();
             new Handler().postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     Intent homeIntent = new Intent(MainActivity.this,cameraView.class);
                     startActivity(homeIntent);
                     finish();
                 }
             },SPLASH_TIME_OUT);
         }

}

    private void requestPermission() {
        final String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };
         if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)){
             new AlertDialog.Builder(this)
                     .setTitle("Permission needed")
                     .setMessage("This permission is needed because of this and that")
                     .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                             ActivityCompat.requestPermissions(MainActivity.this,
                                     PERMISSIONS, code);
                         }
                     })
                     .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                             dialog.dismiss();
                         }
                     })
                     .create().show();
         }
         else{
             Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();

             ActivityCompat.requestPermissions(MainActivity.this,PERMISSIONS,code);
         }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == code)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent homeIntent = new Intent(MainActivity.this,cameraView.class);
                        startActivity(homeIntent);
                        finish();
                    }
                },SPLASH_TIME_OUT);
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();

            }
        }
    }
}
