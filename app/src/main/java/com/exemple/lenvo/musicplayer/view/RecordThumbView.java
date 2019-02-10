package com.exemple.lenvo.musicplayer.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.exemple.lenvo.musicplayer.R;
import com.exemple.lenvo.musicplayer.util.DensityUtil;
import com.exemple.lenvo.musicplayer.util.ImageUtil;

/*
 *   项目名：  Musicplayer
 *   包名：    com.exemple.lenvo.musicplayer.view
 *   文件名：  RecordThumbView
 *   创建者：  LYX
 *   创建时间：2019/2/6 15:44
 *   描述：    TODO
 */
public class RecordThumbView extends View implements ValueAnimator.AnimatorUpdateListener {

    /**
     * Cd白圈背景的比例
     */
    public static final float CD_BG_SCALE = 1.333F;

    /**
     * 指针下面那条线高度
     */
    private static final int CD_THUMB_LINE_HEIGHT = 1;

    /**
     * 指针在停止时候的，旋转角度
     */
    private static final float THUMB_ROTATION_PAUSE = -35F;

    /**
     * 指针在播放时候旋转的角度
     */
    private static final float THUMB_ROTATION_PLAY = -6F;

    /**
     * 指针动画的播放时间
     */
    private static final long THUMB_DURATION = 300;

    /**
     * 指针宽度和1080的比值
     */
    private static final float THUMB_WIDTH_SCALE = 2.7F;

    /**
     * 指针的旋转角度
     * 默认，是不播放状态
     */
    private float thumbRotation = THUMB_ROTATION_PAUSE;

    /**
     * 绘制使用的画笔
     */
    private Paint paint;

    /**
     * 指针上面的那条线
     */
    private Drawable cdThumbLine;

    /**
     * 白圈
     */
    private Drawable cdBg;

    /**
     * 开始播放指针的移动动画
     */
    private ValueAnimator playThumbAnimator;

    /**
     * 停止播放指针的移动动画
     */
    private ValueAnimator pauseThumbAnimator;

    /**
     * CD白圈背景到顶部的比例
     */
    public static final float CD_BG_TOP_SCALE = 17.052F;

    /**
     * 指针上面那个原点的宽度，dp
     */
    private static final int THUMB_CIRCLE_WIDTH = 33;

    /**
     * 指针的高度，原图px
     */
    private static final int THUMB_HEIGHT = 138;

    /**
     * 指针绘制的坐标
     */
    private Point thumbPoint;

    /**
     * 指针旋转的坐标
     */
    private Point thumbRotationPoint;

    /**
     * 指针的bitmap
     */
    private Bitmap cdThumb;

    /**
     * 指针旋转使用的矩阵
     */
    private Matrix thumbMatrix = new Matrix();

    /**
     * 指针的宽度，px
     */
    private static final int THUMB_WIDTH = 92;

    public RecordThumbView(Context context) {
        super(context);
        init();
    }

    public RecordThumbView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecordThumbView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RecordThumbView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        //init方法只执行一次，且无法获得宽度
        //初始化画笔画笔
        paint = new Paint();
        paint.setAntiAlias(true);

        //toolbar下的线
        cdThumbLine = getResources().getDrawable(R.drawable.shape_cd_thumb_line);
        cdBg = getResources().getDrawable(R.drawable.shape_cd_bg);

        //创建指针的属性动画
        playThumbAnimator = ValueAnimator.ofFloat(THUMB_ROTATION_PAUSE, THUMB_ROTATION_PLAY);
        playThumbAnimator.setDuration(THUMB_DURATION);
        playThumbAnimator.addUpdateListener(this);

        pauseThumbAnimator = ValueAnimator.ofFloat(THUMB_ROTATION_PLAY, THUMB_ROTATION_PAUSE);
        pauseThumbAnimator.setDuration(300);
        pauseThumbAnimator.addUpdateListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();

        //设置线
        cdThumbLine.setBounds(0, 0, measuredWidth, DensityUtil.dip2px(getContext(), CD_THUMB_LINE_HEIGHT));

        int topCircleWidth = DensityUtil.dip2px(getContext(), THUMB_CIRCLE_WIDTH);

        //指针的坐标点（左上角点）
        thumbPoint = new Point(measuredWidth / 2 - topCircleWidth / 2, -topCircleWidth / 2);
        //指针的旋转点
        thumbRotationPoint = new Point(measuredWidth / 2, 0);

        //因为onMeasure反复调用，所以必须要先判断是否为空
        if (cdThumb == null) {
            initBitmap();
        }
    }

    private void initBitmap() {
        //获取Bitmap，需要用到View宽度的，所以要在onMeasure中
        int measuredWidth = getMeasuredWidth();

        int imageHeight = (int) (measuredWidth / THUMB_WIDTH_SCALE);

        //计算缩放比，以缩放图片
        double scale = imageHeight * 1.0 / DensityUtil.dip2px(getContext(), THUMB_HEIGHT);

        //Thumb的宽度
        int imageWidth = (int) (scale * DensityUtil.dip2px(getContext(), THUMB_WIDTH));

        //获取到的Bitmap可以比需要的大，要进行调整
        cdThumb = ImageUtil.scaleBitmap(getResources(), R.drawable.cd_thumb, imageWidth, imageHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //java绘图的基础知识canvas.save()和canvas.restore();
        canvas.save();

        //可以通过SurfaceView来实现局部绘制
        //因为旋转指针时，背景和上面那条线不用在重新绘制了
        //但View不行，因为每次View都是一个全新的Canvas
        //绘制线
        cdThumbLine.draw(canvas);

        //绘制指针
        //传入矩阵：旋转角度，旋转点坐标
        thumbMatrix.setRotate(thumbRotation, thumbRotationPoint.x, thumbRotationPoint.y);
        //移动画笔位置
        thumbMatrix.preTranslate(thumbPoint.x, thumbPoint.y);
        canvas.drawBitmap(cdThumb, thumbMatrix, paint);

        canvas.restore();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        //此方法在指针旋转的300ms内反复调用
        thumbRotation = (float) animation.getAnimatedValue();
        //刷新，即执行onDraw方法
        invalidate();
    }

    public void stopThumbAnimation() {
        pauseThumbAnimator.start();
    }

    public void startThumbAnimation() {
        playThumbAnimator.start();
    }
}
