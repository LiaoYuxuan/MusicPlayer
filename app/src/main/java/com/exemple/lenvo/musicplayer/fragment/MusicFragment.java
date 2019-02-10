package com.exemple.lenvo.musicplayer.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exemple.lenvo.musicplayer.R;
import com.exemple.lenvo.musicplayer.adapter.MusicUIAdapter;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import java.util.ArrayList;

/*
 *   项目名：  Musicplayer
 *   包名：    com.exemple.lenvo.musicplayer.fragment
 *   文件名：  MeFragment
 *   创建者：  LYX
 *   创建时间：2019/2/4 12:20
 *   描述：    TODO
 */
public class MusicFragment extends BaseCommonFragment {

    MagicIndicator tabs;
    ViewPager vp;
    private MusicUIAdapter adapter;

    public static MusicFragment newInstance() {

        Bundle args = new Bundle();

        MusicFragment fragment = new MusicFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View getLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_music, null);
    }

    @Override
    protected void initViews() {
        super.initViews();
        tabs = findViewById(R.id.tabs);
        vp = findViewById(R.id.vp);
        //缓存三个页面
        vp.setOffscreenPageLimit(3);
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        //这里一定要调用childFragmentManager!!!!!!
        adapter = new MusicUIAdapter(getActivity(), getChildFragmentManager());
        vp.setAdapter(adapter);

        final ArrayList<Integer> datas = new ArrayList<>();
        datas.add(0);
        datas.add(1);
        datas.add(2);
        adapter.setDatas(datas);

        //将TabLayout和ViewPager关联起来
        CommonNavigator commonNavigator = new CommonNavigator(getMainActivity());
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return datas.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);
                colorTransitionPagerTitleView.setNormalColor(getResources().getColor(R.color.text_white));
                colorTransitionPagerTitleView.setSelectedColor(Color.WHITE);
                colorTransitionPagerTitleView.setText(adapter.getPageTitle(index));
                colorTransitionPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        vp.setCurrentItem(index);
                    }
                });
                return colorTransitionPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                indicator.setColors(Color.WHITE);
                return indicator;
            }
        });
        commonNavigator.setAdjustMode(true);
        tabs.setNavigator(commonNavigator);

        ViewPagerHelper.bind(tabs, vp);
    }
}
