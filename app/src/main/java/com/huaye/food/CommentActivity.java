package com.huaye.food;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.huaye.food.bean.Comment;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class CommentActivity extends Activity{

	private ListView commentLv;
	private Button send, right;
	private EditText content;
	private String id;
	private CommentAdapter adapter;
	private BmobQuery<Comment> query;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);
		
		id = getIntent().getStringExtra("id");
		commentLv = (ListView) findViewById(R.id.commentList);
		send = (Button) findViewById(R.id.send);
		right = (Button) findViewById(R.id.right);
		content = (EditText) findViewById(R.id.content);
		query = new BmobQuery<Comment>();
		query.addWhereEqualTo("foodId", id);
		
		adapter = new CommentAdapter(this);
		commentLv.setAdapter(adapter);
		
		send.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Comment comment = new Comment();
				comment.setFoodId(id);
				comment.setContent(content.getText().toString());
				comment.setUser(BmobUser.getCurrentUser());
				comment.save(new SaveListener<String>() {
					@Override
					public void done(String s, BmobException e) {
						if(e == null){
							query.findObjects(new FindListener<Comment>() {
								@Override
								public void done(List<Comment> list, BmobException e) {
									if (e == null){
										adapter.setDatas(list);
									}
								}
							});
						}
					}
				});
			}
		});
		
		right.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CommentActivity.this.finish();
			}
		});
	}
	
	@Override
	protected void onStart() {
		query.include("user");
		query.findObjects(new FindListener<Comment>() {
			@Override
			public void done(List<Comment> list, BmobException e) {
				if (e == null){
					adapter.setDatas(list);
				}
			}
		});

		super.onStart();
	}
}
