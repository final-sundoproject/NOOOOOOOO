package com.example.sundo_project_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView; // Import NestedScrollView
import org.json.JSONException;
import org.json.JSONObject;


public class EvaluationActivity extends AppCompatActivity {

    private SeekBar seekBar1, seekBar2, seekBar3, seekBar4;
    private Button btnSubmit;
    private TextView textViewName, textViewObserver, textViewDate;
    private NestedScrollView nestedScrollView; // Declare NestedScrollView

    private String observerName = "김재엽";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.evaluation);

        seekBar1 = findViewById(R.id.seekBar1);
        seekBar2 = findViewById(R.id.seekBar2);
        seekBar3 = findViewById(R.id.seekBar3);
        seekBar4 = findViewById(R.id.seekBar4);
        btnSubmit = findViewById(R.id.btn_submit);
        textViewName = findViewById(R.id.textViewName);
        textViewObserver = findViewById(R.id.textViewObserver);
        textViewDate = findViewById(R.id.textViewDate);
        nestedScrollView = findViewById(R.id.nestedScrollView);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitEvaluation();
            }
        });
    }

    private void submitEvaluation() {
        int score1 = seekBar1.getProgress();
        int score2 = seekBar2.getProgress();
        int score3 = seekBar3.getProgress();
        int score4 = seekBar4.getProgress();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title", textViewName);
            jsonObject.put("registrantName", textViewObserver);
            jsonObject.put("arImage", textViewObserver);
            jsonObject.put("windVolume", "image_url.jpg");
            jsonObject.put("noiseLevel", score2);
            jsonObject.put("scenery", score3);
            jsonObject.put("waterDepth", score4);

            String jsonString = jsonObject.toString();

            if (nestedScrollView != null) {
                scrollToView(R.id.seekBar4);
            }
            resetUI();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void scrollToView(int viewId) {
        View view = findViewById(viewId);
        if (view != null && nestedScrollView != null) {
            nestedScrollView.post(new Runnable() {
                @Override
                public void run() {
                    nestedScrollView.smoothScrollTo(0, view.getTop());
                }
            });
        }
    }

    private void resetUI() {
        seekBar1.setProgress(0);
        seekBar2.setProgress(0);
        seekBar3.setProgress(0);
        seekBar4.setProgress(0);
    }
}
