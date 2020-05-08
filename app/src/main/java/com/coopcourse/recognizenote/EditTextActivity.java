package com.coopcourse.recognizenote;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

public class EditTextActivity extends AppCompatActivity {
    private EditText editText;
    private Bundle extras;
    ItemDataBase DB;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_text_activity);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        DB = Room.databaseBuilder(
                getApplicationContext(), ItemDataBase.class, "items_db")
                .allowMainThreadQueries()// QUICK FIX TODO: [ASYNC TASK NEEDED]
                .build();

        //Setting text from database
        editText = findViewById(R.id.edit_text);
        String recognizedText;
        int id;
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
