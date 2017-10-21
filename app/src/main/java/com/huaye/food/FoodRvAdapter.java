package com.huaye.food;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huaye.food.bean.Food;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunhuahui on 2017/10/21.
 */

public class FoodRvAdapter extends BaseQuickAdapter<Food, BaseViewHolder> {

    public FoodRvAdapter(@LayoutRes int layoutResId, @Nullable List<Food> data) {
        super(layoutResId, data);
    }

    public FoodRvAdapter() {
        this(R.layout.item_food_rv, new ArrayList<Food>());
    }

    @Override
    protected void convert(BaseViewHolder helper, Food item) {
        helper.setText(R.id.title, item.getName())
                .setText(R.id.calorie, item.getCalories() + "cal")
                .addOnClickListener(R.id.add);

        Glide.with(helper.itemView.getContext()).load(item.getPic()).centerCrop().into((ImageView) helper.getView(R.id.pic));
    }
}
