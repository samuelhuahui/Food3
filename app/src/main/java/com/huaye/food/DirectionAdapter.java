package com.huaye.food;

import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huaye.food.bean.Direction;

import java.util.ArrayList;

/**
 * Created by sunhuahui on 2017/10/11.
 */

public class DirectionAdapter extends BaseQuickAdapter<Direction.Route, BaseViewHolder> {
    private int mWidth;
    private int mPosition;

    public DirectionAdapter(ArrayList<Direction.Route> data, int width, int position) {
        super(R.layout.item_detail, data);
        mWidth = width;
        mPosition = position;
    }

    @Override
    protected void convert(BaseViewHolder helper, Direction.Route item) {
        helper.setText(R.id.time, item.legs.get(0).duration.text)
                .setText(R.id.dis, item.legs.get(0).distance.value + "m");

        if (helper.getAdapterPosition() == mPosition) {
            helper.setText(R.id.program, "Recommend");
            helper.setBackgroundRes(R.id.bottom_detail, R.drawable.item_bg);
            helper.setBackgroundColor(R.id.program, Color.parseColor("#4182FF"));
            helper.setTextColor(R.id.program, Color.parseColor("#FFFFFF"));
        } else {
            helper.setText(R.id.program, "Way" + (helper.getAdapterPosition() + 1));
            helper.setBackgroundRes(R.id.bottom_detail, R.drawable.item_bg_p);
            helper.setBackgroundColor(R.id.program, Color.parseColor("#E8E8E8"));
            helper.setTextColor(R.id.program, Color.parseColor("#838383"));
        }
        FrameLayout root = helper.getView(R.id.root);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(mWidth / mData.size(), ViewGroup.LayoutParams.MATCH_PARENT);
        root.setLayoutParams(params);
    }

    public void setRecommend(int position) {
        mPosition = position;
        notifyDataSetChanged();
    }
}
