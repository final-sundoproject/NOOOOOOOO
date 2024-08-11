package com.example.sundo_project_app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView; // Import NestedScrollView
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EvaluationActivity extends AppCompatActivity {

    private SeekBar seekBar1, seekBar2, seekBar3, seekBar4;
    private Button btnSubmit;
    private TextView textViewName, textViewObserver;
    private NestedScrollView nestedScrollView;

    private Log log;

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
            jsonObject.put("title", textViewName.getText());
            jsonObject.put("registrantName", textViewObserver.getText());
            jsonObject.put("arImage", "image_url.jpg");
            jsonObject.put("windVolume", score1);
            jsonObject.put("noiseLevel", score2);
            jsonObject.put("scenery", score3);
            jsonObject.put("waterDepth", score4);

            String jsonString = jsonObject.toString();
            sendCoordinates(jsonString);


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


    private void sendCoordinates(String jsonData) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            String result = null;
            try {
                URL url = new URL("http://10.0.2.2:8000/evaluation");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setDoOutput(true);

                // 데이터 전송
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonData.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    result = "좌표가 성공적으로 전송되었습니다.";
                } else {
                    result = "서버 오류가 발생했습니다. 응답 코드: " + responseCode;
                }

            } catch (Exception e) {
                e.printStackTrace();
                result = "전송 중 오류가 발생했습니다: " + e.getMessage(); // 오류 메시지 포함
            }

            // 메인 스레드에서 UI 업데이트
            String finalResult = result;
            handler.post(() -> Toast.makeText(EvaluationActivity.this, finalResult, Toast.LENGTH_LONG).show());
        });
    }
}
