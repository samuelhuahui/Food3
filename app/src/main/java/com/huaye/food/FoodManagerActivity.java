package com.huaye.food;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.huaye.food.bean.Food;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class FoodManagerActivity extends AppCompatActivity {
    private RecyclerView allFoodRv;
    private BmobQuery<Food> mQuery;
    private FoodRvAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_manager);
        allFoodRv = findViewById(R.id.all_food);
        mAdapter = new FoodRvAdapter(false);
        allFoodRv.setLayoutManager(new LinearLayoutManager(this));
        allFoodRv.setAdapter(mAdapter);
        mQuery = new BmobQuery<>();
        mQuery.findObjects(new FindListener<Food>() {
            @Override
            public void done(List<Food> list, BmobException e) {
                mAdapter.setNewData(list);
            }
        });

        mAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, final int position) {
                final Food food = (Food) adapter.getItem(position);
                AlertDialog.Builder normalDialog = new AlertDialog.Builder(FoodManagerActivity.this);
                normalDialog.setIcon(R.mipmap.ic_launcher);
                normalDialog.setTitle("Warning");
                normalDialog.setMessage("确定删除吗?");
                normalDialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Food delFood = new Food();
                                delFood.delete(food.getObjectId(), new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            mAdapter.remove(position);
                                            Toast.makeText(FoodManagerActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(FoodManagerActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                normalDialog.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
                normalDialog.show();
                return false;
            }
        });
    }
}
