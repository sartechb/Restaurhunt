package com.sarthakb.restaurhunt;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Sarthak on 10/8/16.
 */

public class StagViewAdapter extends RecyclerView.Adapter<StagViewAdapter.FoodViewHolder> {
    private List<FoodItem> itemList;
    private Context context;

    public StagViewAdapter(Context context, List<FoodItem> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_card_view, null);
        FoodViewHolder rcv = new FoodViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(FoodViewHolder holder, int position) {
        Picasso.with(context).load(itemList.get(position).getImageUrl()).into(holder.countryPhoto);
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView countryPhoto;

        public FoodViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            countryPhoto = (ImageView) itemView.findViewById(R.id.card_image);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(), "Clicked Position = " + getPosition(), Toast.LENGTH_SHORT).show();
        }
    }
}
