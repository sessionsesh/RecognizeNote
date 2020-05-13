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
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.coopcourse.recognizenote.R;
import com.coopcourse.recognizenote.activities.camera.CameraActivity;
import com.coopcourse.recognizenote.activities.camera.crop.CropActivity;
import com.coopcourse.recognizenote.adapters.RecViewAdapter;
import com.coopcourse.recognizenote.database.ItemDataBase;
import com.coopcourse.recognizenote.database.RecViewItemTable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.net.URI;

public class MainActivity extends AppCompatActivity implements LifecycleOwner {
    /*Permissions*/
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_CODE_PERMISSION = 10;

    /*Result codes*/
    private int PICKFILE_RESULT_CODE = 1;
    private int EDIT_TEXT_ACTIVITY_RESULT_CODE = 2;
    private int CAMERA_ACTIVITY_RESULT_CODE = 3;
    private int CROP_ACTIVITY_RESULT_CODE = 4;

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
            Intent chooseFile = new Intent();
            chooseFile.setAction(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("image/*");  //TODO: Set only images types
            startActivityForResult(
                    Intent.createChooser(chooseFile, "Select a picture"),
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
            grantUriPermission(getPackageName(), selectedImage, 0);
            String path = getImagePath(selectedImage);

            //Starting crop activity
            Intent intent = new Intent(this, CropActivity.class);
            intent.putExtra(CropActivity.IMAGE_PATH, path);
            Log.d("PATH", path);
            intent.putExtra(CropActivity.SCALE, true);
            intent.putExtra("DELETE",false);
            startActivityForResult(intent, CROP_ACTIVITY_RESULT_CODE);
        }

        //updating recyclerView after closing EditTextActivity
        if (requestCode == EDIT_TEXT_ACTIVITY_RESULT_CODE) {
            recyclerViewAdapter.notifyDataSetChanged();
        }

        //updating recyclerView after closing CameraActivity
        if (requestCode == CAMERA_ACTIVITY_RESULT_CODE) {
            recyclerViewAdapter.notifyDataSetChanged();
        }

        if (requestCode == CROP_ACTIVITY_RESULT_CODE) {
            Log.d("CROP","true");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    recyclerViewAdapter.notifyDataSetChanged();
                }
            }, 2000);

        }
    }

    public String getImagePath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
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
