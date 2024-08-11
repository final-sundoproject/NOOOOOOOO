// EvaluationFindAllActivity.java
package com.example.sundo_project_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class EvaluationFindAllActivity extends AppCompatActivity {

    private Button btnShowDialog;
    private Button btnShowList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map); // Using map layout

        btnShowDialog = findViewById(R.id.btnShowDialog);
        btnShowList = findViewById(R.id.enteredPoint);

        btnShowDialog.setOnClickListener(v -> {
            Log.d("EvaluationFindAllActivity", "평가리스트 버튼 클릭됨");
            showEvaluationDialog();
        });

        btnShowList.setOnClickListener(v -> {
            Log.d("btnShowList", "평가입력 버튼 클릭됨");
            Intent intent = new Intent(EvaluationFindAllActivity.this, EvaluationActivity.class);
            startActivity(intent);
        });
    }

    private void showEvaluationDialog() {
        EvaluationDialogFragment dialog = new EvaluationDialogFragment();
        dialog.show(getSupportFragmentManager(), "EvaluationDialog");
    }
}
