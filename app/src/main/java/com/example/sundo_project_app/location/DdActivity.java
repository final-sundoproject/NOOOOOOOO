package com.example.sundo_project_app.location;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sundo_project_app.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DdActivity extends AppCompatActivity {

    private EditText etlatitude;
    private EditText etlongitude;
    private Button btnSubmit;
    private String locationId;

    private static final String TAG = "DdActivity";
    private static final String SERVER_URL = "http://10.0.2.2:8000/location"; // 변경할 서버 URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_dd_input);

        etlatitude = findViewById(R.id.et_latitude);
        etlongitude = findViewById(R.id.et_longitude);
        btnSubmit = findViewById(R.id.btn_submit);

        // Intent에서 위도와 경도를 가져와 EditText에 설정
        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra("latitude", 0);
        double longitude = intent.getDoubleExtra("longitude", 0);

        etlatitude.setText(String.valueOf(latitude));
        etlongitude.setText(String.valueOf(longitude));

        btnSubmit.setOnClickListener(v -> handleSubmit());
    }

    private void handleSubmit() {
        try {
            double latitude = Double.parseDouble(etlatitude.getText().toString());
            double longitude = Double.parseDouble(etlongitude.getText().toString());

            // 서버로 전송할 데이터 생성
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("latitude", latitude);
            jsonObject.put("longitude", longitude);

            // 서버로 데이터 전송
            sendCoordinates(jsonObject.toString());

        } catch (NumberFormatException e) {
            Toast.makeText(this, "숫자를 올바르게 입력해주세요.", Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendCoordinates(String jsonData) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        Log.d(TAG, "jsonData: " + jsonData);

        executor.execute(() -> {
            String result = null;
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(SERVER_URL);
                connection = (HttpURLConnection) url.openConnection();
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
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder responseBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseBuilder.append(line);
                    }
                    locationId = responseBuilder.toString();
                    result = "좌표가 성공적으로 전송되었습니다.";
                } else {
                    result = "서버 오류가 발생했습니다. 응답 코드: " + responseCode;
                }

            } catch (Exception e) {
                e.printStackTrace();
                result = "전송 중 오류가 발생했습니다: " + e.getMessage();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        Log.e(TAG, "Error closing reader: " + e.getMessage());
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }

            String finalResult = result;
            handler.post(() -> {
                Toast.makeText(DdActivity.this, finalResult, Toast.LENGTH_LONG).show();
                if (locationId != null) {
                    Intent intent = new Intent(DdActivity.this, GeneratorActivity.class);
                    intent.putExtra("locationId", locationId);
                    startActivity(intent);
                    Log.d(TAG, "locationId: " + locationId);
                }
            });
        });
    }
}
