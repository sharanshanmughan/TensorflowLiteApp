package com.example.tensorflowliteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.naver.android.helloyako.imagecrop.view.ImageCropView;
import com.yalantis.ucrop.UCrop;

import java.io.File;

public class ResizeActivity extends AppCompatActivity {
    ImageCropView imageCropView;
    Button click;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resize);
        imageCropView = (ImageCropView) findViewById(R.id.cropImage);
        Intent intent = getIntent();
        String name = intent.getStringExtra("Uri");
        final String path =name;
        final Uri uri = Uri.parse(path);
        File folder_gui = new File(Environment.getExternalStorageDirectory()+File.separator+"GUI/11_24_36.jpg");

        Toast.makeText(ResizeActivity.this,"uri:"+folder_gui,Toast.LENGTH_LONG).show();
        imageCropView.setImageURI(uri);
        imageCropView.setGridInnerMode(ImageCropView.GRID_ON);
        imageCropView.setGridOuterMode(ImageCropView.GRID_ON);
        click=(Button)findViewById(R.id.click);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UCrop uCrop = UCrop.of(uri,uri);
                uCrop.withAspectRatio(1,1);
                uCrop.withMaxResultSize(450,450);
                uCrop.withOptions(getCropOptions());
                uCrop.start(ResizeActivity.this);
            }
        });
       // imageCropView.setScaleX(2);
    }

    private UCrop.Options getCropOptions() {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(70);
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(true);
        options.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        options.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        options.setToolbarTitle("Recortar image");
        return options;
    }
}
