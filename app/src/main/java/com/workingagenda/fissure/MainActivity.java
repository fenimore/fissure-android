package com.workingagenda.fissure;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
    ArrayList<String> images;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button btnGen = (Button)findViewById(R.id.generateGIF);
        images = new ArrayList<String>();
        ListView lv = (ListView)findViewById(R.id.listImage);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, images);

        lv.setAdapter(adapter);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
                Snackbar.make(view, "Select Image", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        btnGen.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
                 // Perform action on click

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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d("Yup", "File Uri Path: " + uri.getPath());
                    //Log.d("Real Path", getRealPathFromURI(uri));
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        TextView tv = (TextView)findViewById(R.id.imageSelections);
                        ListView lv = (ListView)findViewById(R.id.listImage);

                        tv.append("\n" + uri.getPath());
                        bitmaps.add(bitmap);
                        images.add(uri.getPath());
                        ((ArrayAdapter) lv.getAdapter()).notifyDataSetChanged();
                        if(bitmap!=null)  {
                            bitmap.recycle();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    // Save the gif to file system
    public void saveGIF(ArrayList<Bitmap> bitmaps, String filename) {
        // Write Gif
        FileOutputStream outStream = null;
        try{
            //outStream = new FileOutputStream("/storage/emulated/0/test.gif");
            outStream = new FileOutputStream(Environment.DIRECTORY_PICTURES + "/gif/" + filename);
            outStream.write(generateGIF(bitmaps));
            // TOAST
            outStream.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    // Return a byte[] which is infact an encoded GIF
    public byte[] generateGIF(ArrayList<Bitmap> bitmaps) { // pass in bitmap array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.start(bos);
        // Repeat setting:
        // 0 is indefinite
        // must be invoked before adding first image!
        encoder.setRepeat(0);
        // Delay settings:
        // I dunno
        encoder.setDelay(2000);
        // Size Settings:
        // I dunno
        for (Bitmap bitmap : bitmaps) {
            encoder.addFrame(bitmap);
        }
        encoder.finish();
        return bos.toByteArray();
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    0);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

}
