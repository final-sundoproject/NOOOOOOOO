package com.example.sundo_project_app.regulatedArea;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.sundo_project_app.R;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Callback;
import retrofit2.Response;

public class RegulatedArea extends AppCompatActivity {

    private static final String BASE_URL = "https://apis.data.go.kr/1192000/";
    private static final String SERVICE_KEY = "xyigcn2H+16RENHs6SNbyOXjPjW0t0Tastu/ePEl3PW6jMKcyrxrFErPO4Rzc+GgV2G44DvWYE/HGIeUhEIxCw==";
    private static final int PAGE_NO = 1;
    private static final int NUM_OF_ROWS = 100;

    private TextView TextViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtiy_regulate_area);

        TextViewResult = findViewById(R.id.textViewInfo);

        // Retrofit 초기화
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // API 인터페이스 생성
        regulateApi api = retrofit.create(regulateApi.class);

        // API 호출
        Call<ResponseBody> call = api.getMarineProtectArea(SERVICE_KEY, PAGE_NO, NUM_OF_ROWS);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // 응답 본문을 문자열로 변환
                        String result = response.body().string();
                        TextViewResult.setText(result);
                    } catch (IOException e) {
                        Log.e("RegulatedArea", "Error parsing response", e);
                        TextViewResult.setText("Error parsing response");
                    }
                } else {
                    Log.e("RegulatedArea", "Request failed with code: " + response.code());
                    TextViewResult.setText("Request failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("RegulatedArea", "Request failed", t);
                TextViewResult.setText("Request failed: " + t.getMessage());
            }
        });
    }
}
