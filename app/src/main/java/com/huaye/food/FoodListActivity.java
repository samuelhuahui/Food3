package com.huaye.food;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.huaye.food.bean.Food;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class FoodListActivity extends Activity {
	private TextView date_time;
	private ListView list;
	private TextView name;
	private TextView time_dis;
	private int type = 0;
	private ImageView add;
	private Button right;
	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_food_list);
		list = (ListView) findViewById(R.id.list);
		name = (TextView) findViewById(R.id.name);
		right = (Button) findViewById(R.id.right);
		time_dis = (TextView) findViewById(R.id.time_dis);
		date_time = (TextView) findViewById(R.id.date_time);
		add = (ImageView) findViewById(R.id.add);
		type = getIntent().getIntExtra("type", 0);
		query = new BmobQuery<Food>();
		Calendar calendar = Calendar.getInstance();
		int week =	calendar.get(Calendar.DAY_OF_WEEK);
		query.addWhereEqualTo("week", week);
		switch (type) {
		case 0:
			query.addWhereEqualTo("type", 0);
			time_dis.setText("7:00~10:00");
			name.setText("Breakfast");
			break;
		case 1:
			query.addWhereEqualTo("type", 1);
			time_dis.setText("11:00~13:30");
			name.setText("Lunch");
			break;
		case 2:
			query.addWhereEqualTo("type", 2);
			time_dis.setText("17:00~19:30");
			name.setText("Dinner");
			break;
		case 3:
			query.addWhereEqualTo("type", 3);
			time_dis.setText("21:00~22:30");
			name.setText("Lave night");
			break;
		}
		adapter = new FoodAdapter(this);
		list.setAdapter(adapter);
		query.addWhereEqualTo("restaurantId", Const.currentR);

		handler.postDelayed(runnable, 10);

		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(FoodListActivity.this, AddFoodActivity.class));
			}
		});
		
		right.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

	}

	@Override
	protected void onStart() {
		query.findObjects(new FindListener<Food>() {
			@Override
			public void done(List<Food> list, BmobException e) {
				if (e == null){
					adapter.setDatas(list);
				}
			}
		});
		super.onStart();
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
	private FoodAdapter adapter;
	private BmobQuery<Food> query;

	@Override
	protected void onDestroy() {
		handler.removeCallbacks(runnable);
		super.onDestroy();
	}
}
