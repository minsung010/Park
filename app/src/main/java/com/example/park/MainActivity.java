package com.example.park;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.firebase.database.*;

import java.io.IOException;
import java.util.*;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText addressEditText;
    private ImageButton zoomInButton, zoomOutButton, myLocationButton, favoriteListButton;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();  // Realtime DB 참조
    DatabaseReference favoriteRef = dbRef.child("즐겨찾기");  // 즐겨찾기 노드 참조

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addressEditText = findViewById(R.id.editText);
        Button confirmButton = findViewById(R.id.button2);
        ImageButton searchButton = findViewById(R.id.button_search);
        zoomInButton = findViewById(R.id.button_zoom_in);
        zoomOutButton = findViewById(R.id.button_zoom_out);
        myLocationButton = findViewById(R.id.button_my_location);
        favoriteListButton = findViewById(R.id.button_favorite_list);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        SupportMapFragment mapFragment = new SupportMapFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.map_container, mapFragment)
                .commit();
        mapFragment.getMapAsync(this);

        confirmButton.setOnClickListener(v -> {
            String address = addressEditText.getText().toString().trim();
            if (!address.isEmpty()) {
                // 구글 지도에서 길찾기 실행
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Uri.encode(address));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                // 구글 지도가 설치되어 있는지 확인
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(MainActivity.this, "Google 지도 앱이 설치되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "주소를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            }
        });


        searchButton.setOnClickListener(v -> {
            String address = addressEditText.getText().toString().trim();
            if (!address.isEmpty()) {
                searchAddressAndMoveMap(address);
            } else {
                Toast.makeText(MainActivity.this, "주소를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        favoriteListButton.setOnClickListener(v -> {
            loadFavoriteList(); // 즐겨찾기 목록 불러오기
        });

        zoomInButton.setOnClickListener(v -> {
            if (mMap != null) {
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });

        zoomOutButton.setOnClickListener(v -> {
            if (mMap != null) {
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });

        myLocationButton.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                return;
            }

            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null && mMap != null) {
                    LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(myLatLng).title("내 위치"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 18));
                } else {
                    Toast.makeText(MainActivity.this, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // 즐겨찾기 추가 함수
    private void addFavorite(String address) {
        String key = favoriteRef.push().getKey();  // 고유 키 생성
        if (key != null) {
            favoriteRef.child(key).setValue(address);  // Firebase에 즐겨찾기 주소 저장
        }
    }

    // 즐겨찾기 목록 불러오기
    private void loadFavoriteList() {
        favoriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(MainActivity.this, "즐겨찾기가 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<String> favoriteAddresses = new ArrayList<>();
                for (DataSnapshot favoriteSnapshot : snapshot.getChildren()) {
                    String address = favoriteSnapshot.getKey();  // 키는 주차장 이름
                    Boolean isFavorite = favoriteSnapshot.getValue(Boolean.class);  // 값은 true/false

                    // 값이 true일 경우만 즐겨찾기 목록에 추가
                    if (isFavorite != null && isFavorite) {
                        favoriteAddresses.add(address);
                    }
                }

                // 즐겨찾기 목록을 AlertDialog로 표시
                if (!favoriteAddresses.isEmpty()) {
                    String[] favoriteArray = favoriteAddresses.toArray(new String[0]);
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("즐겨찾기 목록")
                            .setItems(favoriteArray, (dialog, which) -> {
                                String selectedAddress = favoriteArray[which];
                                searchAddressAndMoveMap(selectedAddress);
                            })
                            .setNegativeButton("닫기", null)
                            .show();
                } else {
                    Toast.makeText(MainActivity.this, "즐겨찾기가 비어 있습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "데이터를 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 즐겨찾기 목록에서 선택한 주차장 위치로 카메라만 이동
    private void searchAddressAndMoveMap(String address) {
        Geocoder geocoder = new Geocoder(MainActivity.this);
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                // 카메라만 해당 위치로 이동 (마커 추가 없음)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
            } else {
                Toast.makeText(MainActivity.this, "주소를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "지오코딩 실패. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // 지도 타입: HYBRID (건물과 도로 상세 보기 가능)
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // 주차장 데이터 로딩 예시 (Firebase에서 직접 주차장 정보를 가져오는 코드)
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    String name = child.child("주차장_명").getValue(String.class);
                    String address = child.child("소재지").getValue(String.class);

                    // 위도, 경도 값은 Double로 가져옴
                    Double latitude = child.child("위도").getValue(Double.class);
                    Double longitude = child.child("경도").getValue(Double.class);

                    // 계약일, 운영 시작일 등을 Long 타입으로 저장했다면 이를 String으로 변환
                    String contractDate = getStringValue(child.child("계약일"));
                    String operationStartDate = getStringValue(child.child("운영_시작일"));
                    String type = getStringValue(child.child("유형"));
                    String spaces = getStringValue(child.child("주차면_수"));

                    if (latitude != null && longitude != null) {
                        LatLng latLng = new LatLng(latitude, longitude);

                        String snippet = "주소: " + address;
                        BitmapDescriptor customIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_parking_marker);
                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(name)
                                .snippet(snippet)
                                .icon(customIcon));

                        // 마커 클릭 시 상세 화면으로 이동
                        mMap.setOnInfoWindowClickListener(markerClicked -> {
                            // 마커 클릭 시 Intent로 상세정보 전달
                            Intent intent = new Intent(MainActivity.this, ParkingDetailActivity.class);
                            intent.putExtra("title", markerClicked.getTitle());
                            intent.putExtra("snippet", markerClicked.getSnippet());
                            intent.putExtra("address", address);
                            intent.putExtra("contract_date", contractDate);
                            intent.putExtra("operation_start_date", operationStartDate);
                            intent.putExtra("type", type);
                            intent.putExtra("spaces", spaces);
                            startActivity(intent);  // ParkingDetailActivity로 이동
                        });
                    }
                }
            }

            // Long 타입을 String으로 변환하는 함수
            private String getStringValue(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() instanceof Long) {
                    return String.valueOf(dataSnapshot.getValue(Long.class));
                } else {
                    return dataSnapshot.getValue(String.class);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "데이터 로드 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                myLocationButton.performClick();
            } else {
                Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
