package com.workingagenda.fissure;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fen on 8/18/16.
 */
public class FilesActivity extends AppCompatActivity {

    //Declare some Views
    public Button btnClear;
    public Button btnRefresh;
    public ListView mList;
    // File List
    public List<File> files;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // ls the Gifs dir
        files = getListFiles();
        // Views items
        mList = (ListView) findViewById(android.R.id.list);
        btnClear = (Button) findViewById(R.id.clear);
        btnRefresh = (Button) findViewById(R.id.refresh);
        // Context registration
        registerForContextMenu(mList);

    }

    private List<File> getListFiles() {
        ArrayList<File> inFiles = new ArrayList<File>(); // Must be arraylist no List
        File parentDir = new File(Environment.getExternalStorageDirectory() + File.separator +
                Environment.DIRECTORY_PICTURES + File.separator +"Gifs");
        File[] files = parentDir.listFiles();
        if(files != null) {// why do I need this?
            for (File file : files) {
                if(file.getName().endsWith(".gif")){
                    inFiles.add(file);
                }
            }
        }
        return inFiles;
    }
}
