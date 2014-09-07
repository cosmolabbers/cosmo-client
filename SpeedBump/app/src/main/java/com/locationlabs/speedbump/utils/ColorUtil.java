package com.locationlabs.speedbump.utils;

import android.graphics.Color;

/**
 * Created by jamesl on 9/7/14.
 */
public class ColorUtil {

    private static final int BEST_COLOR = Color.rgb(0x2E, 0x8B, 0x57);
    private static final int DANGER_COLOR = Color.rgb(0xDA, 0xA5, 0x20);
    private static final int WORST_COLOR = Color.rgb(0xB2, 0x22, 0x22);

    public static int interpolateColorFromScore(int score) {
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
}
