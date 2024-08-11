// EvaluationDialogFragment.java
package com.example.sundo_project_app;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class EvaluationDialogFragment extends DialogFragment {

    private List<Evaluation> evaluationList = new ArrayList<>();
    private EvaluationAdapter evaluationAdapter;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.activity_evaluation_find_all, null);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        evaluationAdapter = new EvaluationAdapter(evaluationList);
        recyclerView.setAdapter(evaluationAdapter);

        fetchDataAndUpdateRecyclerView();

        return new AlertDialog.Builder(getActivity())
                .setTitle("평가리스트")
                .setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .create();
    }

    private void fetchDataAndUpdateRecyclerView() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<ResponseBody> call = apiService.getAllEvaluations();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String jsonResponse = response.body().string();
                        JSONArray jsonArray = new JSONArray(jsonResponse);
                        evaluationList.clear(); // Clear old data

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Evaluation evaluation = new Evaluation();
                            evaluation.setTitle(jsonObject.getString("평가명"));
                            evaluation.setRegistrantName(jsonObject.getString("등록자명"));
                            evaluation.setWindVolume(jsonObject.getInt("풍속"));
                            evaluation.setNoiseLevel(jsonObject.getInt("소음"));
                            evaluation.setScenery(jsonObject.getInt("가시성"));
                            evaluation.setWaterDepth(jsonObject.getInt("수심"));

                            evaluationList.add(evaluation);
                        }

                        evaluationAdapter.notifyDataSetChanged(); // Notify adapter about data changes

                    } catch (Exception e) {
                        e.printStackTrace();
                        // Handle the exception
                    }
                } else {
                    // Handle unsuccessful response
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
            }
        });
    }
}
