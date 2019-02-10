package com.exemple.lenvo.musicplayer.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.exemple.lenvo.musicplayer.R;
import com.exemple.lenvo.musicplayer.util.DensityUtil;
import com.exemple.lenvo.musicplayer.util.ImageUtil;

import java.util.Timer;
import java.util.TimerTask;

/*
 *   项目名：  Musicplayer
 *   包名：    com.exemple.lenvo.musicplayer.view
 *   文件名：  RecordView
 *   创建者：  LYX
 *   创建时间：2019/2/6 15:47
 *   描述：    TODO
 */
public class RecordView extends View {

    /**
     * 黑胶唱片宽高比例
     */
    private static final float CD_SCALE = 1.333F;

    /**
     * 封面比例
     */
    //private static final float ALBUM_SCALE = 2.037F;
    private static final float ALBUM_SCALE = 2.1F;

    /**
     * 1秒绘制60次会保证画面的流畅性
     * 转360°大约需要25s
     * 每16毫秒旋转的角度
     * <p>
     * 16毫秒是通过，每秒60帧计算出来的
     * 也就是1000/60=16，也就是说绘制一帧要在16毫秒中完成，不然就能感觉卡顿
     */
    public static final float ROTATION_PER = 0.2304F;

    /**
     * 画笔
     */
    private Paint paint;

    /**
     * 黑胶唱片bitmap
     */
    private Bitmap cd;

    /**
     * 黑胶唱片绘制坐标
     */
    private Point cdPoint = new Point();

    /**
     * 旋转点，都是在中点，所以一个就够了
     */
    private Point cdRotationPoint = new Point();

    /**
     * 封面的宽度
     */
    private int albumWidth;

    /**
     * 封面绘制坐标
     */
    private Point albumPoint = new Point();


    /**
     * 封面图
     */
    private String albumUri;


    /**
     * 封面bitmap
     */
    private Bitmap album;

    /**
     * 黑胶唱片矩阵
     */
    private Matrix cdMatrix = new Matrix();

    /**
     * 封面矩阵
     */
    private Matrix albumMatrix = new Matrix();


    /**
     * 旋转的角度
     */
    private float cdRotation = 0;

    /**
     * 计时器任务
     */
    private TimerTask timerTask;

    /**
     * 计算器，用来调度唱片，专辑转动
     */
    private Timer timer;

    public RecordView(Context context) {
        super(context);
        init();
    }

    public RecordView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RecordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        int widthHalf = measuredWidth / 2;

        initResource();

        //黑胶
        int cdWidthHalf = cd.getWidth() / 2;
        int cdLeft = widthHalf - cdWidthHalf;

        //他的top，应该有后面白圈的中心点算
        int cdBgWidth = (int) (measuredWidth / RecordThumbView.CD_BG_SCALE);
        int cdBgWidthHalf = cdBgWidth / 2;
        int cdBgTop = DensityUtil.dip2px(getContext(), measuredWidth / RecordThumbView.CD_BG_TOP_SCALE);
        //白圈中心点
        int cdBgCenterY=cdBgTop+cdBgWidthHalf;

        int cdTop = cdBgCenterY-cdWidthHalf;
        //利用对象来保存坐标
        cdPoint.set(cdLeft, cdTop);
        //旋转点坐标
        cdRotationPoint.set(widthHalf, cdWidthHalf + cdTop);

        //封面
        albumWidth = (int) (measuredWidth / ALBUM_SCALE);
        int albumWidthHalf = albumWidth / 2;
        int albumLeft = widthHalf - albumWidthHalf;
        int albumTop = cdBgCenterY-albumWidthHalf;
        albumPoint.set(albumLeft, albumTop);

        showAlbum();
    }

    private void initResource() {
        if (cd == null) {
            //cd背景
            int cdWidth = (int) (getMeasuredWidth() / CD_SCALE);
            cd = ImageUtil.scaleBitmap(getResources(), R.drawable.cd_bg, cdWidth, cdWidth);
        }
    }

    private void showAlbum() {
        if (albumWidth!=0) {
            RequestOptions options = new RequestOptions();
            //中心裁剪
            options.centerCrop();
            //圆形裁剪
            options.circleCrop();
            //options.diskCacheStrategy(DiskCacheStrategy.NONE);
            //获取宽高
            options.override(albumWidth, albumWidth);
            Glide.with(this).asBitmap().load(ImageUtil.getImageURI(this.albumUri)).apply(options).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    album = ImageUtil.resizeImage(resource, albumWidth, albumWidth);
                    invalidate();
                }
            });
        }
    }


    /**
     * 设置歌曲封面
     *
     * @param uri
     */
    public void setAlbumUri(String uri) {
        this.albumUri=uri;
        //在切换歌曲后不会再调用onMeasure所以要重新显示
        showAlbum();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        //绘制cd
        cdMatrix.setRotate(cdRotation, cdRotationPoint.x, cdRotationPoint.y);
        cdMatrix.preTranslate(cdPoint.x, cdPoint.y);
        canvas.drawBitmap(cd, cdMatrix, paint);

        //绘制封面
        if (album != null) {
            albumMatrix.setRotate(cdRotation, cdRotationPoint.x, cdRotationPoint.y);
            albumMatrix.preTranslate(albumPoint.x, albumPoint.y);
            canvas.drawBitmap(album, albumMatrix, paint);
        }

        canvas.restore();
    }

    public void stopAlbumRotate() {
        cancelTask();
    }

    public void startAlbumRotate() {
        //销毁原来的task
        cancelTask();
        timerTask = new TimerTask() {

            @Override
            public void run() {
                if (cdRotation >= 360) {
                    cdRotation = 0;
                }
                cdRotation += ROTATION_PER;
                //Invalidate()只能在主线程中调用，而postInvalidate则可在子线程中调用
                postInvalidate();
            }
        };
        timer = new Timer();
        //第一次执行时的延时和隔多长时间再次调用此方法
        timer.schedule(timerTask, 0, 16);
    }

    private void cancelTask() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

    }
}
