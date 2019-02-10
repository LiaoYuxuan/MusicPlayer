package com.exemple.lenvo.musicplayer.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.WindowManager;

import com.exemple.lenvo.musicplayer.MainActivity;
import com.exemple.lenvo.musicplayer.R;
import com.exemple.lenvo.musicplayer.adapter.GuideAdapter;
import com.exemple.lenvo.musicplayer.util.PackageUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import me.relex.circleindicator.CircleIndicator;

public class GuideActivity extends BaseCommonActivity {

    @BindView(R.id.vp)
    ViewPager vp;

    @BindView(R.id.indicator)
    CircleIndicator indicator;

    private GuideAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_guide);
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        adapter = new GuideAdapter(getActivity(), getSupportFragmentManager());
        vp.setAdapter(adapter);
        indicator.setViewPager(vp);
        adapter.registerDataSetObserver(indicator.getDataSetObserver());


        ArrayList<Integer> datas = new ArrayList<>();
        datas.add(R.drawable.guide1);
        datas.add(R.drawable.guide2);
        datas.add(R.drawable.guide3);
        adapter.setDatas(datas);
    }

    @OnClick(R.id.bt_login_or_register)
    public void bt_login_or_register() {
        setFirst();
        startActivityAfterFinishThis(LoginActivity.class);
    }

    /**
     * 或者
     * public void bt_enter(View view) {
     *         setFirst();
     *         startActivityAfterFinishThis(MainActivity.class);
     *     }
     *     并在布局文件中对应按钮中加：
     *     android onClick="对应id值"
     */

    @OnClick(R.id.bt_enter)
    public void bt_enter() {
        setFirst();
        startActivityAfterFinishThis(MainActivity.class);
    }

    private void setFirst() {
        sp.putBoolean(String.valueOf(PackageUtil.getVersionCode(getApplicationContext())),false);
    }

    /**
     * 不调用父类方法，用户按返回键就不能关闭当前页面了
     */
    @Override
    public void onBackPressed() {
     //super.onBackPressed();
    }
}
