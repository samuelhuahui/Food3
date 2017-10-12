package com.huaye.food;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

import com.huaye.food.bean.Score;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

public class MenuActivity extends Activity implements OnClickListener {
	private TextView date_time;
	private Button btn1, btn2, btn3, btn4, right;
	private TextView name, ave;
	private RatingBar scoreRb;
	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);

		date_time = (TextView) findViewById(R.id.date_time);
		scoreRb = (RatingBar) findViewById(R.id.score);
		name = (TextView) findViewById(R.id.name);
		ave = (TextView) findViewById(R.id.ave);
		right = (Button) findViewById(R.id.right);
		btn1 = (Button) findViewById(R.id.btn1);
		btn2 = (Button) findViewById(R.id.btn2);
		btn3 = (Button) findViewById(R.id.btn3);
		btn4 = (Button) findViewById(R.id.btn4);
		name.setText(getIntent().getStringExtra("name"));
		handler.postDelayed(runnable, 10);
		query = new BmobQuery<Score>();
		query.average(new String[]{"scroe"});
		query.addWhereEqualTo("restaurant", getIntent().getStringExtra("name"));
		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
		btn3.setOnClickListener(this);
		btn4.setOnClickListener(this);
		right.setOnClickListener(this);
		scoreRb.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				Score score = new Score();
				score.setRestaurant(getIntent().getStringExtra("name"));
				score.setScroe(rating);
				score.save(new SaveListener<String>() {
					@Override
					public void done(String s, BmobException e) {
						if (e == null){
							query.findStatistics(Score.class, new QueryListener<JSONArray>() {
								@Override
								public void done(JSONArray jsonArray, BmobException e) {
									if (e == null){
										try {
											JSONObject obj = jsonArray.getJSONObject(0);
											double average = obj.getDouble("_avgScroe");//_(关键字)+首字母大写的列名
											BigDecimal bd = new BigDecimal(average);
											bd = bd.setScale(2, RoundingMode.HALF_UP);
											ave.setText(bd.toString() + "''");
										} catch (JSONException ex) {
											ex.printStackTrace();
										}
									}
								}
							});
						}
					}
				});
			}
		});
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
	private BmobQuery<Score> query;

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		query.findStatistics(Score.class, new QueryListener<JSONArray>() {
			@Override
			public void done(JSONArray jsonArray, BmobException e) {
				if (e == null){
					try {
						JSONObject obj = jsonArray.getJSONObject(0);
						double average = obj.getDouble("_avgScroe");//_(关键字)+首字母大写的列名
						BigDecimal bd = new BigDecimal(average);
						bd = bd.setScale(2, RoundingMode.HALF_UP);
						ave.setText(bd.toString() + "''");
					} catch (JSONException ex) {
						ex.printStackTrace();
					}
				}
			}
		});
	}
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		intent.setClass(MenuActivity.this, FoodListActivity.class);
		switch (v.getId()) {
		case R.id.btn1:
			intent.putExtra("type", 0);
			break;
		case R.id.btn2:
			intent.putExtra("type", 1);
			break;
		case R.id.btn3:
			intent.putExtra("type", 2);
			break;
		case R.id.btn4:
			intent.putExtra("type", 3);
			break;
		case R.id.right:
			finish();
			return;
		}
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		handler.removeCallbacks(runnable);
		super.onDestroy();
	}
}
