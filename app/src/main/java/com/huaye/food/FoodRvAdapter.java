package com.huaye.food;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huaye.food.bean.Food;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunhuahui on 2017/10/21.
 */

public class FoodRvAdapter extends BaseQuickAdapter<Food, BaseViewHolder> {
    private boolean isShowAdd = true;

    public FoodRvAdapter(@LayoutRes int layoutResId, @Nullable List<Food> data) {
        super(layoutResId, data);
    }

    public FoodRvAdapter(boolean isShowAdd) {
        this(R.layout.item_food_rv, new ArrayList<Food>());
        this.isShowAdd = isShowAdd;
    }

    @Override
    protected void convert(BaseViewHolder helper, Food item) {
        helper.setText(R.id.title, item.getName())
                .setText(R.id.calorie, item.getCalories() + "kcal")
                .addOnClickListener(R.id.add);
        if (isShowAdd) {
            helper.setImageResource(R.id.add, R.drawable.add);
        } else {
            helper.setImageResource(R.id.add, R.drawable.navigation);
        }

        switch (item.getRestaurantId()) {
            case 0:
                helper.setText(R.id.name, "South Dining Hall");
                break;
            case 1:
                helper.setText(R.id.name, "North Dining Hall");
                break;
            case 2:
                helper.setText(R.id.name, "Library Cafe");
                break;
            case 3:
                helper.setText(R.id.name, "Grace Hall");
                break;
        }
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.placeholer)
                .centerCrop();
        Glide.with(helper.itemView.getContext()).load(item.getPic()).apply(requestOptions).into((ImageView) helper.getView(R.id.pic));
    }
}
