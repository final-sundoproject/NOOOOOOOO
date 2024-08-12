package com.example.sundo_project_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class EvaluationDetailActivity extends AppCompatActivity {

    private ImageView arImageView;
    private TextView titleTextView;
    private TextView registrantNameTextView;
    private TextView averageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.evaluation_detail);

        arImageView = findViewById(R.id.arImageView);
        titleTextView = findViewById(R.id.textViewTitle);
        registrantNameTextView = findViewById(R.id.textViewRegistrantName);
        averageTextView = findViewById(R.id.textViewAverage);

        Intent intent = getIntent();
        Evaluation evaluation = (Evaluation) intent.getSerializableExtra("evaluation");

        if (evaluation != null) {
            Glide.with(this)
                    .load(evaluation.getArImage())
                    .into(arImageView);
            titleTextView.setText("평가명: " + evaluation.getTitle());
            registrantNameTextView.setText("등록자명: " + evaluation.getRegistrantName());
            averageTextView.setText("평균 점수: " + evaluation.getAverageRating());
        }
    }
}
