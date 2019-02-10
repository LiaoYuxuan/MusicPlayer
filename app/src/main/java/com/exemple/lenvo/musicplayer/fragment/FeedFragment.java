package com.exemple.lenvo.musicplayer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exemple.lenvo.musicplayer.R;
import com.exemple.lenvo.musicplayer.util.Consts;

import org.apache.commons.lang3.StringUtils;

/*
 *   项目名：  Musicplayer
 *   包名：    com.exemple.lenvo.musicplayer.fragment
 *   文件名：  MeFragment
 *   创建者：  LYX
 *   创建时间：2019/2/4 12:20
 *   描述：    TODO
 */
public class FeedFragment extends BaseCommonFragment {

    public static FeedFragment newInstance(String userId) {

        Bundle args = new Bundle();
        if (StringUtils.isNotBlank(userId)) {
            args.putString(Consts.ID,userId);
        }

        FeedFragment fragment = new FeedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static FeedFragment newInstance() {
        return newInstance(null);
    }

    @Override
    protected View getLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed,null);
    }
}
