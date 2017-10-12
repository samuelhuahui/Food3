package com.huaye.food.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huaye.food.CommentAdapter;
import com.huaye.food.R;
import com.huaye.food.bean.Comment;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by sunhuahui on 2017/10/12.
 */

public class MyFragment extends Fragment {
    private TextView username;
    private ListView list;
    private CommentAdapter adapter;
    private Button exit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        username = (TextView) view.findViewById(R.id.username);
        list = (ListView) view.findViewById(R.id.list);
        exit = (Button) view.findViewById(R.id.exit);

        username.setText(BmobUser.getCurrentUser().getUsername());

        adapter = new CommentAdapter(getContext());
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
