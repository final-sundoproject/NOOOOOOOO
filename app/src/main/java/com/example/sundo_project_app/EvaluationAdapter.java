package com.example.sundo_project_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class EvaluationAdapter extends RecyclerView.Adapter<EvaluationAdapter.EvaluationViewHolder> {

    private final List<Evaluation> evaluationList;

    public EvaluationAdapter(List<Evaluation> evaluationList) {
        this.evaluationList = evaluationList;
    }

    @NonNull
    @Override
    public EvaluationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_evaluation, parent, false);
        return new EvaluationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EvaluationViewHolder holder, int position) {
        Evaluation evaluation = evaluationList.get(position);
        holder.titleTextView.setText("Title: " + evaluation.getTitle());
        holder.registrantNameTextView.setText("Registrant: " + evaluation.getRegistrantName());
        holder.windVolumeTextView.setText("Wind Volume: " + evaluation.getWindVolume());
        holder.noiseLevelTextView.setText("Noise Level: " + evaluation.getNoiseLevel());
        holder.sceneryTextView.setText("Scenery: " + evaluation.getScenery());
        holder.waterDepthTextView.setText("Water Depth: " + evaluation.getWaterDepth());
    }

    @Override
    public int getItemCount() {
        return evaluationList.size();
    }

    static class EvaluationViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView registrantNameTextView;
        TextView windVolumeTextView;
        TextView noiseLevelTextView;
        TextView sceneryTextView;
        TextView waterDepthTextView;

        public EvaluationViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textViewTitle);
            registrantNameTextView = itemView.findViewById(R.id.textViewRegistrantName);
            windVolumeTextView = itemView.findViewById(R.id.textViewWindVolume);
            noiseLevelTextView = itemView.findViewById(R.id.textViewNoiseLevel);
            sceneryTextView = itemView.findViewById(R.id.textViewScenery);
            waterDepthTextView = itemView.findViewById(R.id.textViewWaterDepth);
        }
    }
}
