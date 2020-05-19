package com.example.tensorflowliteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class classifyActivity extends AppCompatActivity {
    ImageView imageView;
    Button button;
    AssetManager assetManager;
    Bitmap resizedBitmap;
    Bitmap bitmap=null;
    Uri uri;
    TextView classlabel;
    private List<String> labels;
    private List<Integer> confidence;
    private List<Pair<Float,String>> results;
    private float[] floatValues;
    private int[] intValues;
    //private String[] outputNames;
    private float[] outputs;
    private String[] outputNames;
    String label = "";
    private static final String MODEL_FILE = "file:///android_asset/opt_mnist_convent.pb";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classify);
        Intent intent = getIntent();
        final String name = intent.getStringExtra("path");
        final String path ="file:"+name;
        uri = Uri.parse(path);
        //uri = Uri.parse(name);
        button=(Button)findViewById(R.id.more);
        //Toast.makeText(classifyActivity.this,"path : "+path,Toast.LENGTH_LONG).show();
        imageView=(ImageView)findViewById(R.id.image1);
        //imageView.setImageURI(uri);
        //File f =new File(path);
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        InputStream istr;
        try {
            //istr = getAssets().open("ten.png");
           // bitmap=BitmapFactory.decodeStream(istr);
            bitmap= MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
        }catch (IOException e){}
            //bitmap=BitmapFactory.decodeFile(f.getAbsolutePath(),options);
        assetManager=getAssets();
        classlabel=(TextView)findViewById(R.id.output);
        TensorFlowInferenceInterface inference = new TensorFlowInferenceInterface(assetManager, "glucosedetectmodel.pb" );
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("glucoselabels.txt")));
            String line;
            labels = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                labels.add(line);

            }

            br.close();
        }catch (IOException e){}

        resizedBitmap = Bitmap.createScaledBitmap(bitmap,224,224,false)
                .copy(Bitmap.Config.ARGB_8888,false);
        imageView.setImageBitmap(resizedBitmap);
        floatValues = new float[224*224*3];
        intValues = new int[224*224];
        resizedBitmap.getPixels(intValues,0,224,0,0,224,224);
        for (int i=0;i<intValues.length;++i){
            floatValues[i*3+0]=((intValues[i]&0xFF)-104);
            floatValues[i*3+1]=(((intValues[i]>>8)&0xFF)-177);
            floatValues[i*3+2]=(((intValues[i]>>16)&0xFF)-123);

            floatValues[i * 3] = Color.red(intValues[i]);
            floatValues[i * 3 + 1] = Color.green(intValues[i]);
            floatValues[i * 3 + 2] = Color.blue(intValues[i]);
        }

        //Toast.makeText(classifyActivity.this,"res:"+labels.size(),Toast.LENGTH_LONG).show();
        outputs = new float[labels.size()];
        inference.feed("Placeholder",floatValues,1,224,224,3);
        outputNames = new String[]{"loss"};
        inference.run(outputNames);
        inference.fetch("loss",outputs);
        List<Classification> myList = new ArrayList<Classification>();

//        List<Pair<Float, String>> words = new ArrayList<Pair<Float, String>>();
//        List<Pair<Float,String>> temp = new ArrayList<Pair<Float, String>>();
        //Classification cs = new Classification(1,"e");
        for (int i=0;i<outputs.length;++i){
            myList.add(new Classification(outputs[i],labels.get(i)));
        }

        Iterator iterator = myList.iterator();

        String conf ="";
        confidence=new ArrayList<>();
        while(iterator.hasNext()){
            Classification st=(Classification) iterator.next();
            label+="\n\t\tPredict is "+st.label+" : "+Math.round(st.conf*100)+"%\n";

              confidence.add(Math.round(st.conf*100));

           // classlabel.setText(label);
            System.out.println(st.conf+" "+st.label);
        }

       // Toast.makeText(classifyActivity.this,"conf"+confidence,Toast.LENGTH_LONG).show();
        int max = 0;
        int index=0;
        for (int i=0;i<confidence.size();i++){
            if(max<confidence.get(i)){
                max=confidence.get(i);
                index=i;
            }
        }

        classlabel.setText("Predicted label : "+labels.get(index)+"\n\nAccuracy : "+max+"%");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(classifyActivity.this," label:"+label,Toast.LENGTH_LONG).show();
                LayoutInflater layoutInflater = LayoutInflater.from(classifyActivity.this);
                View promptView = layoutInflater.inflate(R.layout.prompt,null);
                AlertDialog.Builder alertdialog = new AlertDialog.Builder(classifyActivity.this);
                alertdialog.setView(promptView);
                final TextView moreinfo = new TextView(classifyActivity.this);
                alertdialog.setView(moreinfo);
                moreinfo.setText(label);
                alertdialog.setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                alertdialog.create();
                alertdialog.show();
            }
        });

        //Toast.makeText(classifyActivity.this,"data :"+myList.size(),Toast.LENGTH_LONG).show();
        //Toast.makeText(classifyActivity.this,"Accuracy1:"+outputs[0]+", Accuracy2:"+outputs[1],Toast.LENGTH_LONG).show();



    }
}
