package com.danielhedren.trafikinformation;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;

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
        viewHolder.messageText.setText(dataset.get(i).getMessage());
        viewHolder.severityText.setText(dataset.get(i).getSeverityText());
        viewHolder.roadText.setText(dataset.get(i).getRoadNumber());

        MainActivity activity = (MainActivity) viewHolder.distanceText.getContext();
        viewHolder.distanceText.setText(String.format("%.1f", activity.getLocation().distanceTo(dataset.get(i).getLocation()) / 1000) + "km");
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView messageText;
        public TextView severityText;
        public TextView roadText;
        public TextView distanceText;

        public ViewHolder(View v) {
            super(v);
            messageText = v.findViewById(R.id.messageText);
            severityText = v.findViewById(R.id.severityText);
            roadText = v.findViewById(R.id.roadText);
            distanceText = v.findViewById(R.id.distanceText);
        }
    }
}
