package com.coopcourse.recognizenote;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditTextActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_text_activity);

        String recognizedText;
        Bundle extras = getIntent().getExtras();
        EditText editText = findViewById(R.id.edit_text);

        if (extras == null) recognizedText = "";
        else recognizedText = extras.getString("text");
        editText.setText(recognizedText);


        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
