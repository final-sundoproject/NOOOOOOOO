package com.example.sundo_project_app.location;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sundo_project_app.R;
import com.example.sundo_project_app.evaluation.EvaluationActivity;
import com.example.sundo_project_app.evaluation.EvaluationDialogFragment;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.overlay.Marker;

public class MapActivity extends AppCompatActivity {

    private NaverMap naverMap;
    private boolean isMarkerEnabled = false; // 마커 추가 모드 상태
    private Button coordinateSelectButton;
    private Button btnShowDialog;
    private Button btnShowList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        // MapFragment 설정
        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this::onMapReady);
        }

        // 좌표 입력 버튼 클릭 리스너
        findViewById(R.id.coordinateInput).setOnClickListener(v -> {
            ChoiceCooridate choiceCoordinateDialog = new ChoiceCooridate();
            choiceCoordinateDialog.show(getSupportFragmentManager(), "choiceCoordinateDialog");
        });

        // AR 확인 버튼 클릭 리스너
        findViewById(R.id.arCheck).setOnClickListener(v -> {
            Intent intent = new Intent(MapActivity.this, GeneratorActivity.class);
            startActivity(intent);
        });

        // 좌표 선택 버튼 초기화
        coordinateSelectButton = findViewById(R.id.coordinateSelect);
        coordinateSelectButton.setOnClickListener(v -> toggleMarkerMode());

        btnShowDialog = findViewById(R.id.btnShowDialog);
        btnShowList = findViewById(R.id.enteredPoint);

        btnShowDialog.setOnClickListener(v -> {
            Log.d("EvaluationFindAllActivity", "평가리스트 버튼 클릭됨");
            showEvaluationDialog();
        });

        btnShowList.setOnClickListener(v -> {
            Log.d("btnShowList", "평가입력 버튼 클릭됨");
            Intent intent = new Intent(MapActivity.this, EvaluationActivity.class);
            startActivity(intent);
        });
    }

    private void showEvaluationDialog() {
        EvaluationDialogFragment dialog = new EvaluationDialogFragment();
        dialog.show(getSupportFragmentManager(), "EvaluationDialog");
    }

    private void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;

        // 지도 클릭 이벤트 리스너 설정
        naverMap.setOnMapClickListener((point, latLng) -> {
            if (isMarkerEnabled) {
                // 클릭한 위치에 마커 추가
                Marker marker = new Marker();
                marker.setPosition(latLng);
                marker.setMap(naverMap);

                // 마커가 추가된 위치에 토스트 메시지 표시
                Toast.makeText(MapActivity.this, "Clicked Location: " + latLng.latitude + ", " + latLng.longitude, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleMarkerMode() {
        isMarkerEnabled = !isMarkerEnabled; // 상태 토글
        if (isMarkerEnabled) {
            coordinateSelectButton.setText("선택해제"); // 버튼 텍스트 변경
            Toast.makeText(this, "마커 추가 모드 활성화", Toast.LENGTH_SHORT).show();
        } else {
            coordinateSelectButton.setText("좌표선택"); // 원래 텍스트로 변경
            Toast.makeText(this, "마커 추가 모드 비활성화", Toast.LENGTH_SHORT).show();
        }
    }
}
