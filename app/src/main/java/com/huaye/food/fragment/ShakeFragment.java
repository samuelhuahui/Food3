package com.huaye.food.fragment;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.huaye.food.FoodRvAdapter;
import com.huaye.food.MarkerActivity;
import com.huaye.food.R;
import com.huaye.food.bean.Caleras;
import com.huaye.food.bean.Food;
import com.wang.avi.AVLoadingIndicatorView;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShakeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShakeFragment extends Fragment implements SensorEventListener {
    private static final String ARG_STEP_COUNT = "step_count";
    private static final LatLng SOUTH_DINNING_HALL = new LatLng(41.6994831, -86.2413696);
    private static final LatLng NORTH_DINNING_HALL = new LatLng(41.7044217, -86.2359478);
    private static final LatLng LIBRARY_CAFE = new LatLng(41.7023435, -86.2340916);
    private static final LatLng GRACE_HALL = new LatLng(41.7048248, -86.2339147);

    private static final String TAG = "MainActivity";
    private static final int START_SHAKE = 0x1;
    private static final int AGAIN_SHAKE = 0x2;
    private static final int END_SHAKE = 0x3;

    private SensorManager mSensorManager;
    private Sensor mAccelerometerSensor;
    private Vibrator mVibrator;//手机震动
    private SoundPool mSoundPool;//摇一摇音效

    //记录摇动状态
    private boolean isShake = false;

    private LinearLayout mRootLv;
    private LinearLayout mTopLayout;
    private LinearLayout mBottomLayout;
    private ImageView mTopLine;
    private ImageView mBottomLine;

    private MyHandler mHandler;
    private int mWeiChatAudio;

    private RecyclerView mFoodRv;
    private FoodRvAdapter mAdapter;
    private int mStepCount;
    private BmobQuery queryFood;

    private AVLoadingIndicatorView loading;
    public ShakeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param stepCount Parameter 1.
     * @return A new instance of fragment ShakeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShakeFragment newInstance(int stepCount) {
        ShakeFragment fragment = new ShakeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_STEP_COUNT, stepCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStepCount = getArguments().getInt(ARG_STEP_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shake, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initView(view);
        initData();
        initSensor();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
        super.onDestroyView();
    }

    private void initView(View view) {
        loading = view.findViewById(R.id.loading);
        mTopLayout = view.findViewById(R.id.main_linear_top);
        mBottomLayout = view.findViewById(R.id.main_linear_bottom);
        mTopLine = view.findViewById(R.id.main_shake_top_line);
        mBottomLine = view.findViewById(R.id.main_shake_bottom_line);
        mRootLv = view.findViewById(R.id.root);
        //默认
        mTopLine.setVisibility(View.GONE);
        mBottomLine.setVisibility(View.GONE);

        mFoodRv = view.findViewById(R.id.food_rv);
        mAdapter = new FoodRvAdapter(false);
        mFoodRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mFoodRv.setAdapter(mAdapter);

        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Food food = (Food) adapter.getItem(position);
                Intent intent = new Intent(getContext(), MarkerActivity.class);
                intent.putExtra("id", food.getRestaurantId());
                startActivity(intent);
            }
        });
    }

    private void initData() {
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
        mHandler = new MyHandler(this);

        //初始化SoundPool
        mSoundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 5);
        mWeiChatAudio = mSoundPool.load(getContext(), R.raw.weichat_audio, 1);

        //获取Vibrator震动服务
        mVibrator = (Vibrator) getContext().getSystemService(Activity.VIBRATOR_SERVICE);
    }

    private void initSensor() {
        //获取 SensorManager 负责管理传感器
        mSensorManager = ((SensorManager) getContext().getSystemService(Activity.SENSOR_SERVICE));
        if (mSensorManager != null) {
            //获取加速度传感器
            mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (mAccelerometerSensor != null) {
                mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_UI);
            }
        }
    }

    private void getIntake() {
        final BmobQuery<Caleras> query = new BmobQuery<Caleras>();
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
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("data", Activity.MODE_PRIVATE);
                int stepCount = sharedPreferences.getInt("step_count_" + df.format(new Date()), 0);
                float consumeCal = stepCount / 20.0f;

                BmobQuery<Food> query = new BmobQuery<>();
                List<BmobQuery<Food>> andQuerys = new ArrayList<BmobQuery<Food>>();
                andQuerys.add(queryFood);
                query.and(andQuerys);
                if (Math.abs(cal - consumeCal) < 400 && cal - consumeCal > -400) {
                    query.addWhereEqualTo("calorieLevel", 1);
                } else if (cal - consumeCal > 400) {
                    query.addWhereEqualTo("calorieLevel", 0);
                } else {
                    query.addWhereEqualTo("calorieLevel", 2);
                }

                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                if (hour > 6 && hour <= 11) {
                    query.addWhereContainedIn("type", Arrays.asList(0, 4));
                } else if (hour > 11 && hour <= 16) {
                    query.addWhereContainedIn("type", Arrays.asList(1, 4));
//                    query.addWhereEqualTo("type", 1);
                } else if (hour > 16 && hour <= 20) {
                    query.addWhereContainedIn("type", Arrays.asList(2, 4));
//                    query.addWhereEqualTo("type", 2);
                } else {
                    query.addWhereContainedIn("type", Arrays.asList(3, 4));
//                    query.addWhereEqualTo("type", 3);
                }
                query.findObjects(new FindListener<Food>() {
                    @Override
                    public void done(List<Food> list, BmobException e) {

                        if (list.size() <= 5){
                            mAdapter.setNewData(list);
                        } else {
                            List<Food> subList = new ArrayList<>();
                            Random random = new Random();
                            ArrayList<Integer> ls = new ArrayList<Integer>();

                            for(int i = 0; i < 5;i++){
                                int number = random.nextInt(list.size());

                                if(!ls.contains(number)){
                                    ls.add(number);
                                    subList.add(list.get(number));
                                } else {
                                    i--;
                                }
                            }

                            mAdapter.setNewData(subList);
                        }

                        loading.hide();
                    }
                });
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();

        if (type == Sensor.TYPE_ACCELEROMETER) {
            //获取三个方向值
            float[] values = event.values;
            float x = values[0];
            float y = values[1];
            float z = values[2];

            if ((Math.abs(x) > 17 || Math.abs(y) > 17 || Math
                    .abs(z) > 17) && !isShake) {
                isShake = true;
                mRootLv.setVisibility(View.VISIBLE);
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            Log.d(TAG, "onSensorChanged: 摇动");

                            //开始震动 发出提示音 展示动画效果
                            mHandler.obtainMessage(START_SHAKE).sendToTarget();
                            Thread.sleep(500);
                            //再来一次震动提示
                            mHandler.obtainMessage(AGAIN_SHAKE).sendToTarget();
                            Thread.sleep(500);
                            mHandler.obtainMessage(END_SHAKE).sendToTarget();


                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private static class MyHandler extends Handler {
        private WeakReference<ShakeFragment> mReference;
        private ShakeFragment mFragment;

        public MyHandler(ShakeFragment fragment) {
            mReference = new WeakReference<>(fragment);
            if (mReference != null) {
                mFragment = mReference.get();
            }
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_SHAKE:
                    //This method requires the caller to hold the permission VIBRATE.
                    mFragment.mVibrator.vibrate(300);
                    //发出提示音
                    mFragment.mSoundPool.play(mFragment.mWeiChatAudio, 1, 1, 0, 0, 1);
                    mFragment.mTopLine.setVisibility(View.VISIBLE);
                    mFragment.mBottomLine.setVisibility(View.VISIBLE);
                    mFragment.startAnimation(false);//参数含义: (不是回来) 也就是说两张图片分散开的动画
                    break;
                case AGAIN_SHAKE:
                    mFragment.mVibrator.vibrate(300);
                    break;
                case END_SHAKE:
                    //整体效果结束, 将震动设置为false
                    mFragment.isShake = false;
                    // 展示上下两种图片回来的效果
                    mFragment.startAnimation(true);
                    break;
            }
        }
    }

    /**
     * 开启 摇一摇动画
     *
     * @param isBack 是否是返回初识状态
     */
    private void startAnimation(boolean isBack) {
        //动画坐标移动的位置的类型是相对自己的
        int type = Animation.RELATIVE_TO_SELF;

        float topFromY;
        float topToY;
        float bottomFromY;
        float bottomToY;
        if (isBack) {
            topFromY = -0.5f;
            topToY = 0;
            bottomFromY = 0.5f;
            bottomToY = 0;
        } else {
            topFromY = 0;
            topToY = -0.5f;
            bottomFromY = 0;
            bottomToY = 0.5f;
        }

        //上面图片的动画效果
        TranslateAnimation topAnim = new TranslateAnimation(
                type, 0, type, 0, type, topFromY, type, topToY
        );
        topAnim.setDuration(200);
        //动画终止时停留在最后一帧~不然会回到没有执行之前的状态
        topAnim.setFillAfter(true);

        //底部的动画效果
        TranslateAnimation bottomAnim = new TranslateAnimation(
                type, 0, type, 0, type, bottomFromY, type, bottomToY
        );
        bottomAnim.setDuration(200);
        bottomAnim.setFillAfter(true);

        //大家一定不要忘记, 当要回来时, 我们中间的两根线需要GONE掉
        if (isBack) {
            bottomAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    loading.show();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    //当动画结束后 , 将中间两条线GONE掉, 不让其占位
                    mTopLine.setVisibility(View.GONE);
                    mBottomLine.setVisibility(View.GONE);
                    mRootLv.setVisibility(View.GONE);
                    getIntake();
                }
            });
        }
        //设置动画
        mTopLayout.startAnimation(topAnim);
        mBottomLayout.startAnimation(bottomAnim);
    }
}
