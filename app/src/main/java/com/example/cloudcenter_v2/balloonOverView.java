package com.example.cloudcenter_v2;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class balloonOverView extends FrameLayout {

    private LinearLayout layout;
    private TextView title, snip;
    protected int deviceNum;
    protected int balloonBottomOffset = 18;

    public balloonOverView(Context context){
        super(context);
        setPadding(10, 0, 10, balloonBottomOffset);
        layout = new LinearLayout(context);
        layout.setVisibility(VISIBLE);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.balloon_overlay, layout);
        title = (TextView) v.findViewById(R.id.balloon_title);
        snip = (TextView) v.findViewById(R.id.balloon_snip);
        ImageView goAcademy = (ImageView) v.findViewById(R.id.btn_GoAcademy);

        goAcademy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.NO_GRAVITY;
        addView(layout, params);
    }
}
