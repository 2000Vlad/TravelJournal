package com.course.traveljournal;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private int mDateCode;
    private OnDateReceivedListener mListener;



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mListener.onDateReceived(year, month, dayOfMonth, mDateCode);
    }

    public int getDateCode() {
        return mDateCode;
    }

    public void setDateCode(int dateType) {
        mDateCode = dateType;
    }

    public OnDateReceivedListener getListener() {
        return mListener;
    }

    public void setListener(OnDateReceivedListener listener) {
        mListener = listener;
    }
}
