package com.example.sundo_project_app.location;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.sundo_project_app.R;

public class ChoiceCooridate extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choice_coordinate, container, false);

        // 'DD 선택' 버튼 클릭 리스너
        Button btnDdFormat = view.findViewById(R.id.btn_dd_format);
        btnDdFormat.setOnClickListener(v -> {
            dismiss(); // 모달을 닫고
            startActivity(new Intent(getActivity(), DdActivity.class)); // DdActivity 시작
        });

        // 'DMS' 버튼 클릭 리스너
        Button btnDmsFormat = view.findViewById(R.id.btn_dms_format);
        btnDmsFormat.setOnClickListener(v -> {
            dismiss(); // 모달을 닫고
            startActivity(new Intent(getActivity(), DmsActivity.class)); // DmsActivity 시작
        });

        return view;
    }
}
