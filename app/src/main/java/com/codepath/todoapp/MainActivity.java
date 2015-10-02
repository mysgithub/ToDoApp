package com.codepath.todoapp;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.todoapp.adapter.TodoItemAdapter;
import com.codepath.todoapp.db.TodoItemDatabaseHelper;
import com.codepath.todoapp.db.model.Todos;
import com.codepath.todoapp.dialog.EditItemDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity implements EditItemDialog.OnItemUpdateListener {

    ArrayList<Todos> todosArrayList;
    TodoItemAdapter todoItemAdapter;
    ListView lvItems;
    EditText etEditText;
    TodoItemDatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // GetInstance of database helper
        dbHelper = TodoItemDatabaseHelper.getInstance(this);
        // Get the place to display list
        lvItems = (ListView) findViewById(R.id.lvItems);
        // Text field
        etEditText = (EditText) findViewById(R.id.etEditText);
        // Display ToDoList
        populateTodoItems();


        // Delete Listener
        lvItems.setOnItemLongClickListener(
            new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    // remove from database
                    dbHelper.deleteTodo(todosArrayList.get(position));
                    // remove from list
                    todosArrayList.remove(position);
                    // update adapter
                    todoItemAdapter.notifyDataSetChanged();
                    // Display some message
                    Toast.makeText(MainActivity.this, getString(R.string.item_deleted_success), Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
        );

        // Edit Listener
        lvItems.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        showEditDialog(position);
                    }
                }
        );


    }

    public void populateTodoItems(){
        // Load from database
        todosArrayList = dbHelper.getAllTodos();
        // sort by date
        sortByDate(todosArrayList);
        // Assign to adapter
        todoItemAdapter = new TodoItemAdapter(this, todosArrayList);
        // Set Adapter
        lvItems.setAdapter(todoItemAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void onAddItem(View view) {
        if(!etEditText.getText().toString().isEmpty()) {
            // Set object
            Todos aTodo = new Todos();
            aTodo.setItem(etEditText.getText().toString());
            // update db
            dbHelper.insertOrUpdateTodo(aTodo);
            // update adapter
            todoItemAdapter.add(aTodo);
            // clear text box
            etEditText.setText("");
            // Display some message
            Toast.makeText(this, getString(R.string.item_added_success), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, getString(R.string.error_valid_item), Toast.LENGTH_SHORT).show();
        }
    }


    private void showEditDialog(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        EditItemDialog editItemDialog = EditItemDialog.newInstance(
                position,
                todosArrayList.get(position).getItem(),
                todosArrayList.get(position).getDueDate());

        editItemDialog.show(fragmentManager, "fragment_edit_item");

    }

    @Override
    public void onItemUpdate(int position, String editToDoText, Long dueDate) {
        // set object
        Todos aTodo = todosArrayList.get(position);
        aTodo.setItem(editToDoText);
        if(dueDate != null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dueDate);
            aTodo.setDueDate(calendar.getTime());
        }
        // update database
        dbHelper.insertOrUpdateTodo(aTodo);
        // display it on screen
        todosArrayList.set(position, aTodo);
        // sort by date
        sortByDate(todosArrayList);
        // update adapter
        todoItemAdapter.notifyDataSetChanged();
        // Display some message
        Toast.makeText(this, getString(R.string.item_updated_success), Toast.LENGTH_SHORT).show();
    }

    private void sortByDate(ArrayList<Todos> todosArrayList){
        Collections.sort(todosArrayList, new Comparator<Todos>() {
            @Override
            public int compare(Todos lhs, Todos rhs) {
                if(lhs.getDueDate() != null && rhs.getDueDate() != null){
                    if (lhs.getDueDate().getTime() > rhs.getDueDate().getTime()) {
                        return 1;
                    } else if (lhs.getDueDate().getTime() < rhs.getDueDate().getTime()) {
                        return -1;
                    }
                }
                return 0;
            }
        });
    }
}
