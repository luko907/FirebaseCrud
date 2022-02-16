package com.lucaskoch.firebasecrud.adapter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.lucaskoch.firebasecrud.R;
import com.lucaskoch.firebasecrud.fragment.ProductDetails;
import com.lucaskoch.firebasecrud.model.ItemRVModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ItemRVAdapter extends RecyclerView.Adapter<ItemRVAdapter.ViewHolder> {

    private final ArrayList<ItemRVModel> itemRVModelArrayList;

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


        holder.idITV_title.setText(itemRVModelArrayList.get(position).getTitle());
        holder.idITV_price.setText("$ " + itemRVModelArrayList.get(position).getPrice());
        Picasso.get().load(itemRVModelArrayList.get(position).getImg()).into(holder.idIV_img);
        holder.idCV_itemContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("title",itemRVModelArrayList.get(position).getTitle().toString());
                bundle.putString("price",itemRVModelArrayList.get(position).getPrice().toString());
                bundle.putString("size",itemRVModelArrayList.get(position).getSize().toString());
                bundle.putString("id",itemRVModelArrayList.get(position).getItemID().toString());


                bundle.putString("type_gender",itemRVModelArrayList.get(position).getType().toString() + " | " + itemRVModelArrayList.get(position).getGender().toString());


                bundle.putString("description",itemRVModelArrayList.get(position).getDescription().toString());
                bundle.putString("image", itemRVModelArrayList.get(position).getImg());
                ProductDetails productDetails = new ProductDetails();
                productDetails.setArguments(bundle);
                FragmentTransaction fragmentTransaction = ((AppCompatActivity)v.getContext()).getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout_fragment_container, productDetails).addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        
    }

    @Override
    public int getItemCount() {
        return itemRVModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView idITV_title, idITV_price;
        ImageView idIV_img;
        CardView idCV_itemContainer;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            idITV_title = itemView.findViewById(R.id.idITV_title);
            idITV_price = itemView.findViewById(R.id.idITV_price);
            idIV_img = itemView.findViewById(R.id.idCV_img_container);
            idCV_itemContainer = itemView.findViewById(R.id.idCV_itemContainer);
        }
    }
}
