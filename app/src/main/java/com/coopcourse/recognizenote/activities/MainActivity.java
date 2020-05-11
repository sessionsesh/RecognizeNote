package com.coopcourse.recognizenote.activities;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.coopcourse.recognizenote.R;
import com.coopcourse.recognizenote.activities.camera.CameraActivity;
import com.coopcourse.recognizenote.adapters.RecViewAdapter;
import com.coopcourse.recognizenote.database.ItemDataBase;
import com.coopcourse.recognizenote.database.RecViewItemTable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements LifecycleOwner {
    /*Permissions*/
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};
    private static final int REQUEST_CODE_PERMISSION = 10;

    /*Result codes*/
    private int PICKFILE_RESULT_CODE = 1;
    private int EDIT_TEXT_ACTIVITY_RESULT_CODE = 2;
    private int CAMERA_ACTIVITY_RESULT_CODE = 3;

    /*How to call it?*/
    RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewAdapter;
    FloatingActionButton fab;
    ItemDataBase DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSION);


        /*Set up database*/
        DB = Room.databaseBuilder(
                getApplicationContext(), ItemDataBase.class, "items_db")
                .allowMainThreadQueries()// QUICK FIX [ASYNC TASK NEEDED]
                .build();//createDB();
        recyclerView = createRecycleView(DB);

        /*FloatingActionButton listening*/
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditTextActivity.class);
                DB.itemDao().insertItem(new RecViewItemTable(""));

                int last_item = DB.itemDao().itemCount() - 1;
                Integer id = DB.itemDao().getItem(last_item).id;
                String text = DB.itemDao().getItem(last_item).text;
                intent.putExtra("text", text);
                intent.putExtra("id", id);

                startActivityForResult(intent, EDIT_TEXT_ACTIVITY_RESULT_CODE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();  //getting id of selected menu item
        if (id == R.id.camera_item) {
            if (!allPermissionsGranted()) {
                ActivityCompat.requestPermissions(
                        this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSION);
            } else {
                Intent myIntent = new Intent(this, CameraActivity.class);
                startActivityForResult(myIntent, CAMERA_ACTIVITY_RESULT_CODE);
            }
        }

        if (id == R.id.explorer_item) { // StartActivityForResult
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
            chooseFile.setType("*/*");  //TODO: Set only images types
            startActivityForResult(
                    Intent.createChooser(chooseFile, "Choose a file"),
                    PICKFILE_RESULT_CODE
            );
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // getting URI of selected in file explorer picture
        if (requestCode == PICKFILE_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
        }

        //updating recyclerView after closing EditTextActivity
        if (requestCode == EDIT_TEXT_ACTIVITY_RESULT_CODE) {
            recyclerViewAdapter.notifyDataSetChanged();
        }

        //updating recyclerView after closing CameraActivity
        if (requestCode == CAMERA_ACTIVITY_RESULT_CODE) {
            recyclerViewAdapter.notifyDataSetChanged();
        }
    }

    private RecyclerView createRecycleView(ItemDataBase DB) {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdapter = new RecViewAdapter(MainActivity.this, DB);
        recyclerView.setAdapter(recyclerViewAdapter);
        return recyclerView;
    }

    private boolean allPermissionsGranted() {
        for (String perm : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(getBaseContext(), perm) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
