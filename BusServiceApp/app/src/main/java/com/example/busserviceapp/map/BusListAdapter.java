package com.example.busserviceapp.map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busserviceapp.R;
import com.example.busserviceapp.modelclass.BusList;

import java.util.ArrayList;
import java.util.List;

public class BusListAdapter extends RecyclerView.Adapter<BusListAdapter.ViewHolder> {

    private static final String TAG="BusListAdapter";
    private Context context;
    //private HashSet<BusList> buslists_set = new HashSet<>();
    private List<BusList> buslists = new ArrayList<>();
    private AdapterCallBack adapterCallback;


    public BusListAdapter(Context context, /*HashSet<BusList> buslists*/ List<BusList> list,AdapterCallBack callBack) {
        this.context = context;
        //this.buslists_set = buslists;
        //buslists.addAll(buslists_set);
        this.buslists=list;
        this.adapterCallback=callBack;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.available_bus_kayout,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.b_name.setText(buslists.get(position).getBusname());
        holder.b_route.setText(buslists.get(position).getBusroute());
        holder.b_reaching_time.setText(buslists.get(position).getReachingtime());

        /*holder.linear_container_buslist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, ""+buslists.get(position).getBusname()+
                        " time=>"+buslists.get(position).getReachingtime(), Toast.LENGTH_SHORT).show();
            }
        });*/

        holder.btn_catchBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapterCallback.onButtonClickCallBack(buslists.get(position));
            }
        });

        holder.cardview_container_buslist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapterCallback.onViewClickCallBack(buslists.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return buslists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardview_container_buslist;
        TextView b_name,b_route,b_reaching_time;
        Button btn_catchBus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            b_name=itemView.findViewById(R.id.bus_name);
            b_route=itemView.findViewById(R.id.bus_route);
            b_reaching_time=itemView.findViewById(R.id.bus_reaching_time_xml);
            cardview_container_buslist=itemView.findViewById(R.id.container_buslist);
            btn_catchBus=itemView.findViewById(R.id.btn_catch_bus);
        }
    }
}

