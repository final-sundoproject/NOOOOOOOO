package com.example.sundo_project_app.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.sundo_project_app.R;
import com.example.sundo_project_app.evaluation.EvaluationActivity;
import com.example.sundo_project_app.evaluation.EvaluationDialogFragment;
import com.example.sundo_project_app.project.model.Project;
import com.example.sundo_project_app.regulatedArea.RegulatedArea;
import com.example.sundo_project_app.utill.KoreanInputFilter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity {

    private NaverMap naverMap;
    private boolean isMarkerEnabled = false; // 마커 추가 모드 상태
    private Button coordinateSelectButton;
    private Button resetButton; // 초기화 버튼
    private Button gpsButton; // GPS 버튼
    private List<Marker> markers; // 사용자가 추가한 마커 리스트
    private List<Marker> gpsMarkers; // GPS로 추가한 마커 리스트
    private boolean isFollowingLocation = false; // 사용자에 의해 화면이 위치를 따라갈지 결정

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Handler handler; // Handler를 사용하여 주기적으로 작업 수행
    private Runnable locationUpdateRunnable; // 위치 업데이트를 주기적으로 수행하는 Runnable
    private static final long LOCATION_UPDATE_INTERVAL = 10000; // 10초 간격
    private static final long LOCATION_UPDATE_FASTEST_INTERVAL = 5000; // 5초 간격
    private Button btnShowDialog;
    private Button btnShowList;
    private Button btnRedulated;
    private String projectId;

    private Project currentProject;
    private String registerName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        showEvaluatorNameDialog();
        initializeViews();
        initializeLocationServices();
        retrieveProjectData();
        setupButtonListeners();
        setupMapFragment();
    }

    private void initializeViews() {
        coordinateSelectButton = findViewById(R.id.coordinateSelect);
        resetButton = findViewById(R.id.resetButton);
        gpsButton = findViewById(R.id.gps);
        btnShowDialog = findViewById(R.id.btnShowDialog);
        btnShowList = findViewById(R.id.enteredPoint);
        btnRedulated = findViewById(R.id.redulated);
        markers = new ArrayList<>();
        gpsMarkers = new ArrayList<>();

    }

    private void initializeLocationServices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        handler = new Handler();

        // LocationCallback 정의
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
                    // 위치 업데이트를 지도에 반영
                    updateCurrentLocationOnMap(location);
                }
            }
        };

        // 위치 업데이트 Runnable 정의
        locationUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                if (isFollowingLocation) {
                    getCurrentLocation();
                }
                handler.postDelayed(this, LOCATION_UPDATE_INTERVAL); // 주기적으로 실행
            }
        };

        startLocationUpdates(); // 위치 업데이트 시작
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        if (!hasLocationPermissions()) {
            requestLocationPermissions();
            return;
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(LOCATION_UPDATE_INTERVAL); // 10초마다 위치 요청
        locationRequest.setFastestInterval(LOCATION_UPDATE_FASTEST_INTERVAL); // 가장 빠른 위치 요청 주기
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, handler.getLooper());

        // 위치 업데이트 Runnable 시작
        handler.post(locationUpdateRunnable);
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);

        // 위치 업데이트 Runnable 중지
        handler.removeCallbacks(locationUpdateRunnable);
    }

    private void retrieveProjectData() {
        Intent projectIntent = getIntent();
        Bundle extras = projectIntent.getExtras();

        for (String key : extras.keySet()) {
            Object value = extras.get(key);
            Log.d("IntentExtras", "Key: " + key + ", Value: " + value);
        }

        currentProject = (Project) extras.getSerializable("project");
        if (currentProject != null) {
            projectId = currentProject.getProjectId().toString();
            TextView projectNameTextView = findViewById(R.id.textBox3);
            projectNameTextView.setText(currentProject.getProjectName());
        }
    }

    private void setupButtonListeners() {
        btnRedulated.setOnClickListener(v ->{
            Log.d("EvaluationFindAllActivity", "규제지역 버튼 클릭됨");
            Intent intent = new Intent(MapActivity.this, RegulatedArea.class);
            startActivity(intent);
        });

        btnShowDialog.setOnClickListener(v -> {
            Log.d("EvaluationFindAllActivity", "평가리스트 버튼 클릭됨");
            showEvaluationDialog();
        });

        btnShowList.setOnClickListener(v -> {
            Log.d("btnShowList", "평가입력 버튼 클릭됨");
            Intent intent = new Intent(MapActivity.this, EvaluationActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.coordinateInput).setOnClickListener(v -> {
            if (projectId != null) {
                ChoiceCooridate choiceCoordinateDialog = ChoiceCooridate.newInstance(projectId, currentProject, registerName);
                choiceCoordinateDialog.show(getSupportFragmentManager(), "choiceCoordinateDialog");
            } else {
                Toast.makeText(MapActivity.this, "Project ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.arCheck).setOnClickListener(v -> {
            Intent intent = new Intent(MapActivity.this, GeneratorActivity.class);
            startActivity(intent);
        });

        coordinateSelectButton.setOnClickListener(v -> toggleMarkerMode());
        resetButton.setOnClickListener(v -> resetToInitialState());
        gpsButton.setOnClickListener(v -> getCurrentLocation());
    }

    private void setupMapFragment() {
        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this::onMapReady);
        }
    }

    private void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;

        naverMap.setOnMapClickListener((point, latLng) -> {
            if (isMarkerEnabled) {
                addMarkerAtLocation(latLng);
            }
        });
    }

    private void addMarkerAtLocation(LatLng latLng) {
        Marker marker = new Marker();
        marker.setPosition(latLng);
        marker.setMap(naverMap);
        markers.add(marker);

        marker.setOnClickListener(overlay -> {
            Intent intent = new Intent(MapActivity.this, DdActivity.class);
            intent.putExtra("latitude", latLng.latitude);
            intent.putExtra("longitude", latLng.longitude);
            startActivity(intent);
            return true;
        });

        Toast.makeText(MapActivity.this, "Clicked Location: " + latLng.latitude + ", " + latLng.longitude, Toast.LENGTH_SHORT).show();
    }

    private void showEvaluationDialog() {
        EvaluationDialogFragment dialog = new EvaluationDialogFragment();
        dialog.show(getSupportFragmentManager(), "EvaluationDialog");
    }

    private void toggleMarkerMode() {
        isMarkerEnabled = !isMarkerEnabled;
        coordinateSelectButton.setText(isMarkerEnabled ? "선택해제" : "좌표선택");
        Toast.makeText(this, isMarkerEnabled ? "마커 추가 모드 활성화" : "마커 추가 모드 비활성화", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        if (!hasLocationPermissions()) {
            requestLocationPermissions();
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

        gpsMarkers.add(currentLocationMarker);

        if (isFollowingLocation) {
            naverMap.setCameraPosition(new CameraPosition(new LatLng(latitude, longitude), 15));
        }

        Toast.makeText(MapActivity.this, "현재 위치: " + latitude + ", " + longitude, Toast.LENGTH_SHORT).show();
    }

    private void resetToInitialState() {
        for (Marker marker : markers) {
            marker.setMap(null);
        }
        markers.clear();

        for (Marker marker : gpsMarkers) {
            marker.setMap(null);
        }
        gpsMarkers.clear();

        Toast.makeText(this, "초기화되었습니다.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates(); // 액티비티 재개 시 위치 업데이트 재시작
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates(); // 액티비티 일시정지 시 위치 업데이트 중지
    }

    // 평가자 이름 입력 대화 상자 표시
    private void showEvaluatorNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("평가자 이름 입력");

        final EditText input = new EditText(this);
        input.setFilters(new InputFilter[]{new KoreanInputFilter()});
        builder.setView(input);

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                registerName = input.getText().toString();
                if (registerName.isEmpty()) {
                    Toast.makeText(MapActivity.this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    showEvaluatorNameDialog();
                } else {
                    Toast.makeText(MapActivity.this, "환영합니다, " + registerName + "님!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
