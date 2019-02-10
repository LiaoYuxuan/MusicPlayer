package com.exemple.lenvo.musicplayer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exemple.lenvo.musicplayer.R;

/*
 *   项目名：  Musicplayer
 *   包名：    com.exemple.lenvo.musicplayer.fragment
 *   文件名：  MeFragment
 *   创建者：  LYX
 *   创建时间：2019/2/4 12:20
 *   描述：    TODO
 */
public class UserDetailMusicFragment extends BaseCommonFragment {

    public static UserDetailMusicFragment newInstance() {
        
        Bundle args = new Bundle();
        
        UserDetailMusicFragment fragment = new UserDetailMusicFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected View getLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_userdetailmusic,null);
    }
}
