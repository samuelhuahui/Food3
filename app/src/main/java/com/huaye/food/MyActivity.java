package com.huaye.food;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huaye.food.bean.Comment;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MyActivity extends Activity{

	private TextView username;
	private ListView list;
	private CommentAdapter adapter;
	private Button exit;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my);
		username = (TextView) findViewById(R.id.username);
		list = (ListView) findViewById(R.id.list);
		exit = (Button) findViewById(R.id.exit);
		
		username.setText(BmobUser.getCurrentUser().getUsername());
		
		adapter = new CommentAdapter(this);
		list.setAdapter(adapter);
		
		BmobQuery<Comment> query = new BmobQuery<Comment>();
		query.addWhereEqualTo("user", BmobUser.getCurrentUser());
		query.findObjects(new FindListener<Comment>() {
			@Override
			public void done(List<Comment> list, BmobException e) {
				if (e == null){
					adapter.setDatas(list);
				}
			}
		});

		exit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity.this);
				builder.setTitle("Warnning!");
				builder.setMessage("Confirm exit?");
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						BmobUser.logOut();
						Toast.makeText(MyActivity.this, "Exit", Toast.LENGTH_SHORT).show();
						MyActivity.this.finish();
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
