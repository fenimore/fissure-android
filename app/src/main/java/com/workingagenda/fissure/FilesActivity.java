package com.workingagenda.fissure;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.workingagenda.fissure.Adapters.FilesAdapter;

import java.io.File;
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

        mList.setAdapter(new FilesAdapter(getBaseContext(), R.layout.row_file, files));


        // Click on List item
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File f = files.get(position);
                Intent y = getIntent();//new Intent(getBaseContext(), ViewActivity.class);
                y.setData(Uri.fromFile(f));
                // IMPORTANT WTF
                // This activity must have been started from
                // another activity with startActivityForResult()
                // It's not that I start this activity as such...
                setResult(RESULT_OK, y);
                Log.d("Result aaught to be", String.valueOf(RESULT_OK));
                finish();
            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == android.R.id.list) {
            MenuInflater inflater = new MenuInflater(getBaseContext());
            menu.setHeaderTitle("Gif Select");
            inflater.inflate(R.menu.menu_context, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int pos = info.position;
        File file = files.get(pos);
        switch(item.getItemId()) {
            case R.id.action_remove:
                file.delete();
                files = getListFiles();
                mList.setAdapter(new FilesAdapter(getBaseContext(), R.layout.row_file, files));
                return true;
            default:
                return super.onContextItemSelected(item);
        }
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
