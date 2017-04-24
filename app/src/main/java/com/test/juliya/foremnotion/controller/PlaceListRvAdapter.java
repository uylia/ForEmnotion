package com.test.juliya.foremnotion.controller;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.test.juliya.foremnotion.R;
import com.test.juliya.foremnotion.model.CardModel;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by juliya on 23.04.2017.
 */

public class PlaceListRvAdapter extends RecyclerView.Adapter<PlaceListRvAdapter.MyViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<CardModel> list = new ArrayList<>();

    public PlaceListRvAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = inflater.inflate(R.layout.card_item, parent, false);
        return new MyViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final CardModel cardItem = getItem(position);
        holder.sourceTitle.setText(cardItem.getTitleSource());
        holder.destinationTitle.setText(cardItem.getTitleDestination());
        holder.distanceTitle.setText(String.valueOf(cardItem.getValueDistance()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public CardModel getItem(int position) {
        return list.get(position);
    }

    public void updateDataList(CardModel cardModel){
        if (cardModel == null){
            list.clear();
        } else {
            list.add(cardModel);
        }
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.source_title)
        TextView sourceTitle;
        @Bind(R.id.destination_title)
        TextView destinationTitle;
        @Bind(R.id.distance_title)
        TextView distanceTitle;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
