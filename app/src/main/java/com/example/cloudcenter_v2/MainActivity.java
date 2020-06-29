package com.example.cloudcenter_v2;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements MapView.POIItemEventListener, MapView.MapViewEventListener {


    private GpsTracker gpsTracker;
    ArrayList<JSONObject> arrayJson = new ArrayList<JSONObject>();

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    FloatingActionButton fab, fab1;

    MapPOIItem mCustomBmMarker;

    TextView addressName;

    LinearLayout page;

    Button showAcademy;

    Animation tranlateTopAnim;
    Animation tranlateBottomAnim;
    boolean isPageOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");



        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("response", "실행");
                    //JSONObject jsonObject = new JSONObject(response);
                    //배열 생성
                    JSONArray jsonArray = new JSONArray(response);
                    //배열 입력
                   //jsonArray.put(jsonObject);
                    //initView
                    initView(jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        MainRequest mainRequest = new MainRequest(userID, responseListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(mainRequest);
    }


    private void initView(JSONArray jsonArray) throws JSONException {



        for(int k = 0; k < jsonArray.length(); k++){
            JSONObject tempJson = jsonArray.getJSONObject(k);
            arrayJson.add(tempJson);
        }

        JSONObject[] jsons = new JSONObject[arrayJson.size()];
        arrayJson.toArray(jsons);

        fab = findViewById(R.id.goMyLocation);
        fab1 = findViewById(R.id.goHome);
        addressName = findViewById(R.id.address);


        final MapView mapView = new MapView(this);
        final ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        final Geocoder geocoder = new Geocoder(this);


        mapView.setMapViewEventListener(this);
        mapView.setPOIItemEventListener(this);






        // 처음 집
        String address = arrayJson.get(0).getString("userLoad");

        List<Address> list = null;

        // 친구들 화이팅 !! - LHH
        try {
            list = geocoder.getFromLocationName(address, 10);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final Address addr = list.get(0);
        double lat = addr.getLatitude();
        double lon = addr.getLongitude();
        Log.d(lat + "", "집 위도");
        Log.d(lon + "", "집 경도");

        addressName.setText(address);

        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(lat, lon), true);

        Log.d(arrayJson.size()+"", "배열길이");

        // foreach문써서 DB박기
        for(int i = 0; i < arrayJson.size(); i++) {
            MapPOIItem mapPOIItem = createCustomBitmapMarker(mapView, arrayJson.get(i).getString("academyName") ,Integer.parseInt(arrayJson.get(i).getString("academyCode")),
                    arrayJson.get(i).getString("academyAddr"));
        }


        // 맵 나타내기
        mapViewContainer.addView(mapView);

        // 현재 위치 검색
        fab.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                List<Double> list = clickFab();

                double latitude = list.get(0);
                double longitude = list.get(1);

                String address = getCurrentAddress(latitude, longitude);
                Log.d(address, "주소 명");
                address = address.substring(5);
                addressName.setText(address);

                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);
            }
        });

        // 다시 자신의 주소로 검색
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = null;
                try {
                    address = arrayJson.get(0).getString("userLoad");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                List<Address> list = null;

                try {
                    list = geocoder.getFromLocationName(address, 10);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Address addr = list.get(0);
                double lat = addr.getLatitude();
                double lon = addr.getLongitude();
                Log.d(lat + "", "집 위도");
                Log.d(lon + "", "집 경도");

                addressName.setText(address);

                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(lat, lon), true);

            }
        });


    }

    // 현재위치로 검색
    private List<Double> clickFab(){
        if (!checkLocationServicesStatus()) {

            showDialogForLocationServiceSetting();
        }else {

            checkRunTimePermission();
        }

        List<Double> addr = new ArrayList<Double>();

        gpsTracker = new GpsTracker(MainActivity.this);

        double latitude = gpsTracker.getLatitude();
        Log.d(latitude + "", "위도");
        double longitude = gpsTracker.getLongitude();
        Log.d(longitude + "", "경도");

        addr.add(latitude);
        addr.add(longitude);

        return addr;


    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {

                //위치 값을 가져올 수 있음
                ;
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();


                }else {

                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission(){

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음



        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(MainActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }



    public String getCurrentAddress( double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }



        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";

    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }




    // CalloutBalloonAdapter 인터페이스 구현
    class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {
        private final View mCalloutBalloon;

        public CustomCalloutBalloonAdapter(String academyName, String academyIntroduce) {
            mCalloutBalloon = getLayoutInflater().inflate(R.layout.balloon_overlay, null);
            ((TextView)mCalloutBalloon.findViewById(R.id.balloon_title)).setText(academyName);
            ((TextView)mCalloutBalloon.findViewById(R.id.balloon_snip)).setText(academyIntroduce);
        }

        @Override
        public View getCalloutBalloon(MapPOIItem poiItem) {


            return mCalloutBalloon;
        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem poiItem) {

            return null;
        }

    }

    private MapPOIItem createCustomBitmapMarker(MapView mapView, String academyName, int index, String academyAddr) {
        String address = academyAddr;
        Geocoder geocoder = new Geocoder(getApplicationContext());
        List<Address> list = null;

        try {
            list = geocoder.getFromLocationName(address, 10);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Address addr = list.get(0);
        double lat = addr.getLatitude();
        double lon = addr.getLongitude();

        mCustomBmMarker = new MapPOIItem();
        mCustomBmMarker.setItemName(academyName);
        mCustomBmMarker.setTag(index);
        mCustomBmMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(lat, lon));
        mCustomBmMarker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        mCustomBmMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        mCustomBmMarker.isMoveToCenterOnSelect();

        mapView.addPOIItem(mCustomBmMarker);

        return mCustomBmMarker;
    }

    // POIItemEventListenr 인터페이스 구현
    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {


    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @SuppressLint({"RestrictedApi", "SetTextI18n"})
    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, final MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

        showAcademy = findViewById(R.id.btn_Academy);
        showAcademy.setVisibility(View.VISIBLE);
        page = findViewById(R.id.page);
        //anim 폴더의 애니메이션을 가져와서 준비
        tranlateTopAnim = AnimationUtils.loadAnimation(this,R.anim.translate_top);
        tranlateBottomAnim = AnimationUtils.loadAnimation(this,R.anim.translate_bottom);
        //페이지 슬라이딩 이벤트가 발생했을때 애니메이션이 시작 됐는지 종료 됐는지 감지할 수 있다.
        SlidingPageAnimationListener animListener = new SlidingPageAnimationListener();
        tranlateTopAnim.setAnimationListener(animListener);
        tranlateBottomAnim.setAnimationListener(animListener);

        fab1.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);

        ImageView academyPicture = findViewById(R.id.academyPicture);
        TextView academyName = findViewById(R.id.academyName);
        TextView academyAddr = findViewById(R.id.academyAddr);
        TextView academyGuide = findViewById(R.id.academyGuide);
        TextView academyPrice = findViewById(R.id.academyPrice);

        int index = mapPOIItem.getTag();

        for(int i = 0; i < arrayJson.size(); i++){
            try {
                if(index == arrayJson.get(i).getInt("academyCode")){
                    Log.d(index + "", "index");
                    Log.d(i + "", "i");
                    String resName = "@drawable/a" + (index);
                    int resID = getResources().getIdentifier(resName, "drawable", this.getPackageName());
                    academyPicture.setImageResource(resID);
                    academyName.setText(arrayJson.get(i).getString("academyName"));
                    academyAddr.setText("위치 : " + arrayJson.get(i).getString("academyAddr"));
                    academyGuide.setText("지도 방법 : " + arrayJson.get(i).getString("academyGuide"));
                    academyPrice.setText("가격 : " + arrayJson.get(i).getString("academyPrice" + "원"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        showAcademy.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if(isPageOpen) {
                    page.startAnimation(tranlateTopAnim);
                }
                else{
                    page.setVisibility(View.VISIBLE);
                    page.startAnimation(tranlateBottomAnim);
                }
            }
        });

        final Button academyBooking = findViewById(R.id.academyBooking);

        academyBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent goBooking = new Intent(getApplicationContext(), BookingActivity.class);
                    goBooking.putExtra("userID", arrayJson.get(0).getString("userID"));
                    for(int i = 0; i < arrayJson.size(); i++){
                        if(mapPOIItem.getTag() == Integer.parseInt(arrayJson.get(i).getString("academyCode"))){
                            goBooking.putExtra("academyCode", arrayJson.get(i).getInt("academyCode"));
                            goBooking.putExtra("academyName", arrayJson.get(i).getString("academyName"));
                        }
                    }
                    startActivity(goBooking);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }


    // MapViewEventListenr 인터페이스 구현
    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

        showAcademy = findViewById(R.id.btn_Academy);
        if (!isPageOpen){
            showAcademy.setVisibility(View.GONE);
            fab1.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    private class SlidingPageAnimationListener implements Animation.AnimationListener{
        @Override public void onAnimationStart(Animation animation) {

        }

        public void onAnimationEnd(Animation animation){
            if(isPageOpen){
                page.setVisibility(View.INVISIBLE);
                showAcademy.setText("열기");
                isPageOpen = false;
            }
            else{
                showAcademy.setText("닫기");
                isPageOpen = true;
            }
        }

        @Override public void onAnimationRepeat(Animation animation) {

        }
    }

    public class MainRequest extends StringRequest {

        // 서버 URL 실행
        final static private String URL = "http://ssh9754.dothome.co.kr/Main.php";
        private Map<String, String> map;

        public MainRequest(String userID, Response.Listener<String> listener){
            super(Method.POST, URL, listener, null);

            map = new HashMap<>();
            map.put("userID", userID);

        }

        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            return map;
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}

