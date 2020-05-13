package com.coopcourse.recognizenote.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.room.Room;

import com.coopcourse.recognizenote.R;
import com.coopcourse.recognizenote.activities.camera.CameraActivity;
import com.coopcourse.recognizenote.database.ItemDataBase;
import com.coopcourse.recognizenote.database.RecViewItemTable;

public class EditTextActivity extends AppCompatActivity {
    private EditText editText;
    private Bundle extras;
    ItemDataBase DB;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_text_activity);
        Toolbar toolbar = findViewById(R.id.toolbar_edit_activity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        DB = Room.databaseBuilder(
                getApplicationContext(), ItemDataBase.class, "items_db")
                .allowMainThreadQueries()// QUICK FIX TODO: [ASYNC TASK NEEDED]
                .build();

        //Setting text from database
        editText = findViewById(R.id.edit_text);
        String recognizedText;
        extras = getIntent().getExtras();
        if (extras == null) {
            recognizedText = "";
            id = 0;
        } else {
            recognizedText = extras.getString("text");
            id = extras.getInt("id");
        }
        editText.setText(recognizedText);

        //Listening to text change and update database entry by ID value, which was received by getExtras() function
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                DB.itemDao().updateItem(new RecViewItemTable(id, editText.getText().toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        LinearLayout linearLayout = findViewById(R.id.edit_text_main_layout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.requestFocus();
                editText.setFocusableInTouchMode(true);
                editText.setSelection(editText.getText().length());
                InputMethodManager inputMethodMngr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodMngr.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (editText.getText().toString().isEmpty()) {
            DB.itemDao().deleteItem(new RecViewItemTable(id, editText.getText().toString()));
        }
        editText.clearFocus();
        finish();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_edit_text, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int item_id = item.getItemId();  //getting id of selected menu item
        if (item_id == R.id.delete_item) {
            DB.itemDao().deleteItem(new RecViewItemTable(id, editText.getText().toString()));
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

}
