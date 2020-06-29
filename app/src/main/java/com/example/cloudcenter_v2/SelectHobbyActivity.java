package com.example.cloudcenter_v2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class SelectHobbyActivity extends AppCompatActivity {

    RadioGroup hobby;
    RadioButton music, sports, cook, art;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_hobby);

        final Intent intent1 = getIntent();
        final String et_id, et_password, et_name, et_age, et_phone, et_load, et_addr;


        et_id = (intent1.getStringExtra("et_id"));
        et_password = (intent1.getStringExtra("et_password"));
        et_name = (intent1.getStringExtra("et_name"));
        et_age = (intent1.getStringExtra("et_age"));
        et_phone = (intent1.getStringExtra("et_phone"));
        et_load = (intent1.getStringExtra("et_load"));
        et_addr = (intent1.getStringExtra("et_addr"));

        //cook어댑터 시작

        final List<String> userHobby = new ArrayList<>();
        final int i = 0;

        final ListView listView1 = findViewById(R.id.menu);

        final List<String> cooklist = new ArrayList<>();
        cooklist.add("한식");
        cooklist.add("중식");
        cooklist.add("일식");
        cooklist.add("양식");
        cooklist.add("간식만들기");
        final ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_multiple_choice, cooklist);

        //art어댑터 시작
        final ListView listView2 = findViewById(R.id.menu);

        final List<String> artlist = new ArrayList<>();
        artlist.add("캘리그라피");
        artlist.add("일반미술");
        artlist.add("페인팅아트");
        artlist.add("포토샵");

        final ArrayAdapter adapter2 = new ArrayAdapter(this,
                android.R.layout.simple_list_item_multiple_choice, artlist);

        //music어댑터 시작
        final ListView listView3 = findViewById(R.id.menu);

        final List<String> musiclist = new ArrayList<>();
        musiclist.add("보컬");
        musiclist.add("기타");
        musiclist.add("드럼");
        musiclist.add("바이올린");
        musiclist.add("피아노");
        musiclist.add("플룻");
        musiclist.add("작곡");
        musiclist.add("국악");
        final ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_multiple_choice, musiclist);

        //sport어댑터 시작
        final ListView listView4 = findViewById(R.id.menu);

        final List<String> sportslist = new ArrayList<>();
        sportslist.add("축구");
        sportslist.add("농구");
        sportslist.add("탁구");
        sportslist.add("마라톤");
        sportslist.add("헬스");
        final ArrayAdapter<String> adapter4 = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_multiple_choice, sportslist);


        //취미 버튼 불러오기
        hobby = (RadioGroup) findViewById(R.id.hobby);
        cook = findViewById(R.id.cook);
        sports = findViewById(R.id.sport);
        art = findViewById(R.id.art);
        music = findViewById(R.id.music);


        //취미에 대한 오른쪽 리스트
        cook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView1.setAdapter(adapter1);
                Log.d(String.valueOf(listView1.isSelected()),"selectcook" );
                listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (listView1.isItemChecked(position) == true) {
                            listView1.setItemChecked(position, true);
                            Log.d(String.valueOf(position), "check확인");
                            userHobby.add(cooklist.get(position));
                            Log.d(String.valueOf(userHobby),"userhobby");
                        }else {
                            Log.d(String.valueOf(listView1.isItemChecked(position)), "changetrue");
                            listView1.setItemChecked(position, false);
                            Log.d(String.valueOf(position),"changeflase" );
                            userHobby.remove(cooklist.get(position));
                            Log.d(String.valueOf(userHobby),"userhobby");
                        }
                    }
                });
            }
        });

        art.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView2.setAdapter(adapter2);
                Log.d(String.valueOf(listView2.isSelected()),"selectart" );
                listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (listView2.isItemChecked(position) == true) {
                            listView2.setItemChecked(position, true);
                            Log.d(String.valueOf(position), "check확인");
                            userHobby.add(artlist.get(position));
                            Log.d(String.valueOf(userHobby),"userhobby");
                        }else {
                            Log.d(String.valueOf(listView2.isItemChecked(position)), "changetrue");
                            listView2.setItemChecked(position, false);
                            Log.d(String.valueOf(position),"changeflase" );
                            userHobby.remove(artlist.get(position));
                            Log.d(String.valueOf(userHobby),"userhobby");
                        }
                    }
                });
            }
        });
        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView3.setAdapter(adapter3);
                Log.d(String.valueOf(listView3.isSelected()),"selectmsuic" );
                listView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (listView3.isItemChecked(position) == true) {
                            listView3.setItemChecked(position, true);
                            Log.d(String.valueOf(position), "check확인");
                            userHobby.add(musiclist.get(position));
                            Log.d(String.valueOf(userHobby),"userhobby");
                        }else {
                            Log.d(String.valueOf(listView3.isItemChecked(position)), "changetrue");
                            listView3.setItemChecked(position, false);
                            Log.d(String.valueOf(position),"changeflase" );
                            userHobby.remove(musiclist.get(position));
                            Log.d(String.valueOf(userHobby),"userhobby");
                        }
                    }
                });
            }
        });
        sports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView4.setAdapter(adapter4);
                Log.d(String.valueOf(listView4.isSelected()),"selectsports" );
                listView4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (listView4.isItemChecked(position) == true) {
                            listView4.setItemChecked(position, true);
                            Log.d(String.valueOf(position), "check확인");
                            userHobby.add(sportslist.get(position));
                            Log.d(String.valueOf(userHobby),"userhobby");
                        }else {
                            Log.d(String.valueOf(listView4.isItemChecked(position)), "changetrue");
                            listView4.setItemChecked(position, false);
                            Log.d(String.valueOf(position),"changeflase" );
                            userHobby.remove(cooklist.get(position));
                            Log.d(String.valueOf(userHobby),"userhobby");
                        }
                    }
                });
            }
        });

        //final String texthobby= new String(String.valueOf(userHobby1));

        Button imageButton = (Button) findViewById(R.id.selectClear);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String selectHobby = "";
                for(int i = 0; i < userHobby.size(); i++){
                    if(i<userHobby.size()-1) {
                        selectHobby = selectHobby + userHobby.get(i) + ",";
                    }
                    else if(i==userHobby.size()-1){
                        selectHobby = selectHobby + userHobby.get(i);
                    }
                }


                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                intent.putExtra("selectHobby", selectHobby);
                intent.putStringArrayListExtra("userHobby", (ArrayList<String>) userHobby);
                intent.putExtra("et_id", et_id);
                intent.putExtra("et_password", et_password);
                intent.putExtra("et_name", et_name);
                intent.putExtra("et_age", et_age);
                intent.putExtra("et_phone", et_phone);
                intent.putExtra("et_load", et_load);
                intent.putExtra("et_addr", et_addr);
                startActivity(intent);
            }
        });
    }
}
