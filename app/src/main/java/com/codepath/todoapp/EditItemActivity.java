package com.codepath.todoapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class EditItemActivity extends AppCompatActivity {

    EditText etEditText;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        //Read data from request
        position = getIntent().getIntExtra("position", 0);
        String editToDoText = getIntent().getStringExtra("editToDoText");

        etEditText = (EditText) findViewById(R.id.etEditText);
        etEditText.setText(editToDoText);
        etEditText.setSelection(etEditText.getText().length());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSaveItem(View view) {
        Intent i = new Intent();
        // Pass resulted data
        i.putExtra("editToDoText", etEditText.getText().toString());
        i.putExtra("position", position);

        setResult(RESULT_OK, i);
        finish();
    }
}
