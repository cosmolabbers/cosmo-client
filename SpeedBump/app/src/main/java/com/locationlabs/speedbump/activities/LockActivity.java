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
import com.locationlabs.speedbump.utils.LogUtil;

import org.w3c.dom.Text;

import java.util.concurrent.locks.Lock;

/**
 *
 */
public class LockActivity extends Activity {

    private static final int BEST_COLOR = Color.rgb(0x2E, 0x8B, 0x57);
    private static final int DANGER_COLOR = Color.rgb(0xDA, 0xA5, 0x20);
    private static final int WORST_COLOR = Color.rgb(0xB2, 0x22, 0x22);

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
            int scoreColor = interpolateColorFromScore(score);
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

    private static int interpolateColorFromScore(int score) {
        int topComp, bottomComp;
        double scoreRatio;
        if (score > 80) {
            topComp = BEST_COLOR;
            bottomComp = DANGER_COLOR;
            scoreRatio = 1 - ((score - 80) / 20.0);
        } else {
            topComp = DANGER_COLOR;
            bottomComp = WORST_COLOR;
            scoreRatio = 1 - (score / 80.0);
        }

        int rTop = Color.red(topComp);
        int bTop = Color.blue(topComp);
        int gTop = Color.green(topComp);

        int rBot = Color.red(bottomComp);
        int bBot = Color.blue(bottomComp);
        int gBot = Color.green(bottomComp);

        int red, blue, green;
        red = rTop - (int) ((rTop - rBot) * scoreRatio);
        blue = bTop - (int) ((bTop - bBot) * scoreRatio);
        green = gTop - (int) ((gTop - gBot) * scoreRatio);

//        LogUtil.d("first red is " + Color.red(topComp) + " and second is " + Color.red(bottomComp));
//        LogUtil.d("first blue is " + Color.blue(topComp) + " and second is " + Color.blue(bottomComp));
//        LogUtil.d("first green is " + Color.green(topComp) + " and second is " + Color.green(bottomComp));
//
//        LogUtil.d("score ratio is " + scoreRatio);
//        LogUtil.d("red is " + red);
//        LogUtil.d("blue is " + blue);
//        LogUtil.d("green is " + green);

        return Color.rgb(red, green, blue);
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



