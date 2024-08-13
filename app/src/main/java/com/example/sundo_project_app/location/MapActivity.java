package com.example.sundo_project_app.location;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sundo_project_app.R;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity {

    private NaverMap naverMap;
    private boolean isMarkerEnabled = false; // 마커 추가 모드 상태
    private Button coordinateSelectButton;
    private Button resetButton; // 뒤로 버튼
    private List<Marker> markers; // 마커 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        // 마커 리스트 초기화
        markers = new ArrayList<>();

        // MapFragment 설정
        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this::onMapReady);
        }

        // 좌표 입력 버튼 클릭 리스너
        findViewById(R.id.coordinateInput).setOnClickListener(v -> {
            Intent intent = new Intent(MapActivity.this, DdActivity.class);
            startActivity(intent);
        });

        // AR 확인 버튼 클릭 리스너
        findViewById(R.id.arCheck).setOnClickListener(v -> {
            Intent intent = new Intent(MapActivity.this, GeneratorActivity.class);
            startActivity(intent);
        });

        // 좌표 선택 버튼 초기화
        coordinateSelectButton = findViewById(R.id.coordinateSelect);
        coordinateSelectButton.setOnClickListener(v -> toggleMarkerMode());

        // 뒤로 버튼 초기화
        resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(v -> resetToInitialState()); // 초기 상태로 리셋하는 메서드 호출
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

                // 마커 리스트에 추가
                markers.add(marker);

                // 마커 클릭 이벤트 리스너 설정
                marker.setOnClickListener(overlay -> {
                    // 마커가 클릭되었을 때 위도와 경도 가져오기
                    double markerLatitude = latLng.latitude;
                    double markerLongitude = latLng.longitude;

                    // DdActivity로 좌표 전달
                    Intent intent = new Intent(MapActivity.this, DdActivity.class);
                    intent.putExtra("latitude", markerLatitude);
                    intent.putExtra("longitude", markerLongitude);
                    startActivity(intent);

                    return true; // 클릭 이벤트 처리 완료
                });

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

    // 초기 상태로 리셋
    private void resetToInitialState() {
        clearMarkers(); // 모든 마커 제거
        isMarkerEnabled = false; // 마커 추가 모드 비활성화
        coordinateSelectButton.setText("좌표선택"); // 좌표 선택 버튼 텍스트 초기화
        Toast.makeText(this, "초기 화면으로 되돌아갔습니다.", Toast.LENGTH_SHORT).show();
    }

    // 모든 마커 제거
    private void clearMarkers() {
        for (Marker marker : markers) {
            marker.setMap(null); // 마커를 지도에서 제거
        }
        markers.clear(); // 리스트 초기화
        Toast.makeText(this, "모든 마커가 제거되었습니다.", Toast.LENGTH_SHORT).show();
    }
}
