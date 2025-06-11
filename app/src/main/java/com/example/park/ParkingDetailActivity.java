package com.example.park;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ParkingDetailActivity extends AppCompatActivity {

    private TextView titleView, contentView, addressView, contractDateView, operationStartDateView, typeView, spacesView;
    private Button favoriteButton;
    private String parkingTitle, snippet, address, contractDate, operationStartDate, type, spaces;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_detail);

        // Firebase Database 참조 초기화
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Intent로 전달받은 정보
        parkingTitle = getIntent().getStringExtra("title");
        snippet = getIntent().getStringExtra("snippet");
        address = getIntent().getStringExtra("address");
        contractDate = getIntent().getStringExtra("contract_date");
        operationStartDate = getIntent().getStringExtra("operation_start_date");
        type = getIntent().getStringExtra("type");
        spaces = getIntent().getStringExtra("spaces");

        // UI 요소 초기화
        titleView = findViewById(R.id.title);
        contentView = findViewById(R.id.content);
        addressView = findViewById(R.id.address);
        contractDateView = findViewById(R.id.contract_date);
        operationStartDateView = findViewById(R.id.operation_start_date);
        typeView = findViewById(R.id.type);
        spacesView = findViewById(R.id.spaces);
        favoriteButton = findViewById(R.id.favoriteButton);

        // UI에 정보 설정
        titleView.setText(parkingTitle);
        contentView.setText(snippet);
        addressView.setText(address);
        contractDateView.setText("계약일: " + contractDate);
        operationStartDateView.setText("운영 시작일: " + operationStartDate);
        typeView.setText("유형: " + type);
        spacesView.setText("주차면 수: " + spaces);

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
                } else {
                    Toast.makeText(ParkingDetailActivity.this, "Firebase 오류: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
