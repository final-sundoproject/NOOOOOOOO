package com.example.sundo_project_app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (hasCameraPermission()) {
            if (hasLocationPermission()) {
                setContentView(R.layout.activity_main);
            } else {

            }
        } else {
            showPermissionExplanationDialog();
        }
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }



    private void showPermissionExplanationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_permission_explanation, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();

        Button allow = view.findViewById(R.id.btn_allow);
        Button deny = view.findViewById(R.id.btn_deny);

        allow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        deny.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "카메라 권한이 거절되었습니다. \n앱을 종료합니다.", Toast.LENGTH_LONG).show();
                dialog.dismiss();
                finish(); // 앱 종료
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this, "카메라 권한이 거부되었습니다. \n앱을 종료합니다.", Toast.LENGTH_LONG).show();
                finish(); // 앱 종료
            }
        } else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setContentView(R.layout.activity_main); // 모든 권한이 승인되었을 때 UI 설정
            } else {
                Toast.makeText(this, "위치 권한이 거부되었습니다. \n앱을 종료합니다.", Toast.LENGTH_LONG).show();
                finish(); // 앱 종료

            }
        }
    }
}
