package com.example.sundo_project_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class mapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        // '좌표 선택' 버튼을 찾고 클릭 리스너를 추가합니다.
        findViewById(R.id.coordinateInput).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DmsActivity 시작합니다.
                Intent intent = new Intent(mapActivity.this, DmsActivity.class);
                startActivity(intent);
            }
        });

        // 'AR 확인' 버튼을 찾고 클릭 리스너를 추가합니다.
        findViewById(R.id.arCheck).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // GeneratorActivity 시작합니다.
                Intent intent = new Intent(mapActivity.this, GeneratorActivity.class);
                startActivity(intent);
            }
        });
    }
}
