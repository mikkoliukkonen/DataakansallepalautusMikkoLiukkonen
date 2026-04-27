package com.example.dataakansalleliukkonenmikko;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DataItemAdapter extends RecyclerView.Adapter<DataItemAdapter.DataItemViewHolder> {

    private List<DataItem> dataItems;

    public DataItemAdapter(List<DataItem> dataItems) {
        this.dataItems = dataItems;
    }

    @NonNull
    @Override
    public DataItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_data, parent, false);

        return new DataItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataItemViewHolder holder, int position) {
        DataItem item = dataItems.get(position);

        holder.textViewTitle.setText(item.getTitle());
        holder.textViewValue.setText(item.getValue());
    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }

    public void updateData(List<DataItem> newDataItems) {
        dataItems.clear();
        dataItems.addAll(newDataItems);
        notifyDataSetChanged();
    }

    public static class DataItemViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle;
        TextView textViewValue;

        public DataItemViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewValue = itemView.findViewById(R.id.textViewValue);
        }
    }
}