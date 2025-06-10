package com.example.park;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ParkingDetailActivity extends AppCompatActivity {

    private TextView titleView, contentView;
    private Button favoriteButton;
    private DatabaseReference databaseReference;
    private String parkingTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_detail);

        // Firebase Database 참조 초기화
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Intent로 전달받은 정보 (주차장명)
        parkingTitle = getIntent().getStringExtra("title");
        String snippet = getIntent().getStringExtra("snippet");

        // UI 요소 초기화
        titleView = findViewById(R.id.title);
        contentView = findViewById(R.id.content);
        favoriteButton = findViewById(R.id.favoriteButton);

        // UI에 정보 설정
        titleView.setText(parkingTitle);
        contentView.setText(snippet);

        // 즐겨찾기 버튼 클릭 시
        favoriteButton.setOnClickListener(v -> {
            // Firebase의 '즐겨찾기' 경로에 접근
            DatabaseReference favoritesRef = databaseReference.child("즐겨찾기");

            // 즐겨찾기 목록에 해당 주차장이 이미 있는지 확인
            favoritesRef.child(parkingTitle).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        // 이미 즐겨찾기 목록에 있으면 삭제
                        favoritesRef.child(parkingTitle).removeValue();
                        favoriteButton.setText("즐겨찾기 추가");
                        Toast.makeText(ParkingDetailActivity.this, "즐겨찾기에서 제거되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        // 즐겨찾기 목록에 없으면 추가
                        favoritesRef.child(parkingTitle).setValue(true);
                        favoriteButton.setText("즐겨찾기 제거");
                        Toast.makeText(ParkingDetailActivity.this, "즐겨찾기에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }
}
