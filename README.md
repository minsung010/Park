#  Park - 스마트 공영 주차장 찾기 앱

**"복잡한 도시에서 빈 주차장, 이제 쉽게 찾으세요"**

`Park` 앱은 사용자 위치 기반으로 주변의 공영 주차장 정보를 지도에 표시하고, 실시간 주차 가능 여부와 상세 정보를 제공하여 주차난을 해소하는 안드로이드 애플리케이션입니다.

---

##  주요 기능 (Key Features)

### 1.  내 주변 주차장 지도 보기
- **Google Maps 연동**: 현재 내 위치를 중심으로 주변 공영 주차장 마커를 지도에 표시합니다.
- **좌표 변환**: Proj4j 라이브러리를 활용하여 다양한 좌표계의 공공 데이터를 정확한 위도/경도로 변환하여 제공합니다.

### 2.  주차장 상세 정보
- **상세 화면**: 주차장 마커 클릭 시 요금, 운영 시간, 주차 구획 수 등 상세 정보를 제공합니다.
- **실시간 데이터**: (구현 예정/확인 필요) 공공 데이터를 활용한 실시간 잔여 주차면 수 확인이 가능합니다.

### 3.  클라우드 데이터 관리
- **Firebase 연동**: 사용자 즐겨찾기 목록이나 앱 설정 등 개인화된 데이터를 Firebase Firestore/Realtime Database에 안전하게 저장합니다.
- **로그인/인증**: (코드 기반 추정) Firebase Auth를 통한 사용자 관리 기능을 포함하고 있습니다.

---

##  기술 스택 (Tech Stack)

| 카테고리 | 사용 기술 |
|:---:|:---|
| **개발 언어** | Java / Kotlin, Android SDK (API 35) |
| **지도 API** | Google Maps SDK for Android |
| **위치 서비스** | Google Play Services Location |
| **데이터 처리** | Proj4j (좌표 변환) |
| **백엔드 (BaaS)** | Firebase (Analytics, Firestore, Auth, Realtime Database) |
| **UI** | Material Design, ConstraintLayout, SplashScreen |

---

##  설치 및 실행 방법

1. **저장소 클론 (Clone Repository)**
   ```bash
   git clone https://github.com/minsung010/Park.git
   ```
2. **안드로이드 스튜디오 열기**
   - 프로젝트 폴더(`Park`)를 Android Studio로 엽니다.
3. **API 키 설정**
   - `AndroidManifest.xml` 파일 내 `com.google.android.geo.API_KEY` 부분에 본인의 Google Maps API 키가 정상적으로 입력되어 있는지 확인합니다.
4. **Firebase 설정**
   - `google-services.json` 파일이 `app/` 폴더 내에 위치해야 정상적으로 빌드됩니다.
5. **실행 (Run)**
   - 에뮬레이터 또는 실제 기기에서 앱을 실행합니다.

---
