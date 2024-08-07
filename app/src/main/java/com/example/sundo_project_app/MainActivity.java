package com.example.sundo_project_app;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button addBusinessButton;
    private Button removeBusinessButton;
    private LinearLayout boxContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // XML 레이아웃에서 뷰들을 찾습니다.
        addBusinessButton = findViewById(R.id.addBusiness);
        removeBusinessButton = findViewById(R.id.removeBusiness);
        boxContainer = findViewById(R.id.boxContainer);

        // 사업 추가 버튼 클릭 리스너 설정
        addBusinessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddBusinessDialog();
            }
        });

        // 사업 삭제 버튼 클릭 리스너 설정
        removeBusinessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 박스를 삭제하는 로직
                if (boxContainer.getChildCount() > 0) {
                    boxContainer.removeViewAt(boxContainer.getChildCount() - 1);
                }
            }
        });
    }

    private void showAddBusinessDialog() {
        // 모달 다이얼로그 생성
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.modal_dialog);

        // 다이얼로그 내의 뷰들을 찾습니다.
        final EditText projectNameInput = dialog.findViewById(R.id.project_name_input);
        Button confirmButton = dialog.findViewById(R.id.confirm_button);
        Button cancelButton = dialog.findViewById(R.id.cancel_button);

        // 확인 버튼 클릭 리스너 설정
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String projectName = projectNameInput.getText().toString();
                if (!projectName.isEmpty()) {
                    addTextBox(projectName);
                }
                dialog.dismiss();
            }
        });

        // 취소 버튼 클릭 리스너 설정
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void addTextBox(String projectName) {
        // 새로운 텍스트 박스를 생성하고 설정합니다.
        TextView newTextBox = new TextView(this);
        newTextBox.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        newTextBox.setPadding(16, 16, 16, 16);
        newTextBox.setBackground(getResources().getDrawable(android.R.drawable.editbox_background));
        newTextBox.setText(projectName);
        newTextBox.setTextSize(16);

        // 텍스트 박스를 컨테이너에 추가합니다.
        boxContainer.addView(newTextBox);
    }
}