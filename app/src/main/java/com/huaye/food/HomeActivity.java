package com.huaye.food;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.huaye.food.fragment.MapFragment;
import com.huaye.food.fragment.MyFragment;
import com.huaye.food.fragment.ShakeFragment;
import com.huaye.food.fragment.StoreFragment;
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify;
import com.wei.android.lib.fingerprintidentify.base.BaseFingerprint;

import org.greenrobot.eventbus.EventBus;

import cn.bluemobi.dylan.step.msg.MessageEvent;
import cn.bluemobi.dylan.step.step.UpdateUiCallBack;
import cn.bluemobi.dylan.step.step.service.StepService;
import cn.bmob.v3.BmobUser;

public class HomeActivity extends AppCompatActivity {
    private BottomNavigationView mNavigationView;
    private FragmentManager fm;
    private boolean isBind = false;
    private int stepCount;
    private FingerprintManagerCompat manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        manager = FingerprintManagerCompat.from(this);
        if (!App.isRunning){
            manager.authenticate(null, 0, new CancellationSignal(), new FingerprintManagerCompat.AuthenticationCallback() {
                @Override
                public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                    App.isRunning = true;
                    super.onAuthenticationSucceeded(result);
                }
            }, null);
        }
        fm = getSupportFragmentManager();

        Intent intent = new Intent(this, StepService.class);
        isBind = bindService(intent, conn, Context.BIND_AUTO_CREATE);
        startService(intent);
        fm.beginTransaction().replace(R.id.container, StoreFragment.newInstance("", "")).commit();


        mNavigationView = (BottomNavigationView) findViewById(R.id.nav);
        mNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_store:
                        fm.beginTransaction().replace(R.id.container, StoreFragment.newInstance("", "")).commit();
                        break;
                    case R.id.menu_shake:
                        fm.beginTransaction().replace(R.id.container, ShakeFragment.newInstance(stepCount)).commit();
                        break;
                    case R.id.menu_map:
                        fm.beginTransaction().replace(R.id.container, MapFragment.newInstance(stepCount)).commit();
                        break;
                    case R.id.menu_my:
                        if (BmobUser.getCurrentUser() == null) {
                            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                            startActivity(intent);
                            return false;
                        } else {
                            fm.beginTransaction().replace(R.id.container, MyFragment.newInstance(stepCount)).commit();
                        }
                }
                return true;
            }
        });
    }



    /**
     * 用于查询应用服务（application Service）的状态的一种interface，
     * 更详细的信息可以参考Service 和 context.bindService()中的描述，
     * 和许多来自系统的回调方式一样，ServiceConnection的方法都是进程的主线程中调用的。
     */
    ServiceConnection conn = new ServiceConnection() {
        /**
         * 在建立起于Service的连接时会调用该方法，目前Android是通过IBind机制实现与服务的连接。
         * @param name 实际所连接到的Service组件名称
         * @param service 服务的通信信道的IBind，可以通过Service访问对应服务
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            StepService stepService = ((StepService.StepBinder) service).getService();

            stepCount = stepService.getStepCount();
            EventBus.getDefault().post(new MessageEvent(stepService.getStepCount()));
            //设置步数监听回调
            stepService.registerCallback(new UpdateUiCallBack() {
                @Override
                public void updateUi(int stepCount) {
                    HomeActivity.this.stepCount = stepCount;
                }
            });
        }

        /**
         * 当与Service之间的连接丢失的时候会调用该方法，
         * 这种情况经常发生在Service所在的进程崩溃或者被Kill的时候调用，
         * 此方法不会移除与Service的连接，当服务重新启动的时候仍然会调用 onServiceConnected()。
         * @param name 丢失连接的组件名称
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isBind) {
            this.unbindService(conn);
        }
    }
}
