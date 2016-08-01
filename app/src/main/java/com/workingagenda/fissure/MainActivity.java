package com.workingagenda.fissure;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Write Gif
        FileOutputStream outStream = null;
        try{
            outStream = new FileOutputStream("/storage/emulated/0/test.gif");
            outStream.write(generateGIF());
            Log.v("Yup", "a new gif");
            outStream.close();

        }catch(Exception e){
            e.printStackTrace();
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public byte[] generateGIF() {
        Bitmap bm1 = BitmapFactory.decodeResource(getResources(), android.R.drawable.alert_dark_frame);
        Bitmap bm2 = BitmapFactory.decodeResource(getResources(), android.R.drawable.arrow_down_float);
        Bitmap bm3 = BitmapFactory.decodeResource(getResources(), android.R.drawable.arrow_up_float);
        ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
        bitmaps.add(bm1); // Add a bitmap
        bitmaps.add(bm2);
        bitmaps.add(bm3);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.start(bos);
        // 0 is indefinite
        // must be invoked before adding first image!
        encoder.setRepeat(0);
        for (Bitmap bitmap : bitmaps) {
            encoder.addFrame(bitmap);
        }

        encoder.setDelay(2000);
        encoder.finish();
        return bos.toByteArray();
    }
}
