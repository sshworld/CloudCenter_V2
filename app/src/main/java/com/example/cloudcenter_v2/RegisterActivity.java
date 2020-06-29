package com.example.cloudcenter_v2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText et_id, et_password, et_name, et_age, et_phone, et_load, et_addr;
    private Button btn_register, hobbyButton;
    private TextView hobbyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) { // 액티비티 시작시 처음으로 실행되는 생명주기
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Intent intent1 = getIntent();
        final String message = intent1.getStringExtra("selectHobby");
        ArrayList<String> userHobby = intent1.getStringArrayListExtra("userHobby");

        // 아이디값 찾아주기
        et_id = (EditText)findViewById(R.id.et_id);
        et_password = (EditText)findViewById(R.id.et_password);
        et_name = (EditText)findViewById(R.id.et_name);
        et_age = (EditText)findViewById(R.id.et_age);
        et_phone = findViewById(R.id.et_phone);
        et_load = findViewById(R.id.et_load);
        et_addr = findViewById(R.id.et_addr);
        hobbyName = (TextView)findViewById(R.id.hobbyText);
        hobbyName.setText(message);

        et_id.setText(intent1.getStringExtra("et_id"));
        et_password.setText(intent1.getStringExtra("et_password"));
        et_name.setText(intent1.getStringExtra("et_name"));
        et_age.setText(intent1.getStringExtra("et_age"));
        et_phone.setText(intent1.getStringExtra("et_phone"));
        et_load.setText(intent1.getStringExtra("et_load"));
        et_addr.setText(intent1.getStringExtra("et_addr"));

        hobbyButton = findViewById(R.id.selectHobby);

        hobbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goSelectHobby = new Intent(getApplicationContext(), SelectHobbyActivity.class);
                goSelectHobby.putExtra("et_id", et_id.getText().toString());
                goSelectHobby.putExtra("et_password", et_password.getText().toString());
                goSelectHobby.putExtra("et_name", et_name.getText().toString());
                goSelectHobby.putExtra("et_age", et_age.getText().toString());
                goSelectHobby.putExtra("et_phone", et_phone.getText().toString());
                goSelectHobby.putExtra("et_load", et_load.getText().toString());
                goSelectHobby.putExtra("et_addr", et_addr.getText().toString());

                startActivity(goSelectHobby);
            }
        });

        // 회원가입 버튼 클릭 시 수행
        btn_register = (Button)findViewById(R.id.btn_register);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EditText에 현재 입력되어있는 값을 get(가져오다)해온다.
                String userID = et_id.getText().toString();
                String userPassword = et_password.getText().toString();
                String userName = et_name.getText().toString();
                int userAge = Integer.parseInt(et_age.getText().toString());
                String userPhone = et_phone.getText().toString();
                String userLoad = et_load.getText().toString();
                String userAddr = et_addr.getText().toString();
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                Toast.makeText(getApplicationContext(), "회원등록에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                            } else { // 회원등록에 실패할 경우
                                Toast.makeText(getApplicationContext(), "회원등록에 실패하셨습니다.", Toast.LENGTH_LONG).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                // 서버로 Volley를 이용해서 요청을 함
                RegisterRequest registerRequest = new RegisterRequest(userID, userPassword, userName, userAge, userPhone, userLoad, userAddr, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);

                for(String mg : message.split(",", 3)) {
                    SelectRequest selectRequest = new SelectRequest(userID, mg, responseListener);
                    RequestQueue queue1 = Volley.newRequestQueue(RegisterActivity.this);
                    queue1.add(selectRequest);
                }
            }
        });
    }

    public class SelectRequest extends StringRequest {

        // 서버 URL 실행
        final static private String URL = "http://ssh9754.dothome.co.kr/selectHobby.php";
        private Map<String, String> map;

        public SelectRequest(String userID, String message, Response.Listener<String> listener){
            super(Method.POST, URL, listener, null);

            map = new HashMap<>();
            map.put("userID", userID);
            map.put("hobbyName", message);

        }

        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            return map;
        }
    }
}
