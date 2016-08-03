package com.workingagenda.fissure;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

/**
 * Created by fen on 8/3/16.
 */
public class ViewActivity  extends AppCompatActivity {
    File tmpFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tmpFile.delete();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_load) {
            // load gif
            showFileChooser();
            return true;
        } else if (id == R.id.action_clear) {
            WebView webView = (WebView) findViewById(R.id.gifView);
            webView.clearCache(true);
            webView.loadUrl("about:blank");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    final int chunkSize = 1024; // One kb at a time
                    byte[] imageData = new byte[chunkSize];
                    // Load image
                    Uri uri = data.getData();

                    //File file = new File(uri.getPath());
                    tmpFile = new File(Environment.getExternalStorageDirectory() +
                            File.separator + "tmp.jpeg");
                    try {
                        tmpFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    OutputStream out = null;
                    InputStream in = null;
                    try {
                        out = new FileOutputStream(tmpFile);
                        in = getContentResolver().openInputStream(uri);
                        int bytesRead;
                        while((bytesRead = in.read(imageData)) > 0 ) {
                            out.write(Arrays.copyOfRange(imageData, 0, Math.max(0, bytesRead)));
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            out.close();
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    // Construct path and load into Webview
                    String gif = "file://" + tmpFile.getPath();
                    String html = "<body>" + "<img src=\"" + gif + "\"/></body>";
                    WebView webView = (WebView) findViewById(R.id.gifView);
                    //webView.loadUrl("about:blank");
                    webView.clearCache(true);
                    webView.loadDataWithBaseURL("file://android_asset/", html, "text/html", "utf-8", null);

                    // Delete file on destroy
                    //tmpFile.delete();


                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);


    }

    private void loadGIF() {

    }

    private void showFileChooser() {
        // TODO: Check if have permission
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

}
