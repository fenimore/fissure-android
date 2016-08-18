package com.workingagenda.fissure;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.workingagenda.fissure.Adapters.FilesAdapter;

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

        mList.setAdapter(new FilesAdapter(getBaseContext(), R.layout.row_download, files));

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getBaseContext()).setTitle("Delete all downloads")
                        .setMessage("Are you sure you want to delete all episodes?\nLong click and episode to delete them individually.")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                for (File file : files) {
                                    // remove files
                                    file.delete();
                                }
                                files = getListFiles();
                                mList.setAdapter(new FilesAdapter(getBaseContext(), R.layout.row_download, files));
                                Toast toast = Toast.makeText(getBaseContext(), "GIFs Removed", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                }).setIcon(android.R.drawable.ic_dialog_alert).show();
            }
        });
        // Refresh Button
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                files = getListFiles();
                mList.setAdapter(new FilesAdapter(getBaseContext(), R.layout.row_download, files));
            }
        });
        // Click on List item
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File f = files.get(position);
                Intent y = new Intent(getBaseContext(), ViewActivity.class);
                y.setData(Uri.fromFile(f));
                y.setDataAndType(Uri.fromFile(f), "Uri");
                startActivityForResult(y, 1); //Activity load = 0
                finish();
            }
        });

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
