package com.example.busserviceapp.map;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.busserviceapp.R;
import com.example.busserviceapp.modelclass.SearchBody;

import java.util.List;

public class SearchListAdapter extends ArrayAdapter<SearchBody> {

    private Context context;
    List<SearchBody> artistlist;

    public SearchListAdapter (Context context,List<SearchBody>list){
        super(context, R.layout.search_history_layout,list);
        this.context=context;
        this.artistlist=list;
    }

    public SearchListAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view= LayoutInflater.from(context).inflate(R.layout.search_history_layout,null,true);
        TextView tv_name=(TextView)view.findViewById(R.id.place_name);
        TextView tv_address=(TextView)view.findViewById(R.id.place_address);

        SearchBody sb=artistlist.get(position);
        tv_name.setText(sb.getName());
        tv_address.setText(sb.getAddress());

        return view;
    }
}