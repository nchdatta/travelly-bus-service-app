package com.example.busservicedriverapp.registration;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.busservicedriverapp.R;
import com.example.busservicedriverapp.modelclass.BusCatagory;

import java.util.ArrayList;

public class CustomSpinnerAdapter extends ArrayAdapter<BusCatagory> {

    public CustomSpinnerAdapter(Context context, ArrayList<BusCatagory> countryList) {
        super(context, 0, countryList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.bus_catagory_cs, parent, false
            );
        }

        TextView bus_catagory = convertView.findViewById(R.id.bus_catagory_xml);
        TextView bus_route = convertView.findViewById(R.id.bus_route_xml);

        BusCatagory currentItem = getItem(position);

        if (currentItem != null) {
            bus_catagory.setText(currentItem.getBus_catagory());
            bus_route.setText(currentItem.getBus_route());
        }

        return convertView;
    }
}