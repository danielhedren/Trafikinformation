package com.danielhedren.trafikinformation;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

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
        final Deviation deviation = dataset.get(i);
        viewHolder.messageText.setText(deviation.getMessage());
        viewHolder.severityText.setText(deviation.getSeverityText());
        viewHolder.roadText.setText(deviation.getRoadNumber());

        MainActivity activity = (MainActivity) viewHolder.distanceText.getContext();
        viewHolder.distanceText.setText(String.format("%skm", String.format(Locale.getDefault(), "%.1f", activity.getLocation().distanceTo(deviation.getLocation()) / 1000)));

        viewHolder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DeviationMap.class);
            intent.putExtra("latitude", deviation.getLocation().getLatitude());
            intent.putExtra("longitude", deviation.getLocation().getLongitude());
            intent.putExtra("messageType", deviation.getMessageType());
            intent.putExtra("message", deviation.getTag("Message"));
            intent.putExtra("locationDescriptor", deviation.getTag("LocationDescriptor"));
            intent.putExtra("severityText", deviation.getTag("SeverityText"));
            intent.putExtra("roadNumber", deviation.getTag("RoadNumber"));
            v.getContext().startActivity(intent);
        });
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
