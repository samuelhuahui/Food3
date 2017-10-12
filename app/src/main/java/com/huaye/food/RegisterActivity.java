package com.huaye.food;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends Activity {
    private Button register;
    private EditText name, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register = (Button) findViewById(R.id.register);
        name = (EditText) findViewById(R.id.name);
        password = (EditText) findViewById(R.id.password);

        register.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                BmobUser user = new BmobUser();
                user.setUsername(name.getText().toString());
                user.setPassword(password.getText().toString());

                user.signUp(new SaveListener<BmobUser>() {
                    @Override
                    public void done(BmobUser bmobUser, BmobException e) {
                        if (e == null) {
                            Toast.makeText(RegisterActivity.this, "Register Success", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

}
