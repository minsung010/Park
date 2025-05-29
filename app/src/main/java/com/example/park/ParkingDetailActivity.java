package com.example.park;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ParkingDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_detail);

        TextView titleView = findViewById(R.id.title);
        TextView contentView = findViewById(R.id.content);

        String title = getIntent().getStringExtra("title");
        String snippet = getIntent().getStringExtra("snippet");

        titleView.setText(title);
        contentView.setText(snippet);
    }
}
