package com.exemple.lenvo.musicplayer.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.exemple.lenvo.musicplayer.MainActivity;
import com.exemple.lenvo.musicplayer.R;
import com.exemple.lenvo.musicplayer.util.Consts;
import com.exemple.lenvo.musicplayer.util.PackageUtil;

import butterknife.BindView;
import cdc.sed.yff.AdManager;
import cdc.sed.yff.nm.sp.SplashViewSettings;
import cdc.sed.yff.nm.sp.SpotListener;
import cdc.sed.yff.nm.sp.SpotManager;
import cdc.sed.yff.nm.sp.SpotRequestListener;

public class SplashActivity extends BaseCommonActivity {

    private static final String TAG = "TAG";

    private static final long DEFAULT_DELAY_TIME = 3000;
    public static final int MSG_GUIDE = 100;
    private static final int MSG_HOME = 110;

    //这样创建有内存泄漏，在性能优化我们具体讲解
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_GUIDE:
                    //跳转到引导页
                    startActivityAfterFinishThis(GuideActivity.class);
                    break;

                case MSG_HOME:
                    //跳转到首页
                    startActivityAfterFinishThis(MainActivity.class);
                    break;
            }
            //next();
        }
    };

    //广告容器
    @BindView(R.id.ad_container)
    ViewGroup ad_container;

//    //这样创建有内存泄漏，在性能优化我们具体讲解(只显示主界面的handler)
//    @SuppressLint("HandlerLeak")
//    private Handler mHandler = new Handler() {
//        @SuppressWarnings("unused")
//        public void handleMessage(Message msg) {
//            next();
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        if (isShowGuide()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(MSG_GUIDE);
                }
            }, DEFAULT_DELAY_TIME);
        } else {
            //开始获取广告
            startGetAd();
        }
            //startActivityAfterFinishThis(HomeActivity.class);
//        //延时3秒，在企业中通常会有很多逻辑处理，所以延时时间最好是用3-消耗的的时间
        //显示广告时注释掉即可
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mHandler.sendEmptyMessage(-1);
//            }
//        }, 3000);
    }

    private void next() {
        if (isShowGuide()) {
            startActivityAfterFinishThis(GuideActivity.class);
        } else if (sp.isLogin()) {
            startActivityAfterFinishThis(MainActivity.class);
        } else {
            startActivityAfterFinishThis(LoginActivity.class);
        }
    }

    /**
     * 根据当前版本号判断是否需要引导页
     * @return
     */
    private boolean isShowGuide() {
        return sp.getBoolean(String.valueOf(PackageUtil.getVersionCode(getApplicationContext())),true);
    }

    private void startGetAd() {
        //初始化
        AdManager.getInstance(this).init(Consts.AK, Consts.AS, false);
        //预加载
        SpotManager.getInstance(this).requestSpot(new SpotRequestListener() {
            @Override
            public void onRequestSuccess() {
                Log.d(TAG, "onRequestSuccess");
                //广告获取成功，显示广告
                showA();
            }

            @Override
            public void onRequestFailed(int i) {
                Log.d(TAG, "onRequestFailed:" + i);
                toHome();

            }
        });
    }

    private void showA() {
        SplashViewSettings splashViewSettings = new SplashViewSettings();
        //如果显示的时候失败，还是进入主界面
        splashViewSettings.setAutoJumpToTargetWhenShowFailed(true);
        splashViewSettings.setTargetClass(MainActivity.class);
        splashViewSettings.setSplashViewContainer(ad_container);
        SpotManager.getInstance(this).showSplash(this,
                splashViewSettings, new SpotListener() {
                    @Override
                    public void onShowSuccess() {
                        Log.d(TAG, "onShowSuccess");
                    }

                    @Override
                    public void onShowFailed(int i) {
                        Log.d(TAG, "onShowFailed: " + i);

                        //如果显示的时候失败，还是进入主界面
                        //toHome();
                    }

                    @Override
                    public void onSpotClosed() {
                        Log.d(TAG, "onSpotClosed");
                    }

                    @Override
                    public void onSpotClicked(boolean b) {
                        Log.d(TAG, "onSpotClicked");
                    }
                });
    }

    private void toHome() {
        //获取失败，延时3秒进入主界面
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(MSG_HOME);
            }
        }, DEFAULT_DELAY_TIME);
    }
}

