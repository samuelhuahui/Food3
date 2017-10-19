package com.huaye.food;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huaye.food.bean.Food;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

public class FoodAdapter extends BaseAdapter {
    private List<Food> list;
    private LayoutInflater inflater;
    private Context context;

    public FoodAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        list = new ArrayList<Food>();
    }

    public void setDatas(List<Food> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder;
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.item_food, null);
            holder = new ViewHolder();
            holder.foodname = (TextView) convertView.findViewById(R.id.foodName);
            holder.foodprice = (TextView) convertView.findViewById(R.id.foodPrice);
            holder.comment = (ImageView) convertView.findViewById(R.id.comment);
            holder.pic = (ImageView) convertView.findViewById(R.id.pic);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        String name = "Food Name:" + "<font color='red'>" + list.get(position).getName() + "</font>";
        String price = "Food Calories:" + "<font color='red'>$" + list.get(position).getCalories() + "</font>";
        holder.foodname.setText(Html.fromHtml(name));
        holder.foodprice.setText(Html.fromHtml(price));

        holder.comment.setTag(list.get(position).getObjectId());
        holder.comment.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (BmobUser.getCurrentUser() != null) {
                    Intent intent = new Intent(context, CommentActivity.class);
                    intent.putExtra("id", (String) v.getTag());
                    context.startActivity(intent);
                } else {
                    Intent i = new Intent(context, LoginActivity.class);
                    context.startActivity(i);
                }
            }
        });
        Glide.with(context).load(list.get(position).getPic()).into(holder.pic);
        return convertView;
    }

    class ViewHolder {
        public TextView foodname;
        public TextView foodprice;
        public ImageView comment;
        public ImageView pic;
    }

};