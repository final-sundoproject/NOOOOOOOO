package com.example.sundo_project_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import org.json.JSONArray;
import org.json.JSONObject;

public class EvaluationFindAllActivity extends AppCompatActivity {

    private Button btnShowDialog;
    private Button btnShowList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        btnShowDialog = findViewById(R.id.btnShowDialog);
        btnShowList = findViewById(R.id.enteredPoint);

        btnShowDialog.setOnClickListener(v -> {
            Log.d("EvaluationFindAllActivity", "평가리스트 버튼 클릭됨");
            fetchDataAndShowDialog();
        });

        btnShowList.setOnClickListener(v -> {
            Log.d("btnShowList", "평가입력 버튼 클릭됨");
            Intent intent = new Intent(EvaluationFindAllActivity.this, EvaluationActivity.class);
            startActivity(intent);
        });
    }

    private void fetchDataAndShowDialog() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<ResponseBody> call = apiService.getAllEvaluations();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("EvaluationFindAllActivity", "서버 응답 받음");
                if (response.isSuccessful()) {
                    try {
                        String jsonResponse = response.body().string();
                        JSONArray jsonArray = new JSONArray(jsonResponse);
                        StringBuilder result = new StringBuilder();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String title = jsonObject.getString("title");
                            String registrantName = jsonObject.getString("registrantName");
                            int windVolume = jsonObject.getInt("windVolume");
                            int noiseLevel = jsonObject.getInt("noiseLevel");
                            int scenery = jsonObject.getInt("scenery");
                            int waterDepth = jsonObject.getInt("waterDepth");

                            result.append("Title: ").append(title)
                                    .append("\nRegistrant: ").append(registrantName)
                                    .append("\nWind Volume: ").append(windVolume)
                                    .append("\nNoise Level: ").append(noiseLevel)
                                    .append("\nScenery: ").append(scenery)
                                    .append("\nWater Depth: ").append(waterDepth)
                                    .append("\n\n");
                        }

                        showDataInDialog(result.toString());

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("EvaluationFindAllActivity", "데이터 파싱 오류", e);
                        showErrorDialog("데이터 파싱 오류");
                    }
                } else {
                    Log.e("EvaluationFindAllActivity", "요청 실패");
                    showErrorDialog("요청 실패");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("EvaluationFindAllActivity", "요청 실패", t);
                showErrorDialog("요청 실패: " + t.getMessage());
            }
        });
    }

    private void showDataInDialog(String data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("점수 목록")
                .setMessage(data)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("오류")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
