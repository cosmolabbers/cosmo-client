package com.locationlabs.speedbump.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.locationlabs.speedbump.R;
import com.locationlabs.speedbump.services.LockDetectService;
import com.locationlabs.speedbump.utils.ColorUtil;
import com.locationlabs.speedbump.utils.LogUtil;

import org.w3c.dom.Text;

import java.util.concurrent.locks.Lock;

/**
 *
 */
public class LockActivity extends Activity {

    public static boolean isAlive;
    public static boolean isLocked;
    private LockReceiver lockReceiver = new LockReceiver();
    private static IntentFilter lockIntentFilter = new IntentFilter();

    RelativeLayout backgroundLayout;
    TextView scoreText, lockText;
    RadialGradient radialGradient;

    Point screenSize;

    TextToSpeech tts;

    static {
        lockIntentFilter.addAction(LockDetectService.ACTION_UNLOCK);
        lockIntentFilter.addAction(LockDetectService.ACTION_SCORE);
    }

    private TextToSpeech.OnInitListener ttsListener = new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                // woo hoo!
                LogUtil.d("success!");
            } else {
                // initialization failed
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        // You've been locked!
        backgroundLayout = (RelativeLayout) findViewById(R.id.top_layout);
        if (getIntent().getAction().equals(LockDetectService.ACTION_LOCK)) {
            isLocked = true;
        }

        scoreText = (TextView) findViewById(R.id.score_text);

        tts = new TextToSpeech(this, ttsListener);
        Display display = getWindowManager().getDefaultDisplay();
        screenSize = new Point();
        display.getSize(screenSize);
        updateBackgroundColor();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isAlive = true;
        registerReceiver(lockReceiver, lockIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isAlive = false;
        unregisterReceiver(lockReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }

    @Override
    public void onBackPressed() {
        if (!isLocked) {
            super.onBackPressed();
        }
    }

    private void unlockScreen(Integer score) {
        Intent in = getIntent();
        in.setAction(LockDetectService.ACTION_UNLOCK);
        setIntent(in);

        isLocked = false;
        if (score != null) {
            setScore(score);
            updateBackgroundColor();
        }

        scoreText.setVisibility(View.VISIBLE);
        scoreText.setText(String.valueOf(score));
        scoreText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(LockActivity.this, MainActivity.class);
                LockActivity.this.startActivity(in);
                LockActivity.this.finish();
            }
        });
    }

    private void setScore(int score) {
        Intent in = getIntent();
        in.putExtra(LockDetectService.EXTRA_SCORE_KEY, score);
        setIntent(in);
    }

    private void updateBackgroundColor() {
        Bundle bundle = getIntent().getExtras();
        int score = bundle.getInt(LockDetectService.EXTRA_SCORE_KEY, -1);
        if (score > 0) {
            int scoreColor = ColorUtil.interpolateColorFromScore(score);
            //backgroundLayout.setBackgroundColor(interpolateColorFromScore(score));
            int edgeScoreColor =
                Color.rgb(Math.max(Color.red(scoreColor) - 200, 0),
                          Math.max(Color.green(scoreColor) - 200, 0),
                          Math.max(Color.blue(scoreColor) - 200, 0));
            radialGradient = new RadialGradient(screenSize.x/2, screenSize.y/2, 570.0f,
                    scoreColor, edgeScoreColor, Shader.TileMode.CLAMP);
            ShapeDrawable sd = new ShapeDrawable();
            sd.getPaint().setShader(radialGradient);

            backgroundLayout.setBackground(sd);

        }
    }

    private class LockReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle extras = intent.getExtras();
            Integer score = extras.getInt(LockDetectService.EXTRA_SCORE_KEY);
            if (action.equals(LockDetectService.ACTION_UNLOCK)) {
                unlockScreen(score);
            } else if (action.equals(LockDetectService.ACTION_SCORE)) {
                setScore(score);
                updateBackgroundColor();
            }
        }
    }
}



