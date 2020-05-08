package com.coopcourse.recognizenote;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    private int PICKFILE_RESULT_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ItemDataBase DB = createDB();
        RecyclerView recyclerView = createRecycleView(DB);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.search_item) {
            Toast.makeText(this, "CAMERA_ACTIVITY_OPEN", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.explorer_item) { // StartActivityForResult
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
            chooseFile.setType("*/*");// TODO: Set only images types
            startActivityForResult(
                    Intent.createChooser(chooseFile, "Choose a file"),
                    PICKFILE_RESULT_CODE
            );

            Toast.makeText(this, "EXPLORER_ACTIVITY_OPEN", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKFILE_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            String URI = data.getData().toString();// TODO: Get correct URI to file
            Toast.makeText(this, URI, Toast.LENGTH_SHORT).show();
            Log.d("URI", URI);
        }

    }


    private RecyclerView createRecycleView(ItemDataBase DB) {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RecViewAdapter(this, DB));
        return recyclerView;
    }

    //Methods for DB testing
    private ItemDataBase createDB() {
        ItemDataBase DB = Room.databaseBuilder(
                getApplicationContext(), ItemDataBase.class, "items_db")
                .allowMainThreadQueries()// QUICK FIX [ASYNC TASK NEEDED]
                .build();

        DB.itemDao().clearTable();
        DB.itemDao().insertItem(new RecViewItemTable("WOW", "14:00"));
        DB.itemDao().insertItem(new RecViewItemTable("KOPOW", "15:00"));
        DB.itemDao().insertItem(new RecViewItemTable("SUPERWOW", "16:00"));
        DB.itemDao().insertItem(new RecViewItemTable("MEGAWOW", "17:00"));
        DB.itemDao().insertItem(new RecViewItemTable("ULTRAWOW", "18:00"));

        return DB;
    }

}
