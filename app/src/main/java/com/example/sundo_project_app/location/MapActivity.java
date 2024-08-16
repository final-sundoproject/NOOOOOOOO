package com.example.sundo_project_app.location;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.sundo_project_app.R;
import com.example.sundo_project_app.evaluation.EvaluationActivity;
import com.example.sundo_project_app.evaluation.EvaluationDialogFragment;
import com.example.sundo_project_app.project.model.Project;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.overlay.Marker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity {

    private NaverMap naverMap;
    private boolean isMarkerEnabled = false; // 마커 추가 모드 상태
    private Button coordinateSelectButton;
    private Button resetButton; // 초기화 버튼
    private Button gpsButton; // GPS 버튼
    private List<Marker> markers; // 마커 리스트

    private FusedLocationProviderClient fusedLocationClient;
    private Handler handler; // Handler를 사용하여 주기적으로 작업 수행
    private Runnable locationUpdateRunnable; // 위치 업데이트를 위한 Runnable
    private Button btnShowDialog;
    private Button btnShowList;
    private String projectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        Intent projectIntent = getIntent();
        Bundle extras = projectIntent.getExtras();

        for (String key : extras.keySet()) {
            Object value = extras.get(key);
            Log.d("IntentExtras", "Key: " + key + ", Value: " + value);
        }

        Project currentProject = (Project) extras.getSerializable("project");
        Log.d("projectId", "projectId: " + currentProject.getProjectId().toString());
        if (currentProject != null) {
            projectId = currentProject.getProjectId().toString();
        }

        TextView projectNameTextView = findViewById(R.id.textBox3);
        if (currentProject != null) {
            String currentProjectName = currentProject.getProjectName();
            projectNameTextView.setText(currentProjectName);
        }


        // 마커 리스트 초기화
        markers = new ArrayList<>();

        // 위치 서비스 클라이언트 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        handler = new Handler(); // Handler 초기화

        // MapFragment 설정
        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this::onMapReady);
        }

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

        // 좌표 입력 버튼 클릭 리스너
        findViewById(R.id.coordinateInput).setOnClickListener(v -> {
            if (projectId != null) {
                ChoiceCooridate choiceCoordinateDialog = ChoiceCooridate.newInstance(projectId);
                choiceCoordinateDialog.show(getSupportFragmentManager(), "choiceCoordinateDialog");
            } else {
                Toast.makeText(MapActivity.this, "Project ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // AR 확인 버튼 클릭 리스너
        findViewById(R.id.arCheck).setOnClickListener(v -> {
            Intent intent = new Intent(MapActivity.this, GeneratorActivity.class);
            startActivity(intent);
        });

        // 좌표 선택 버튼 초기화
        coordinateSelectButton = findViewById(R.id.coordinateSelect);
        coordinateSelectButton.setOnClickListener(v -> toggleMarkerMode());

        // 초기화 버튼 초기화
        resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(v -> resetToInitialState()); // 초기 상태로 리셋하는 메서드 호출

        // GPS 버튼 클릭 리스너
        gpsButton = findViewById(R.id.gps);
        gpsButton.setOnClickListener(v -> getCurrentLocation());
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

    private void showEvaluationDialog() {
        EvaluationDialogFragment dialog = new EvaluationDialogFragment();
        dialog.show(getSupportFragmentManager(), "EvaluationDialog");
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

    // 현재 위치 가져오기
    private void getCurrentLocation() {

        if (!hasLocationPermissions()) {
            requestLocationPermissions();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        updateCurrentLocationOnMap(location);
                    } else {
                        Toast.makeText(MapActivity.this, "위치를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MapActivity.this, "위치 검색에 실패했습니다: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private boolean hasLocationPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
    }

    private void updateCurrentLocationOnMap(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Marker currentLocationMarker = new Marker();
        currentLocationMarker.setPosition(new LatLng(latitude, longitude));
        currentLocationMarker.setMap(naverMap);
        naverMap.setCameraPosition(new CameraPosition(new LatLng(latitude, longitude), 15));
        Toast.makeText(MapActivity.this, "현재 위치: " + latitude + ", " + longitude, Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onStop() {
        super.onStop();
        // 위치 업데이트 중지
        handler.removeCallbacks(locationUpdateRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 위치 업데이트 재개
        // startLocationUpdates(); // GPS 버튼 클릭 시 위치 업데이트
    }

    // 권한 요청 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 승인되면 현재 위치 가져오기
                getCurrentLocation();
            } else {
                Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
