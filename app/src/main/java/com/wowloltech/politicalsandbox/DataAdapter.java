package com.wowloltech.politicalsandbox;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<String> saves;
    CardView lastCard = null;

    DataAdapter(Context context, List<String> saves) {
        this.saves = saves;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(DataAdapter.ViewHolder holder, int position) {
        String save = saves.get(position);
        holder.nameView.setText(save);
    }

    @Override
    public int getItemCount() {
        return saves.size();
    }

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameView;
        final CardView cardView;

        ViewHolder(View view, final OnItemClickListener listener) {
            super(view);
            nameView = view.findViewById(R.id.listitem_name);
            cardView = view.findViewById(R.id.listitem_card);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION)
                            listener.onItemClick(position);
                        cardView.setCardBackgroundColor(Color.LTGRAY);
                        if (lastCard!=null) lastCard.setCardBackgroundColor(Color.WHITE);
                        lastCard = cardView;
                    }
                }
            });

        }
    }
}
