package com.exemple.lenvo.musicplayer.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.exemple.lenvo.musicplayer.MainActivity;
import com.exemple.lenvo.musicplayer.R;
import com.exemple.lenvo.musicplayer.api.Api;
import com.exemple.lenvo.musicplayer.domain.Session;
import com.exemple.lenvo.musicplayer.domain.User;
import com.exemple.lenvo.musicplayer.domain.event.LoginSuccessEvent;
import com.exemple.lenvo.musicplayer.domain.response.DetailResponse;
import com.exemple.lenvo.musicplayer.interpolator.JellyInterpolator;
import com.exemple.lenvo.musicplayer.reactivex.HttpListener;
import com.exemple.lenvo.musicplayer.util.StringUtil;
import com.exemple.lenvo.musicplayer.util.ToastUtil;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LoginPhoneActivity extends BaseTitleActivity {

    private float mWidth, mHeight;
    private String phone;
    private String password;

    private static final long DEFAULT_DELAY_TIME = 3000;
    private boolean cancle = false;
    public static final int LOADING = 100;

    //这样创建有内存泄漏，在性能优化我们具体讲解
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    if(!cancle){
                        User user = new User();
                        user.setPhone(phone);
                        user.setPassword(password);
                        user.setType(User.TYPE_PHONE);

                        Api.getInstance().login(user)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new HttpListener<DetailResponse<Session>>(getActivity()) {
                                    @Override
                                    public void onSucceeded(DetailResponse<Session> data) {
                                        super.onSucceeded(data);
                                        next(data.getData());
                                    }
                                });
                    } else {
                        recovery();
                    }
                    break;
            }
        }
    };

    @BindView(R.id.input_layout_name)
    LinearLayout input_layout_name;

    @BindView(R.id.input_layout_psw)
    LinearLayout input_layout_psw;

    @BindView(R.id.layout_progress)
    View layout_progress;

    @BindView(R.id.input_layout)
    View input_layout;

    @BindView(R.id.et_phone)
    EditText et_phone;

    @BindView(R.id.et_password)
    EditText et_password;

    @BindView(R.id.bt_login)
    Button bt_login;

    @BindView(R.id.bt_cancel)
    Button bt_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone);
    }

    @Override
    protected void initViews() {
        super.initViews();
        enableBackMenu();
    }

    @OnClick(R.id.bt_cancel)
    public void bt_cancel(){
        cancle = true;
        bt_cancel.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.bt_login)
    public void bt_login(){
        cancle = false;
        phone = et_phone.getText().toString();
        if (StringUtils.isBlank(phone)) {
            ToastUtil.showSortToast(getActivity(), R.string.hint_phone);
            return;
        }

        if (!StringUtil.isPhone(phone)) {
            ToastUtil.showSortToast(getActivity(), R.string.hint_error_phone);
            return;
        }

        password = et_password.getText().toString();
        if (StringUtils.isBlank(password)) {
            ToastUtil.showSortToast(getActivity(), R.string.hint_password);
            return;
        }

        if (!StringUtil.isPassword(password)) {
            ToastUtil.showSortToast(getActivity(), R.string.hint_error_password_format);
            return;
        }

        bt_cancel.setVisibility(View.VISIBLE);
        //发送延时消息
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(LOADING);
            }
        }, DEFAULT_DELAY_TIME);

        // 计算出控件的高与宽
        mWidth = bt_login.getMeasuredWidth();
        mHeight = bt_login.getMeasuredHeight();
        // 隐藏输入框
        input_layout_name.setVisibility(View.INVISIBLE);
        input_layout_psw.setVisibility(View.INVISIBLE);

        inputAnimator(input_layout, mWidth, mHeight);

    }

    /**
     * 登陆完成后，保存相关信息，并跳转到主界面
     *
     * @param data
     */
    private void next(Session data) {
        sp.setToken(data.getToken());
        sp.setUserId(data.getId());
        sp.setIMToken(data.getIm_token());
        layout_progress.setVisibility(View.INVISIBLE);
        startActivityAfterFinishThis(MainActivity.class);

        EventBus.getDefault().post(new LoginSuccessEvent());
    }

    /**
     * 输入框的动画效果
     *
     * @param view
     *            控件
     * @param w
     *            宽
     * @param h
     *            高
     */
    private void inputAnimator(final View view, float w, float h) {

        AnimatorSet set = new AnimatorSet();

        ValueAnimator animator = ValueAnimator.ofFloat(0, w);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view
                        .getLayoutParams();
                params.leftMargin = (int) value;
                params.rightMargin = (int) value;
                view.setLayoutParams(params);
            }
        });

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(input_layout,
                "scaleX", 1f, 0.5f);
        set.setDuration(1000);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.playTogether(animator, animator2);
        set.start();
        set.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                /**
                 * 动画结束后，先显示加载的动画，然后再隐藏输入框
                 */
                layout_progress.setVisibility(View.VISIBLE);
                progressAnimator(layout_progress);
                bt_cancel.setVisibility(View.VISIBLE);
                input_layout.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });

    }

    /**
     * 出现进度动画
     *
     * @param view
     */
    private void progressAnimator(final View view) {
        PropertyValuesHolder animator = PropertyValuesHolder.ofFloat("scaleX",
                0.5f, 1f);
        PropertyValuesHolder animator2 = PropertyValuesHolder.ofFloat("scaleY",
                0.5f, 1f);
        ObjectAnimator animator3 = ObjectAnimator.ofPropertyValuesHolder(view,
                animator, animator2);
        animator3.setDuration(1000);
        animator3.setInterpolator(new JellyInterpolator());
        animator3.start();

    }

    /**
     * 恢复初始状态
     */
    private void recovery() {
        layout_progress.setVisibility(View.GONE);
        input_layout.setVisibility(View.VISIBLE);
        input_layout_name.setVisibility(View.VISIBLE);
        input_layout_psw.setVisibility(View.VISIBLE);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) input_layout.getLayoutParams();
        params.leftMargin = 0;
        params.rightMargin = 0;
        input_layout.setLayoutParams(params);


        ObjectAnimator animator2 = ObjectAnimator.ofFloat(input_layout, "scaleX", 0.5f,1f );
        animator2.setDuration(500);
        animator2.setInterpolator(new AccelerateDecelerateInterpolator());
        animator2.start();
    }
}
