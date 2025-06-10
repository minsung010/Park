package com.example.park;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ParkingDetailActivity extends AppCompatActivity {

    private TextView titleView, contentView, addressView, contractDateView, operationStartDateView, typeView, spacesView;
    private Button favoriteButton;
    private String parkingTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_detail);

        // Intent로 전달받은 정보 (주차장명)
        parkingTitle = getIntent().getStringExtra("title");
        String snippet = getIntent().getStringExtra("snippet");
        String address = getIntent().getStringExtra("address");
        String contractDate = getIntent().getStringExtra("contract_date");
        String operationStartDate = getIntent().getStringExtra("operation_start_date");
        String type = getIntent().getStringExtra("type");
        String spaces = getIntent().getStringExtra("spaces");

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
            // Firebase에 주차장 즐겨찾기 상태 저장하는 로직 추가
            Toast.makeText(ParkingDetailActivity.this, "즐겨찾기 기능 구현 예정", Toast.LENGTH_SHORT).show();
        });
    }
}
