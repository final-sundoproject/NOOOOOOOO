package com.example.sundo_project_app.location;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sundo_project_app.R;
import com.example.sundo_project_app.evaluation.EvaluationActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DdActivity extends AppCompatActivity {

    private static final double MIN_LATITUDE = -90.0;
    private static final double MAX_LATITUDE = 90.0;
    private static final double MIN_LONGITUDE = -180.0;
    private static final double MAX_LONGITUDE = 180.0;
    private String projectId;
    private String locationId;
    private Serializable currentProject;
    private String registerName;

    private static final String TAG = "DdActivity";
    private static final String SERVER_URL = "http://10.0.2.2:8000/location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showDialog();
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_dd_input);
        dialog.setCancelable(false); // 다이얼로그 외부를 클릭해도 닫히지 않도록 설정
        dialog.setCanceledOnTouchOutside(false); // 다이얼로그 외부를 클릭해도 닫히지 않도록 설정

        // Initialize views
        EditText etLatitude = dialog.findViewById(R.id.et_latitude);
        EditText etLongitude = dialog.findViewById(R.id.et_longitude);
        Button btnSubmit = dialog.findViewById(R.id.btn_submit);
        TextView btnClose = dialog.findViewById(R.id.btn_close);

        // Set up close button
        btnClose.setOnClickListener(v -> dialog.dismiss());

        // Handle Intent data
        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra("latitude", 0);
        double longitude = intent.getDoubleExtra("longitude", 0);
        projectId = intent.getStringExtra("project_id");
        currentProject = intent.getSerializableExtra("currentProject");
        registerName = intent.getStringExtra("registerName");

        Log.d(TAG, "currentProject: " + currentProject);
        Log.d(TAG, "registerName: " + registerName);

        etLatitude.setText(String.valueOf(latitude));
        etLongitude.setText(String.valueOf(longitude));

        btnSubmit.setOnClickListener(v -> handleSubmit(etLatitude, etLongitude));

        dialog.show();
    }

    private void handleSubmit(EditText etLatitude, EditText etLongitude) {
        try {
            double latitude = Double.parseDouble(etLatitude.getText().toString());
            double longitude = Double.parseDouble(etLongitude.getText().toString());

            if (latitude < MIN_LATITUDE || latitude > MAX_LATITUDE) {
                showToast("Latitude must be between " + MIN_LATITUDE + " and " + MAX_LATITUDE + ".");
                return;
            }

            if (longitude < MIN_LONGITUDE || longitude > MAX_LONGITUDE) {
                showToast("Longitude must be between " + MIN_LONGITUDE + " and " + MAX_LONGITUDE + ".");
                return;
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("latitude", latitude);
            jsonObject.put("longitude", longitude);
            jsonObject.put("registerName", registerName);

            if (projectId != null) {
                jsonObject.put("projectId", projectId);
            }

            sendCoordinates(jsonObject.toString());

        } catch (NumberFormatException e) {
            showToast("Please enter valid numbers.");
        } catch (Exception e) {
            Log.e(TAG, "Error: ", e);
            showToast("An error occurred: " + e.getMessage());
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
                URL url = new URL(SERVER_URL + (projectId != null ? "/" + projectId : ""));
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setDoOutput(true);

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
                    String response = responseBuilder.toString();
                    Log.d(TAG, "Response from server: " + response);

                    if ("DUPLICATE".equals(response)) {
                        result = "The coordinates you entered already exist.";
                    } else {
                        result = "Coordinates successfully submitted.";
                        locationId = response;
                    }
                } else {
                    result = "Server error occurred. Response code: " + responseCode;
                }

            } catch (Exception e) {
                Log.e(TAG, "Error: ", e);
                result = "An error occurred while sending data: " + e.getMessage();
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

            final String finalResult = result;
            handler.post(() -> {
                showToast(finalResult);
                if (locationId != null) {
                    Intent intent = new Intent(DdActivity.this, GeneratorActivity.class);
                    intent.putExtra("locationId", locationId);
                    intent.putExtra("currentProject", currentProject);
                    intent.putExtra("registerName", registerName);
                    startActivity(intent);
                }
                finish();
            });
        });
    }

    private void showToast(String message) {
        Toast.makeText(DdActivity.this, message, Toast.LENGTH_LONG).show();
    }
}
