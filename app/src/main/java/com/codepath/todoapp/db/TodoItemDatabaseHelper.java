package com.codepath.todoapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.codepath.todoapp.db.model.Todos;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


public class TodoItemDatabaseHelper extends SQLiteOpenHelper {

    // Database info
    private static final String DATABASE_NAME = "todoDatabase";
    private static final int DATABASE_VERSION = 2;

    // Table name
    private static final String TABLE_TODOS = "todos";

    // todos table columns
    private static final String COL_TODO_ID = "id";
    private static final String COL_TODO_ITEM = "item";
    private static final String COL_TODO_DATE = "due_date";

    // Date format
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    // Singleton
    private static TodoItemDatabaseHelper instance;

    // Get Singleton
    public static synchronized TodoItemDatabaseHelper getInstance(Context context){
        if(instance == null){
            instance = new TodoItemDatabaseHelper(context);
        }
        return instance;
    }

    private TodoItemDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTodoItemsTblQuery = "CREATE TABLE "+ TABLE_TODOS +
                " ( " +
                    COL_TODO_ID + " INTEGER PRIMARY KEY, " +
                    COL_TODO_ITEM + " TEXT, " +
                    COL_TODO_DATE + " TEXT " +
                " ) ";
        db.execSQL(createTodoItemsTblQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            String dropTodoItemsTblQuery = "DROP TABLE IF EXISTS " + TABLE_TODOS;
            db.execSQL(dropTodoItemsTblQuery);
        }
    }

    /**
     * Insert OR Update ToDoItem in database
     */
    public void insertOrUpdateTodo(Todos todos){
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put(COL_TODO_ITEM, todos.getItem());
            if(todos.getDueDate() != null){
                values.put(COL_TODO_DATE, todos.getDueDate().getTime());
            }

            if(todos.getId() == null){
                // insert
                Long id = db.insertOrThrow(TABLE_TODOS, null, values);
                todos.setId(id);
            }else{
                // update
                db.update(TABLE_TODOS, values, COL_TODO_ID + "= ?" ,new String[]{todos.getId().toString()});
            }

            db.setTransactionSuccessful();
        }catch (Exception ex){
            Log.d(getClass().toString(), "Error while trying to insert or update ToDo to database");
        }finally {
            db.endTransaction();
        }
    }

    /**
     * Delete ToDoItem in database
     */
    public void deleteTodo(Todos todos){
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try{
            db.delete(TABLE_TODOS, COL_TODO_ID + " = ?", new String[]{todos.getId().toString()});
            db.setTransactionSuccessful();
        }catch (Exception ex){
            Log.d(getClass().toString(), "Error while deleting ToDo");
        }finally {
            db.endTransaction();
        }
    }

    /**
     * Get all ToDoItems from database
     */
    public ArrayList<Todos> getAllTodos(){
        ArrayList<Todos> allTodos = new ArrayList<>();
        String selectQuery = String.format("SELECT * FROM %s", TABLE_TODOS);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        try{
            if(cursor.moveToFirst()){
                do{
                    Todos todos = new Todos();
                    todos.setId(cursor.getLong(cursor.getColumnIndex(COL_TODO_ID)));
                    todos.setItem(cursor.getString(cursor.getColumnIndex(COL_TODO_ITEM)));
                    long dueDate = cursor.getLong(cursor.getColumnIndex(COL_TODO_DATE));
                    if(dueDate > 0){
                        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
                        todos.setDueDate(sdf.parse(sdf.format(dueDate)));
                    }

                    allTodos.add(todos);
                }while (cursor.moveToNext());
            }
        }catch (Exception ex){
            Log.d(getClass().toString(), "Unable to read data from database");
        }finally {
            if(cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }

        return allTodos;
    }

}
