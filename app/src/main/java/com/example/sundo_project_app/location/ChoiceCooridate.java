package com.example.sundo_project_app.location;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.sundo_project_app.R;

public class ChoiceCooridate extends DialogFragment {

    private static final String ARG_PROJECT_ID = "project_id";

    // 새로운 인스턴스를 생성하는 메서드
    public static ChoiceCooridate newInstance(String projectId) {
        ChoiceCooridate fragment = new ChoiceCooridate();
        Bundle args = new Bundle();
        args.putString(ARG_PROJECT_ID, projectId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choice_coordinate, container, false);

        // arguments에서 projectId를 가져옵니다.
        Bundle args = getArguments();
        if (args != null) {
            String projectId = args.getString(ARG_PROJECT_ID);

            // 'DD 선택' 버튼 클릭 리스너
            Button btnDdFormat = view.findViewById(R.id.btn_dd_format);
            btnDdFormat.setOnClickListener(v -> {
                dismiss(); // 모달을 닫고
                Intent intent = new Intent(getActivity(), DdActivity.class);
                intent.putExtra("project_id", projectId); // projectId 추가
                Log.d("projectId",projectId);
                startActivity(intent); // DdActivity 시작
            });

            // 'DMS' 버튼 클릭 리스너
            Button btnDmsFormat = view.findViewById(R.id.btn_dms_format);
            btnDmsFormat.setOnClickListener(v -> {
                dismiss(); // 모달을 닫고
                Intent intent = new Intent(getActivity(), DmsActivity.class);
                intent.putExtra("project_id", projectId); // projectId 추가
                Log.d("projectId",projectId);
                startActivity(intent); // DmsActivity 시작
            });
        }

        return view;
    }
}
