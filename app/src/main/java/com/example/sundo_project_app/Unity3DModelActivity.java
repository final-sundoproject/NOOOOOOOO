package com.example.sundo_project_app;

import android.content.Intent;
import android.os.Bundle;
import com.unity3d.player.UnityPlayer;  // UnityPlayer 임포트
import com.unity3d.player.UnityPlayerActivity;  // UnityPlayerActivity 임포트

public class Unity3DModelActivity extends UnityPlayerActivity {

    // Unity에 좌표를 전달하는 메서드
    public void sendCoordinatesToUnity(double latitude, double longitude) {
        // Unity 스크립트의 "SetModelPosition" 메서드 호출
        UnityPlayer.UnitySendMessage("ModelController", "SetModelPosition", latitude + "," + longitude);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Intent로부터 좌표를 받습니다.
        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra("latitude", 0);
        double longitude = intent.getDoubleExtra("longitude", 0);

        // Unity로 좌표를 보냅니다.
        sendCoordinatesToUnity(latitude, longitude);
    }
}
