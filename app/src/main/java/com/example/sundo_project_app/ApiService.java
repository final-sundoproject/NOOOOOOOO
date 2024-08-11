package com.example.sundo_project_app;

import retrofit2.Call;
import okhttp3.ResponseBody;
import retrofit2.http.GET;

public interface ApiService {
    @GET("evaluation/foundAll")
    Call<ResponseBody> getAllEvaluations();
}
