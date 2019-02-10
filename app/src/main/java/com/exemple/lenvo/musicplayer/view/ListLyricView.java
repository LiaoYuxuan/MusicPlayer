package com.exemple.lenvo.musicplayer.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

/*
 *   项目名：  Musicplayer
 *   包名：    com.exemple.lenvo.musicplayer.view
 *   文件名：  ListLyricView
 *   创建者：  LYX
 *   创建时间：2019/2/6 15:48
 *   描述：    TODO
 */
public class ListLyricView extends View {
    public ListLyricView(Context context) {
        super(context);
    }

    public ListLyricView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ListLyricView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ListLyricView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
