package com.huaye.food;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.huaye.food.bean.Caleras;
import com.huaye.food.bean.Food;
import com.huaye.food.bean.Score;
import com.huaye.food.fragment.PlateFragment;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

public class MenuActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    private RecyclerView foodRv;
    private RadioGroup typeRg;
    private TextView name, ave;
    private RatingBar scoreRb;
    private BmobQuery<Score> query;
    private BmobQuery<Food> queryFood;
    private FoodRvAdapter foodAdapter;
    private ImageView expandImg;
    private PlateFragment plateFragment;
    private TextView countTxt;
    private TextView eatTxt;
    private float intakeCal;
    private AVLoadingIndicatorView loading;
    private ArrayList<Food> foods = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        loading = findViewById(R.id.loading);
        foodRv = (RecyclerView) findViewById(R.id.foodRv);
        scoreRb = (RatingBar) findViewById(R.id.score);
        typeRg = (RadioGroup) findViewById(R.id.type);
        name = (TextView) findViewById(R.id.name);
        ave = (TextView) findViewById(R.id.ave);
        expandImg = (ImageView) findViewById(R.id.expand);
        countTxt = (TextView) findViewById(R.id.count);
        eatTxt = (TextView) findViewById(R.id.eat);

        name.setText(getIntent().getStringExtra("name"));
        query = new BmobQuery<Score>();
        query.average(new String[]{"scroe"});
        query.addWhereEqualTo("restaurant", getIntent().getStringExtra("name"));


        typeRg.setOnCheckedChangeListener(this);

        scoreRb.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Score score = new Score();
                score.setRestaurant(getIntent().getStringExtra("name"));
                score.setScroe(rating);
                score.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            query.findStatistics(Score.class, new QueryListener<JSONArray>() {
                                @Override
                                public void done(JSONArray jsonArray, BmobException e) {
                                    if (e == null) {
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

        queryFood = new BmobQuery<>();
        int week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        if (Calendar.getInstance().getFirstDayOfWeek() == Calendar.SUNDAY){
            if (week == 1){
                week = 7;
            } else {
                week = week - 1;
            }
        }
        queryFood.addWhereContainedIn("week", Arrays.asList(week, 8));
        queryFood.addWhereEqualTo("restaurantId", Const.currentR);

        foodAdapter = new FoodRvAdapter(true);
        foodRv.setLayoutManager(new LinearLayoutManager(this));
        foodRv.setAdapter(foodAdapter);
        loading.show();
        BmobQuery<Food> query = new BmobQuery<>();

        List<BmobQuery<Food>> andQuerys = new ArrayList<BmobQuery<Food>>();
        andQuerys.add(queryFood);
        query.and(andQuerys);
        query.findObjects(new FindListener<Food>() {
            @Override
            public void done(List<Food> list, BmobException e) {
                foodAdapter.setNewData(list);
                loading.hide();
            }
        });
        initListener();
    }


    private void initListener() {
        foodAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Food food = (Food) adapter.getItem(position);
                foods.add(food);
                countTxt.setVisibility(View.VISIBLE);
                countTxt.setText(foods.size() + "");
            }
        });

        expandImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plateFragment = PlateFragment.newInstance(foods);
                plateFragment.show(getSupportFragmentManager(), "plate");
            }
        });

        eatTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float total = 0;
                for (Food food : foods) {
                    total += food.getCalories();
                }
                Caleras caleras = new Caleras();
                caleras.setUser(BmobUser.getCurrentUser());
                caleras.setCal(total);

                caleras.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        foods.clear();
                        countTxt.setVisibility(View.GONE);
                        Toast.makeText(MenuActivity.this, "Success", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        query.findStatistics(Score.class, new QueryListener<JSONArray>() {
            @Override
            public void done(JSONArray jsonArray, BmobException e) {
                if (e == null) {
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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        BmobQuery<Food> query = new BmobQuery<>();
        Intent intent = new Intent();
        intent.setClass(MenuActivity.this, FoodListActivity.class);
        loading.show();
        switch (checkedId) {
            case R.id.btn0:
                getIntake();
                break;
            case R.id.btn1:
                query.addWhereContainedIn("type", Arrays.asList(0, 4));
                break;
            case R.id.btn2:
                query.addWhereContainedIn("type", Arrays.asList(1, 4));
                break;
            case R.id.btn3:
                query.addWhereContainedIn("type", Arrays.asList(2, 4));
                break;
            case R.id.btn4:
                query.addWhereContainedIn("type", Arrays.asList(3, 4));
                break;
        }
        List<BmobQuery<Food>> andQuerys = new ArrayList<BmobQuery<Food>>();
        andQuerys.add(queryFood);
        query.and(andQuerys);
        query.findObjects(new FindListener<Food>() {
            @Override
            public void done(List<Food> list, BmobException e) {
                foodAdapter.setNewData(list);
                loading.hide();
            }
        });

    }

    private void getIntake() {
        BmobQuery<Caleras> query = new BmobQuery<Caleras>();
        List<BmobQuery<Caleras>> and = new ArrayList<BmobQuery<Caleras>>();
        //大于00：00：00
        BmobQuery<Caleras> q1 = new BmobQuery<Caleras>();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        final String dateStr = df.format(new Date());

        String start = dateStr + " 00:00:00";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(start);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        q1.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(date));
        and.add(q1);
        //小于23：59：59
        BmobQuery<Caleras> q2 = new BmobQuery<Caleras>();
        String end = dateStr + " 23:59:59";
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = null;
        try {
            date1 = sdf1.parse(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        q2.addWhereLessThanOrEqualTo("createdAt", new BmobDate(date1));
        and.add(q2);
        //添加复合与查询
        query.and(and);
        query.addWhereEqualTo("user", BmobUser.getCurrentUser());
        query.findObjects(new FindListener<Caleras>() {
            @Override
            public void done(List<Caleras> list, BmobException e) {
                float cal = 0;
                for (int i = 0; list != null && i < list.size(); i++) {
                    cal += list.get(i).getCal();
                }

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                SharedPreferences sharedPreferences = getSharedPreferences("data", Activity.MODE_PRIVATE);
                int stepCount = sharedPreferences.getInt("step_count_" + df.format(new Date()), 0);
                float consumeCal = stepCount / 20.0f;
                if (Math.abs(cal - consumeCal) < 400) {
                    queryFood.addWhereEqualTo("calorieLevel", 1);
                } else if (cal - consumeCal < -400) {
                    queryFood.addWhereEqualTo("calorieLevel", 0);
                } else {
                    queryFood.addWhereEqualTo("calorieLevel", 2);
                }

                queryFood.findObjects(new FindListener<Food>() {
                    @Override
                    public void done(List<Food> list, BmobException e) {
                        foodAdapter.setNewData(list);
                    }
                });
            }
        });
    }
}
