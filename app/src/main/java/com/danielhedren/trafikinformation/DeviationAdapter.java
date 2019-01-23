package com.danielhedren.trafikinformation;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class DeviationAdapter extends RecyclerView.Adapter<DeviationAdapter.ViewHolder> {
    private ArrayList<Deviation> dataset;

    public DeviationAdapter(ArrayList<Deviation> dataset) {
        this.dataset = dataset;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_layout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.textView.setText(dataset.get(i).getMessage());
        viewHolder.textView2.setText(dataset.get(i).getSeverityText());
        viewHolder.textView3.setText(dataset.get(i).getRoadNumber());
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public TextView textView2;
        public TextView textView3;

        public ViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.textView);
            textView2 = v.findViewById(R.id.textView2);
            textView3 = v.findViewById(R.id.textView3);
        }
    }
}
