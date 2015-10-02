package com.codepath.todoapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.codepath.todoapp.R;
import com.codepath.todoapp.db.model.Todos;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class TodoItemAdapter extends ArrayAdapter<Todos> {

    private static class ViewHolder {
        TextView item;
        TextView subItem;
    }
    public TodoItemAdapter(Context context, ArrayList<Todos> todoItemList) {
        super(context, 0, todoItemList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_todo, parent, false);
            viewHolder.item = (TextView) convertView.findViewById(R.id.tvItem);
            viewHolder.subItem = (TextView) convertView.findViewById(R.id.tvSubItem);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Get TodoItem for this position
        Todos todoItem = getItem(position);

        // Populate Data
        viewHolder.item.setText(todoItem.getItem());

        // Format Date
        this.formatDate(convertView, todoItem, viewHolder);

        // Return completed view
        return convertView;
    }

    private void formatDate(View convertView, Todos todoItem, ViewHolder viewHolder){
        // Format Date for display
        if(todoItem.getDueDate() != null){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            try{
                Date today = sdf.parse(sdf.format(new Date()));
                if(today.compareTo(todoItem.getDueDate()) > 0){
                    // Due over
                    String  subItem = new SimpleDateFormat("E d MMM", Locale.US).format(todoItem.getDueDate());
                    viewHolder.subItem.setText("Already passed due! " + subItem);
                    viewHolder.subItem.setTextColor(Color.RED);
                    convertView.setBackgroundColor(Color.parseColor("#FFB2B2")); // red
                }else if(today.compareTo(todoItem.getDueDate()) < 0){
                    // In future
                    String subItem = new SimpleDateFormat("E d MMM", Locale.US).format(todoItem.getDueDate());
                    viewHolder.subItem.setText(subItem);
                    viewHolder.subItem.setTextColor(Color.GRAY);
                    convertView.setBackgroundColor(Color.WHITE); // white
                }else if(today.compareTo(todoItem.getDueDate()) == 0){
                    // Its today
                    viewHolder.subItem.setText("Due today");
                    viewHolder.subItem.setTextColor(Color.GRAY);
                    convertView.setBackgroundColor(Color.parseColor("#FFFFE6")); // yellow
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }else{
            viewHolder.subItem.setText("");
            convertView.setBackgroundColor(Color.WHITE);
        }
    }
}
