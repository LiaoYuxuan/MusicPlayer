package com.exemple.lenvo.musicplayer.interpolator;

import android.view.animation.LinearInterpolator;

/*
 *   项目名：  LoginActivity
 *   包名：    com.exemple.lenvo.loginactivity
 *   文件名：  JellyInterpolator
 *   创建者：  LYX
 *   创建时间：2019/2/2 14:41
 *   描述：    TODO
 */
public class JellyInterpolator extends LinearInterpolator {
    private float factor;

    public JellyInterpolator() {
        this.factor = 0.15f;
    }

    @Override
    public float getInterpolation(float input) {
        return (float) (Math.pow(2, -10 * input)
                * Math.sin((input - factor / 4) * (2 * Math.PI) / factor) + 1);
    }
}
