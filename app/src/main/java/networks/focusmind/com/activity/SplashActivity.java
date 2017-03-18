package networks.focusmind.com.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import networks.focusmind.com.minion.R;
import networks.focusmind.com.storage.SharedPreferenceManager;

public class SplashActivity extends AppCompatActivity {

    SharedPreferenceManager mSharedpref;
    private Handler mHandler;
    private Runnable mRunnable;

    private static int SPLASH_TIME_OUT = 3000;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.splash_screen);
        mSharedpref = new SharedPreferenceManager(this);
        mHandler = new Handler();
        mRunnable = new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        };
        mHandler.postDelayed(mRunnable, SPLASH_TIME_OUT);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != mHandler) {
            mHandler.removeCallbacks(mRunnable);
        }
    }
}
