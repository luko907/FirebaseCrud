package com.lucaskoch.firebasecrud.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lucaskoch.firebasecrud.R;
import com.lucaskoch.firebasecrud.model.ItemRVModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ItemRVAdapter extends RecyclerView.Adapter<ItemRVAdapter.ViewHolder> {

    ArrayList<ItemRVModel> itemRVModelArrayList;

    public ItemRVAdapter(ArrayList<ItemRVModel> itemRVModelArrayList) {
        this.itemRVModelArrayList = itemRVModelArrayList;
    }

    @NonNull
    @Override
    public ItemRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.clothe_rv_item, parent, false);

        return new ViewHolder(view);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {


        holder.idITV_title.setText(itemRVModelArrayList.get(position).getTitle() + "...");
        holder.idITV_price.setText("$ " + itemRVModelArrayList.get(position).getPrice());
        Picasso.get().load(itemRVModelArrayList.get(position).getImg()).into(holder.idIV_img);
    }

    @Override
    public int getItemCount() {
        return itemRVModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView idITV_title, idITV_price;
        ImageView idIV_img;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            idITV_title = itemView.findViewById(R.id.idITV_title);
            idITV_price = itemView.findViewById(R.id.idITV_price);
            idIV_img = itemView.findViewById(R.id.idIV_img);
        }
    }
}
