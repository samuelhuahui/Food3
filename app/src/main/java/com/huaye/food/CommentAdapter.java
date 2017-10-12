package com.huaye.food;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.huaye.food.bean.Comment;

public class CommentAdapter extends BaseAdapter {
	private List<Comment> list;
	private LayoutInflater mInflater;

	public CommentAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		list = new ArrayList<Comment>();
	}

	public void setDatas(List<Comment> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Comment getItem(int position) {
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
		ViewHolder holder;
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_comment, null);

			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.username = (TextView) convertView.findViewById(R.id.username);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.time.setText(list.get(position).getCreatedAt());
		holder.content.setText(list.get(position).getContent());
		if (list.get(position).getUser() != null)
			holder.username.setText(list.get(position).getUser().getUsername());
		return convertView;
	}

	public static class ViewHolder {
		private TextView time;
		private TextView content;
		private TextView username;
	}
}
