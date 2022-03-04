package com.example.busservicedriverapp.registration;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar=Calendar.getInstance();
        int yera=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int dayofmonth=calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(),
                (DatePickerDialog.OnDateSetListener) getActivity(),
                yera,
                month,
                dayofmonth
        );
    }
}
