package com.example.sundo_project_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.BreakIterator;
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

        Glide.with(holder.itemView.getContext())
                .load(evaluation.getArImage())
                .into(holder.arImage);

        holder.titleTextView.setText("평가명: " + evaluation.getTitle());
        holder.registrantNameTextView.setText("등록자명: " + evaluation.getRegistrantName());
        holder.averageRatingTextView.setText("평점: " + evaluation.getAverageRating());
    }

    @Override
    public int getItemCount() {
        return evaluationList.size();
    }

    static class EvaluationViewHolder extends RecyclerView.ViewHolder {
        ImageView arImage;
        TextView titleTextView;
        TextView registrantNameTextView;
        TextView averageRatingTextView;



        public EvaluationViewHolder(@NonNull View itemView) {
            super(itemView);
            arImage = itemView.findViewById(R.id.arImage);
            titleTextView = itemView.findViewById(R.id.textViewTitle);
            registrantNameTextView = itemView.findViewById(R.id.textViewRegistrantName);
            averageRatingTextView = itemView.findViewById(R.id.textViewAverage);
        }
    }
}
