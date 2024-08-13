package com.example.sundo_project_app.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sundo_project_app.MainActivity;
import com.example.sundo_project_app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput;
    private EditText passwordInput;
    private CheckBox autoLoginCheckbox;
    private Button loginButton;
    private Button signUpButton;

    private static final String LOGIN_URL = "http://10.0.2.2:8000/api/companies/login"; // 서버의 로그인 엔드포인트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        autoLoginCheckbox = findViewById(R.id.autoLoginCheckbox);
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signupButton);

        // 자동 로그인 설정 확인
        checkAutoLogin();

        loginButton.setOnClickListener(view -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "이메일과 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
            } else {
                login(email, password);
            }
        });

        signUpButton.setOnClickListener(view -> {
            // SignUpActivity로 이동
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void login(String email, String password) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        // JSON 객체 생성
        String json = "{\"email\":\"" + email + "\", \"password\":\"" + password + "\"}";
        RequestBody body = RequestBody.create(JSON, json);

        // 서버로 요청 전송
        Request request = new Request.Builder()
                .url(LOGIN_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 요청 실패 시 처리
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    String token = extractTokenFromResponse(responseBody);

                    if (autoLoginCheckbox.isChecked()) {
                        // 자동 로그인 설정을 저장
                        saveToken(token);
                    }

                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                        // 메인 화면으로 이동
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "로그인 실패: 이메일 또는 비밀번호를 확인하세요", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void saveToken(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.putBoolean("auto_login", true);
        editor.apply();
    }

    private void checkAutoLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean autoLogin = sharedPreferences.getBoolean("auto_login", false);
        String token = sharedPreferences.getString("token", null);

        if (autoLogin && token != null) {
            // 토큰 유효성 검증
            validateToken(token);
        }
    }

    private void validateToken(String token) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://10.0.2.2:8000/api/validate-token")
                .header("Authorization", "Bearer " + token)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 토큰 검증 실패: 자동 로그인 해제
                runOnUiThread(() -> {
                    SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("token");
                    editor.putBoolean("auto_login", false);
                    editor.apply();
                    Toast.makeText(LoginActivity.this, "자동 로그인 실패", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // 토큰 검증 성공: 메인 화면으로 이동
                    runOnUiThread(() -> {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    // 토큰 검증 실패: 자동 로그인 해제
                    runOnUiThread(() -> {
                        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("token");
                        editor.putBoolean("auto_login", false);
                        editor.apply();
                        Toast.makeText(LoginActivity.this, "자동 로그인 실패", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private String extractTokenFromResponse(String responseBody) {
        // JSON 파싱을 사용하여 토큰을 추출 (예: Gson 또는 JSONObject 사용)
        try {
            JSONObject jsonObject = new JSONObject(responseBody);
            return jsonObject.getString("token");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
