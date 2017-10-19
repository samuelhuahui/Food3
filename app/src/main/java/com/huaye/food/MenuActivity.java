package com.huaye.food;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

import com.huaye.food.bean.Score;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

import static com.huaye.food.R.id.btn1;
import static com.huaye.food.R.id.btn2;
import static com.huaye.food.R.id.btn3;
import static com.huaye.food.R.id.btn4;

public class MenuActivity extends Activity implements RadioGroup.OnCheckedChangeListener {
    private RadioGroup typeRg;
    private TextView name, ave;
    private RatingBar scoreRb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        scoreRb = (RatingBar) findViewById(R.id.score);
        typeRg = (RadioGroup) findViewById(R.id.type);
        name = (TextView) findViewById(R.id.name);
        ave = (TextView) findViewById(R.id.ave);

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
    }

    private BmobQuery<Score> query;

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
        Intent intent = new Intent();
        intent.setClass(MenuActivity.this, FoodListActivity.class);
        switch (checkedId) {
            case btn1:
                break;
            case btn2:
                break;
            case btn3:
                break;
            case btn4:
                break;
        }
    }
}
