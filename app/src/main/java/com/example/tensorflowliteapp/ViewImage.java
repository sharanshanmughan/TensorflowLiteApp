package com.example.tensorflowliteapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;


public class ViewImage extends AppCompatActivity {
    ImageView imageView;
    Button button,detect;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        imageView = (ImageView)findViewById(R.id.imageView);
        //imageView.setImageBitmap(cameraView.bitmap);
        button=(Button)findViewById(R.id.resize);
        detect=(Button)findViewById(R.id.detect);
        Intent intent = getIntent();
        final String name = intent.getStringExtra("path");
        //Toast.makeText(ViewImage.this,"path : "+name,Toast.LENGTH_LONG).show();
        final String path ="file:"+name;
        uri = Uri.parse(path);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UCrop uCrop = UCrop.of(uri,uri);
                uCrop.withAspectRatio(1,1);
                uCrop.withMaxResultSize(450,450);
                uCrop.withOptions(getCropOptions());
                uCrop.start(ViewImage.this);
            }
        });

        imageView.setImageURI(uri);
        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewImage.this,classifyActivity.class);
                intent.putExtra("path", name);
                startActivity(intent);
            }
        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==UCrop.REQUEST_CROP && resultCode==RESULT_OK){
            Uri imageUriResultCrop = UCrop.getOutput(data);
            if (imageUriResultCrop!=null){
                Toast.makeText(ViewImage.this,"work",Toast.LENGTH_LONG).show();
                imageView.setImageURI(null);
                imageView.setImageURI(uri);
            }
        }
    }
}
