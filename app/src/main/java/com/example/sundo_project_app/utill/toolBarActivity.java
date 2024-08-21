package com.example.sundo_project_app.utill;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.TextView;

import com.example.sundo_project_app.R;

public abstract class toolBarActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toolbar); // 툴바가 포함된 레이아웃 설정

        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.userNameTextView);
        setSupportActionBar(toolbar);
        setToolbarTitle(getDefaultTitle()); // 기본 타이틀 설정

        // 액티비티의 본문 레이아웃을 설정합니다.
        View contentView = getLayoutInflater().inflate(getContentView(), null);
        FrameLayout container = findViewById(R.id.container);
        container.addView(contentView);
    }

    protected abstract int getContentView(); // 하위 클래스에서 본문 레이아웃 리턴

    protected String getDefaultTitle() {
        return "Default Title"; // 기본 타이틀을 하위 클래스에서 재정의 가능
    }

    public void setToolbarTitle(String title) {
        if (toolbarTitle != null) {
            toolbarTitle.setText(title);
        }
    }

    public void setToolbarVisibility(boolean visible) {
        if (toolbar != null) {
            toolbar.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }
}
