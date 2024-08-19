package com.example.sundo_project_app.regulatedArea;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface regulateApi {

    @GET("MarineProtectionAreaInfoService/MarineProtectionAreaInfo")
    Call<ResponseBody> getMarineProtectArea(
            @Query("serviceKey") String serviceKey,
            @Query("pageNo") int pageNo,
            @Query("numOfRows") int numOfRows
    );
}
