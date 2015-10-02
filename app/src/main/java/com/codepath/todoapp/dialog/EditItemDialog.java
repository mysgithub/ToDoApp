package com.codepath.todoapp.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.todoapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditItemDialog extends DialogFragment implements TextView.OnClickListener{

    EditText etEditText;
    Button btnSave;
    Long newDueDate = null;
    private static String DATE_FORMAT="yyyy-MM-dd";

    public EditItemDialog(){

    }

    public interface OnItemUpdateListener {
        public void onItemUpdate(int position, String editToDoText, Long dueDate);
    }

    public static EditItemDialog newInstance(int position, String editToDoText, Date dueDate){
        EditItemDialog editItemDialog = new EditItemDialog();
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("editToDoText", editToDoText);
        if(dueDate != null) {
            args.putLong("dueDate", dueDate.getTime());
        }
        editItemDialog.setArguments(args);
        return editItemDialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_item, container);

        // Read data from request
        String editToDoText = getArguments().getString("editToDoText");

        // View
        etEditText = (EditText) view.findViewById(R.id.etEditText);
        etEditText.setText(editToDoText);
        etEditText.setSelection(etEditText.getText().length());

        // Button Listner
        btnSave = (Button) view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        // Title
        getDialog().setTitle(R.string.title_activity_edit_item);

        // DatePicker
        DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        datePicker.setSpinnersShown(true);
        Calendar c = Calendar.getInstance();
        if(getArguments().getLong("dueDate", 0L) > 0){
            c.setTimeInMillis(getArguments().getLong("dueDate"));
        }
        datePicker.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
                try {
                    newDueDate = sdf.parse(sdf.format(calendar.getTime())).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });


        return view;
    }

    @Override
    public void onClick(View view){

        etEditText = (EditText) this.getView().findViewById(R.id.etEditText);

        if(!etEditText.getText().toString().isEmpty()) {
            if(newDueDate == null){
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
                try {
                    newDueDate = sdf.parse(sdf.format(calendar.getTime())).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            OnItemUpdateListener onItemUpdateListener = (OnItemUpdateListener) getActivity();
            onItemUpdateListener.onItemUpdate(getArguments().getInt("position"), etEditText.getText().toString(), newDueDate);
            dismiss();
        }else {
            Toast.makeText(this.getContext(), getString(R.string.error_valid_item), Toast.LENGTH_SHORT).show();
        }

    }

}
