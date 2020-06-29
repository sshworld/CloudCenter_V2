package com.example.cloudcenter_v2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BookingActivity extends AppCompatActivity {
    TextView academyName, bookingDate, bookingTime;
    CalendarView calendarView;
    Button requestAcademy;
    RadioGroup rg_time;
    RadioButton rb1, rb2;

    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        Intent userDetail = getIntent();

        final String userID = userDetail.getStringExtra("userID");
        final int academyCode = userDetail.getIntExtra("academyCode", 0);

        academyName = (TextView) findViewById(R.id.academyName);
        calendarView = (CalendarView) findViewById(R.id.calendar);
        bookingDate = findViewById(R.id.bookingDate);
        bookingTime = findViewById(R.id.bookingTime);
        requestAcademy = findViewById(R.id.requestAcademy);
        rg_time = findViewById(R.id.rg_time);
        rb1 = findViewById(R.id.rb1);
        rb2 = findViewById(R.id.rb2);

        academyName.setText(userDetail.getStringExtra("academyName"));


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                String date = year + "-" + (month + 1) + "-" + dayOfMonth;
                Log.d(date, "date");

                try {

                    String day = getDateDay(date, "yyyy-MM-dd");


                    bookingDate.setVisibility(View.VISIBLE);
                    bookingDate.setText(date + " " + day);
                    rb1.setChecked(false);
                    rb2.setChecked(false);
                    bookingTime.setText("");
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        rg_time.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rb1){
                    bookingTime.setText("오전반");
                    bookingTime.setVisibility(View.VISIBLE);
                }
                else if(checkedId == R.id.rb2) {
                    bookingTime.setText("오후반");
                    bookingTime.setVisibility(View.VISIBLE);
                }
            }
        });

        requestAcademy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Toast.makeText(getApplicationContext(),bookingDate.getText().toString() + " " + "\n" + bookingTime.getText() + "에 예약완료",Toast.LENGTH_LONG).show();
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");
                                if (success) {
                                    Intent intent = new Intent(BookingActivity.this, MainActivity.class);
                                    intent.putExtra("userID", userID);
                                    startActivity(intent);
                                } else { // 회원등록에 실패할 경우
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    // 서버로 Volley를 이용해서 요청을 함
                    BookingRequest bookingRequest = new BookingRequest(userID, academyCode, bookingDate.getText().toString(), bookingTime.getText().toString(), responseListener);
                    RequestQueue queue = Volley.newRequestQueue(BookingActivity.this);
                    queue.add(bookingRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public static String getDateDay(String date, String dateType) throws Exception {

        String day = "";

        SimpleDateFormat dateFormat = new SimpleDateFormat(dateType);
        Date nDate = dateFormat.parse(date);
        Log.d(nDate + "", "날짜");

        Calendar cal = Calendar.getInstance();
        cal.setTime(nDate);

        int dayNum = cal.get(Calendar.DAY_OF_WEEK);
        Log.d(dayNum + "", "요일");

        switch (dayNum) {
            case 1:
                day = "일";
                break;
            case 2:
                day = "월";
                break;
            case 3:
                day = "화";
                break;
            case 4:
                day = "수";
                break;
            case 5:
                day = "목";
                break;
            case 6:
                day = "금";
                break;
            case 7:
                day = "토";
                break;

        }

        return day;
    }

    public class BookingRequest extends StringRequest {

        // 서버 URL 실행
        final static private String URL = "http://ssh9754.dothome.co.kr/Booking.php";
        private Map<String, String> map;

        public BookingRequest(String userID, int academyCode, String bookingDate, String bookingTime ,Response.Listener<String> listener){
            super(Method.POST, URL, listener, null);

            map = new HashMap<>();
            map.put("userID", userID);
            map.put("academyCode", academyCode + "");
            map.put("bookingDate", bookingDate);
            map.put("bookingTime", bookingTime);


        }

        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            return map;
        }
    }
}
