package com.huaye.food;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.wei.android.lib.fingerprintidentify.FingerprintIdentify;
import com.wei.android.lib.fingerprintidentify.base.BaseFingerprint;

public class FingerprintIdentifyActivity extends AppCompatActivity {
    private FingerprintIdentify mFingerprintIdentify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_identify);
        fingerprintIdentify();
    }

    private void fingerprintIdentify() {
        mFingerprintIdentify = new FingerprintIdentify(this);
        mFingerprintIdentify.isFingerprintEnable();
        mFingerprintIdentify.isHardwareEnable();                                    // 指纹硬件是否可用
        mFingerprintIdentify.isRegisteredFingerprint();
        if (mFingerprintIdentify.isFingerprintEnable() && mFingerprintIdentify.isHardwareEnable() && mFingerprintIdentify.isRegisteredFingerprint()) {
            mFingerprintIdentify.startIdentify(3, new BaseFingerprint.FingerprintIdentifyListener() {
                @Override
                public void onSucceed() {
                    startActivity(new Intent(FingerprintIdentifyActivity.this, HomeActivity.class));
                    finish();
                }

                @Override
                public void onNotMatch(int availableTimes) {
                    // not match, try again automatically
                }

                @Override
                public void onFailed(boolean isDeviceLocked) {
                    // failed, release hardware automatically
                    // isDeviceLocked: is device locked temporarily
                }

                @Override
                public void onStartFailedByDeviceLocked() {
                    // the first start failed because the device was locked temporarily
                }
            });
        } else {
            startActivity(new Intent(FingerprintIdentifyActivity.this, HomeActivity.class));
            finish();
        }

    }
}
