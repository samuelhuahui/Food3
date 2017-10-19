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
import com.huaye.food.R;
import com.huaye.food.WebViewActivity;

import cn.bluemobi.dylan.step.activity.StepActivity;
import cn.bmob.v3.BmobUser;

/**
 * Created by sunhuahui on 2017/10/12.
 */

public class MyFragment extends Fragment {
    private TextView username;
    private LinearLayout exit, step, about, add;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        username = (TextView) view.findViewById(R.id.username);
        exit = (LinearLayout) view.findViewById(R.id.exit);
        step = (LinearLayout) view.findViewById(R.id.step);
        about = (LinearLayout) view.findViewById(R.id.about);
        add = (LinearLayout) view.findViewById(R.id.add_food);

        username.setText(BmobUser.getCurrentUser().getUsername());

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
    }
}
