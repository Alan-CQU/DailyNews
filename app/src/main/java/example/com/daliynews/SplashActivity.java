package example.com.daliynews;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;


/**
 * use for splash UI
 */
public class SplashActivity extends AppCompatActivity {
    //延迟3秒
    private static final long SPLASH_DELAY_MILLIS = 4000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);
        // 使用Handler的postDelayed方法，3秒后执行跳转到MainActivity
        new Handler().postDelayed(new Runnable() {
            public void run() {
                startMainActivity();
            }
        }, SPLASH_DELAY_MILLIS);
    }

    /**
     * start MainActivity
     */
    public void startMainActivity(){
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();

    }

}
