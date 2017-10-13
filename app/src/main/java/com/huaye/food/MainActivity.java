package com.huaye.food;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.BmobUser;

public class MainActivity extends Activity implements OnClickListener {

    private Button right, btn1, btn2, btn3, btn4, btn5;
    private TextView date_time;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        date_time = (TextView) findViewById(R.id.date_time);
        right = (Button) findViewById(R.id.right);
        right.setText("Login");
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);
        btn5 = (Button) findViewById(R.id.btn5);
        right.setOnClickListener(this);

        handler.postDelayed(runnable, 10);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        findViewById(R.id.m).setOnClickListener(this);
    }

    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            date_time.setText(dateFormat.format(new Date()));

            handler.postDelayed(this, 1000);
        }
    };

    protected void onStart() {
        BmobUser user = BmobUser.getCurrentUser();
        if (user != null) {
            right.setText("My");
        }
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        if (v instanceof Button){
            Button value = (Button) v;
            intent.putExtra("name", value.getText().toString());
        }
        switch (v.getId()) {
            case R.id.btn1:
                Const.currentR = 0;
                intent.setClass(MainActivity.this, MenuActivity.class);
                startActivity(intent);
                break;
            case R.id.btn2:
                Const.currentR = 1;
                intent.setClass(MainActivity.this, MenuActivity.class);
                startActivity(intent);
                break;
            case R.id.btn3:
                Const.currentR = 2;
                intent.setClass(MainActivity.this, MenuActivity.class);
                startActivity(intent);
                break;
            case R.id.btn4:
                Const.currentR = 3;
                intent.setClass(MainActivity.this, MenuActivity.class);
                startActivity(intent);
                break;
            case R.id.right:
                if (right.getText().equals("Login")) {
                    intent.setClass(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    intent.setClass(MainActivity.this, MyActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.btn5:
                intent.setClass(MainActivity.this, WebViewActivity.class);
                startActivity(intent);
                break;
            case R.id.m:
                intent.setClass(MainActivity.this, MarkerActivity.class);
                startActivity(intent);
                break;
        }

    }

}
