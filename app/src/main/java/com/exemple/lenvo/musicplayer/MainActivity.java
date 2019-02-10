package com.exemple.lenvo.musicplayer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.exemple.lenvo.musicplayer.activity.BaseTitleActivity;
import com.exemple.lenvo.musicplayer.activity.LoginActivity;
import com.exemple.lenvo.musicplayer.activity.SettingsActivity;
import com.exemple.lenvo.musicplayer.activity.UserDetailActivity;
import com.exemple.lenvo.musicplayer.adapter.HomeAdapter;
import com.exemple.lenvo.musicplayer.api.Api;
import com.exemple.lenvo.musicplayer.domain.User;
import com.exemple.lenvo.musicplayer.domain.event.LogoutSuccessEvent;
import com.exemple.lenvo.musicplayer.domain.response.DetailResponse;
import com.exemple.lenvo.musicplayer.reactivex.HttpListener;
import com.exemple.lenvo.musicplayer.util.UserUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseTitleActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private DrawerLayout drawer_layout;
    private LinearLayout left_draw;
    private CoordinatorLayout right_draw;

    ImageView iv_avatar;
    TextView tv_nickname;
    TextView tv_description;

    private ViewPager vp;
    private HomeAdapter adapter;
    private ImageView iv_music;
    private ImageView iv_recommend;
    private ImageView iv_video;
    private LinearLayout ll_settings;
    private LinearLayout ll_my_friend;
    private LinearLayout ll_message_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initViews() {
        super.initViews();
        //一般在initViews中注册，在onDestroy中解除注册
        //也可以延迟注册，比如：当前用户点击到设置界面是才注册
        EventBus.getDefault().register(this);

        drawer_layout = findViewById(R.id.drawer_layout);

        iv_avatar = findViewById(R.id.iv_avatar);
        tv_nickname = findViewById(R.id.tv_nickname);
        tv_description = findViewById(R.id.tv_description);

        right_draw = (CoordinatorLayout) findViewById(R.id.right_draw);
        left_draw = (LinearLayout) findViewById(R.id.left_draw);

        iv_music = findViewById(R.id.iv_music);
        iv_recommend = findViewById(R.id.iv_recommend);
        iv_video = findViewById(R.id.iv_video);
        ll_settings = findViewById(R.id.ll_settings);
        ll_my_friend = findViewById(R.id.ll_my_friend);
        ll_message_container = findViewById(R.id.ll_message_container);

        vp = findViewById(R.id.vp);

        //将侧滑页和ActionBar绑定
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer_layout.addDrawerListener(toggle);
        toggle.syncState();
        //修改默认图标
        toolbar.setNavigationIcon(R.drawable.touxiang);

        //设置类似qq的侧滑效果
        drawer_layout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                //滑动过程中不断回调 slideOffset:0~1
                View content = drawer_layout.getChildAt(0);
                View menu = drawerView;

                float scale = 1 - slideOffset;//1~0
                content.setTranslationX(menu.getMeasuredWidth() * (1 - scale));//0~width

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });

        //缓存三个页面
        vp.setOffscreenPageLimit(3);

    }

    @Override
    protected void initDatas() {
        super.initDatas();

        adapter = new HomeAdapter(getActivity(), getSupportFragmentManager());
        vp.setAdapter(adapter);

        ArrayList<Integer> datas = new ArrayList<>();
        datas.add(0);
        datas.add(1);
        datas.add(2);
        adapter.setDatas(datas);

        showUserInfo();
    }

    private void showData(User data) {
        //将显示用户信息放到单独的类中，是为了重用，因为在用户详情界面会用到
        UserUtil.showUser(getActivity(), data, iv_avatar, tv_nickname, tv_description);
    }

    @Override
    protected void initListener() {
        super.initListener();
        iv_music.setOnClickListener(this);
        iv_recommend.setOnClickListener(this);
        iv_video.setOnClickListener(this);

        vp.addOnPageChangeListener(this);
        //默认选中第二个页面，设置监听器在选择就会调用监听器
        vp.setCurrentItem(1);

        ll_settings.setOnClickListener(this);
        ll_my_friend.setOnClickListener(this);
        ll_message_container.setOnClickListener(this);
    }

    @OnClick(R.id.iv_avatar)
    public void avatarClick() {
        closeDrawer();
        if (sp.isLogin()) {
            startActivityExtraId(UserDetailActivity.class, sp.getUserId());
        } else {
            startActivityAfterFinishThis(LoginActivity.class);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_music:
                vp.setCurrentItem(0, true);
                break;
            case R.id.iv_recommend:
                vp.setCurrentItem(1, true);
                break;
            case R.id.iv_video:
                vp.setCurrentItem(2, true);
                break;
            case R.id.ll_settings:
                startActivity(SettingsActivity.class);
                closeDrawer();
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            iv_music.setImageResource(R.drawable.ic_play_selected);
            iv_recommend.setImageResource(R.drawable.ic_music);
            iv_video.setImageResource(R.drawable.ic_video);
        } else if (position == 1) {
            iv_music.setImageResource(R.drawable.ic_play);
            iv_recommend.setImageResource(R.drawable.ic_music_selected);
            iv_video.setImageResource(R.drawable.ic_video);
        } else {
            iv_music.setImageResource(R.drawable.ic_play);
            iv_recommend.setImageResource(R.drawable.ic_music);
            iv_video.setImageResource(R.drawable.ic_video_selected);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void closeDrawer() {
        drawer_layout.closeDrawer(Gravity.START);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void logoutSuccessEvent(LogoutSuccessEvent event) {
        showUserInfo();
    }

    private void showUserInfo() {
        //用户信息这部分，进来是看不到的，所以可以延后初始化
        if (sp.isLogin()) {
            //调用用户信息接口
            Api.getInstance().userDetail(sp.getUserId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new HttpListener<DetailResponse<User>>(getActivity()) {
                        @Override
                        public void onSucceeded(DetailResponse<User> data) {
                            super.onSucceeded(data);
                            showData(data.getData());
                        }
                    });

        } else {
            UserUtil.showNotLoginUser(getActivity(), iv_avatar, tv_nickname, tv_description);
        }
    }

    //event一定要在onDestroy中解除注册
    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
