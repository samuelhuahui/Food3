package com.huaye.food;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.huaye.food.fragment.MapFragment;
import com.huaye.food.fragment.MyFragment;
import com.huaye.food.fragment.StoreFragment;

import cn.bmob.v3.BmobUser;

public class HomeActivity extends AppCompatActivity {
    private BottomNavigationView mNavigationView;
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        fm = getSupportFragmentManager();


        fm.beginTransaction().replace(R.id.container, StoreFragment.newInstance("", "")).commit();


        mNavigationView = (BottomNavigationView) findViewById(R.id.nav);
        mNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_store:
                        fm.beginTransaction().replace(R.id.container, StoreFragment.newInstance("", "")).commit();
                        break;
                    case R.id.menu_map:
                        fm.beginTransaction().replace(R.id.container, new MapFragment()).commit();
                        break;
                    case R.id.menu_my:
                        if (BmobUser.getCurrentUser() == null) {
                            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                            startActivity(intent);
                            return false;
                        } else {
                            fm.beginTransaction().replace(R.id.container, new MyFragment()).commit();
                        }
                }
                return true;
            }
        });
    }
}
