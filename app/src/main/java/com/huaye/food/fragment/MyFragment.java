package com.huaye.food.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huaye.food.AddFoodActivity;
import com.huaye.food.FoodManagerActivity;
import com.huaye.food.R;
import com.huaye.food.WebViewActivity;
import com.huaye.food.bean.Caleras;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bluemobi.dylan.step.activity.StepActivity;
import cn.bluemobi.dylan.step.msg.MessageEvent;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by sunhuahui on 2017/10/12.
 */

public class MyFragment extends Fragment {
    private TextView username, consume, intake;
    private LinearLayout exit, step, about, add, foodManagerLv;
    private int stepCount;

    public static MyFragment newInstance(int stepCount) {
        MyFragment f = new MyFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("step_count", stepCount);
        f.setArguments(bundle);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        stepCount = getArguments().getInt("step_count");
        EventBus.getDefault().register(this);
        username = (TextView) view.findViewById(R.id.username);
        exit = (LinearLayout) view.findViewById(R.id.exit);
        step = (LinearLayout) view.findViewById(R.id.step);
        about = (LinearLayout) view.findViewById(R.id.about);
        foodManagerLv = view.findViewById(R.id.food_manager);
        add = (LinearLayout) view.findViewById(R.id.add_food);
        consume = (TextView) view.findViewById(R.id.consume);
        intake = (TextView) view.findViewById(R.id.intake);
        username.setText(BmobUser.getCurrentUser().getUsername());

        if (!"5d01e4530a".equalsIgnoreCase(BmobUser.getCurrentUser().getObjectId())){
            add.setVisibility(View.GONE);
            foodManagerLv.setVisibility(View.GONE);
        }
        step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), StepActivity.class));
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getContext(), WebViewActivity.class);
                startActivity(intent);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddFoodActivity.class));
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Warnning!");
                builder.setMessage("Confirm exit?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BmobUser.logOut();
                        Toast.makeText(getContext(), "Exit", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });

        foodManagerLv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), FoodManagerActivity.class));
            }
        });

        if (stepCount > 0){
            float kcal = stepCount / 20.0f;
            DecimalFormat format = new DecimalFormat("#0.00");
            consume.setText(format.format(kcal));
        }
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        getIntake();
        super.onResume();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        float kcal = event.stepCount / 20.0f;
        DecimalFormat format = new DecimalFormat("#0.00");
        consume.setText(format.format(kcal));
    }

    private void getIntake() {
        BmobQuery<Caleras> query = new BmobQuery<Caleras>();
        List<BmobQuery<Caleras>> and = new ArrayList<BmobQuery<Caleras>>();
        //大于00：00：00
        BmobQuery<Caleras> q1 = new BmobQuery<Caleras>();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        String dateStr = df.format(new Date());

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

                intake.setText(cal + "");
            }
        });
    }
}
