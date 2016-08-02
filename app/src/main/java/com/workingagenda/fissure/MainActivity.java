package com.workingagenda.fissure;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<Bitmap> bitmaps = new ArrayList<>();
    ArrayList<String> images;
    ArrayList<Uri> uris = new ArrayList<>();
    ArrayAdapter<String> adapter;

    EditText editTxt;

    String filename;

    int COMPRESSION = 30; // not a big diff eh?
    int SAMPLE_SIZE = 3; // ?? unclear to me...
    int INDEF_REPEAT = 0;
    int DELAY = 1000; // milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button btnGen = (Button) findViewById(R.id.generateGIF);
        editTxt = (EditText) findViewById(R.id.titleValue);
        final ImageView prevImg = (ImageView) findViewById(R.id.preview);

        // List view of images?
        images = new ArrayList<>();
        ListView lv = (ListView) findViewById(R.id.listImage);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, images);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getBaseContext(), images.get(position),
                        Toast.LENGTH_SHORT).show();
                prevImg.setImageURI(uris.get(position));
            }
        });
        //TODO: Onclick Listener for individual deletes


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
                EditText et = (EditText) findViewById(R.id.titleValue);
                filename = et.getText().toString();

                Log.d("Begin writing gif", "now");
                new GenerateGif().execute(bitmaps);
                // TOAST
                Toast.makeText(getBaseContext(), "Writing GIF as ",
                        Toast.LENGTH_SHORT).show();
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
        if (id == R.id.action_clear) {
            ClearAll();
        }
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    ListView lv = (ListView) findViewById(R.id.listImage);
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        String size = Integer.toString(bitmap.getAllocationByteCount());
                        Log.d("Bitmap One Size:", size);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // Compress
                    File tmpFile = new File(Environment.getExternalStorageDirectory() +
                            File.separator + "tmp.jpeg");
                    FileOutputStream out = null;
                    try {
                        tmpFile.createNewFile();
                        out = new FileOutputStream(tmpFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, out);
                        Log.d("Outstream", out.toString());
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = SAMPLE_SIZE;
                    options.inJustDecodeBounds = false;
                    Bitmap bitmap2 = BitmapFactory.decodeFile(tmpFile.getPath(), options);
                    Log.d("tmpFile", tmpFile.getPath());
                    tmpFile.delete();
                    String size = Integer.toString(bitmap2.getAllocationByteCount());
                    Log.d("Bitmap Two Size:", size);
                    // Update Arrays
                    bitmaps.add(bitmap);
                    images.add(uri.getPath());
                    uris.add(uri);
                    ((ArrayAdapter) lv.getAdapter()).notifyDataSetChanged();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // Save the gif to file system
    public void saveGIF(ArrayList<Bitmap> bitmaps, String filename) {
        // Write Gif
        FileOutputStream outStream = null;
        try {
            //outStream = new FileOutputStream("/storage/emulated/0/test.gif");
            outStream = new FileOutputStream(Environment.getExternalStorageDirectory()
                    + File.separator + filename);// Environment.DIRECTORY_PICTURES + filename
            outStream.write(generateGIF(bitmaps));
            // TOAST
            Toast.makeText(getBaseContext(), "Wrote GIF as " + filename,
                    Toast.LENGTH_SHORT).show();
            outStream.close();

        } catch (Exception e) {
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
        encoder.setRepeat(INDEF_REPEAT);
        // Delay settings:
        // I dunno
        encoder.setDelay(DELAY);
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
        } catch (ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private class GenerateGif extends AsyncTask<ArrayList, Void, Void> {
        @Override
        protected Void doInBackground(ArrayList... params) {
            TextView txtStatus = (TextView) findViewById(R.id.gif_status);
            // Write Gif
            //if filename != null
            String fn;
            if (filename.isEmpty()){
                fn = "myAsyncGif.gif";
            } else {
                fn = filename.concat(".gif");
            }
            FileOutputStream outStream = null;
            try {
                outStream = new FileOutputStream(Environment.getExternalStorageDirectory()
                        + File.separator + fn);// Environment.DIRECTORY_PICTURES + filename
                outStream.write(generateGIF(bitmaps));

                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void a) {
            ClearAll();
        }
    }

    private void ClearAll() {
        bitmaps.clear();
        uris.clear();
        images.clear();
        adapter.clear();
        Toast.makeText(getBaseContext(), "Done Writing GIF",
                        Toast.LENGTH_SHORT).show();
    }

}
