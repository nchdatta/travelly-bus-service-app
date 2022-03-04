package com.example.busservicedriverapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busservicedriverapp.modelclass.ReportModel;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private List<ReportModel> reportlist ;
    private Context context;

    public RecyclerViewAdapter(List<ReportModel> reportlist, Context context) {
        this.reportlist = reportlist;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_report_layout,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
          holder.date.setText(reportlist.get(position).getDate());
          holder.detailse.setText(reportlist.get(position).getDetailse());
          holder.name.setText(reportlist.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return reportlist.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        TextView date, detailse,name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date =itemView.findViewById(R.id.report_date);
            detailse =itemView.findViewById(R.id.report_detailse);
            name =itemView.findViewById(R.id.report_name);
        }
    }
}
