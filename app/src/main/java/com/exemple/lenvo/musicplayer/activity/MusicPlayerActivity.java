package com.exemple.lenvo.musicplayer.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.exemple.lenvo.musicplayer.R;
import com.exemple.lenvo.musicplayer.domain.Song;
import com.exemple.lenvo.musicplayer.listener.OnMusicPlayerListener;
import com.exemple.lenvo.musicplayer.manager.MusicPlayerManager;
import com.exemple.lenvo.musicplayer.service.MusicPlayerService;
import com.exemple.lenvo.musicplayer.util.AlbumDrawableUtil;
import com.exemple.lenvo.musicplayer.util.ImageUtil;
import com.exemple.lenvo.musicplayer.util.TimeUtil;
import com.exemple.lenvo.musicplayer.view.ListLyricView;
import com.exemple.lenvo.musicplayer.view.RecordThumbView;
import com.exemple.lenvo.musicplayer.view.RecordView;

import org.apache.commons.lang3.StringUtils;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class MusicPlayerActivity extends BaseTitleActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, OnMusicPlayerListener {

    private ImageView iv_loop_model;
    private ImageView iv_album_bg;
    private ImageView iv_play_control;
    private ImageView iv_play_list;
    private ImageView iv_previous;
    private ImageView iv_next;
    private TextView tv_start_time;
    private TextView tv_end_time;
    private SeekBar sb_progress;
    private RecordThumbView rt;
    private ImageView iv_download;
    private RecordView rv;
    private ViewPager vp;
    private LinearLayout lyric_container;
    private RelativeLayout rl_player_container;
    private SeekBar sb_volume;
    private ListLyricView lv;

    private AudioManager audioManager;

    /**
     * 测试使用
     */
    private boolean isPlayer;
    private MusicPlayerManager musicPlayerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
    }

    @Override
    protected void initViews() {
        super.initViews();

        enableBackMenu();

        iv_download = findViewById(R.id.iv_download);
        iv_album_bg = findViewById(R.id.iv_album_bg);
        iv_loop_model = findViewById(R.id.iv_loop_model);
        iv_play_control = findViewById(R.id.iv_play_control);
        rt = findViewById(R.id.rt);
        tv_start_time = findViewById(R.id.tv_start_time);
        tv_end_time = findViewById(R.id.tv_end_time);
        sb_progress = findViewById(R.id.sb_progress);
        iv_next = findViewById(R.id.iv_next);
        iv_previous = findViewById(R.id.iv_previous);
        iv_play_list = findViewById(R.id.iv_play_list);
        rv = findViewById(R.id.rv);
        lyric_container = findViewById(R.id.lyric_container);
        rl_player_container = findViewById(R.id.rl_player_container);
        sb_volume = findViewById(R.id.sb_volume);
        lv = findViewById(R.id.lv);
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        musicPlayerManager = MusicPlayerService.getMusicPlayerManager(getApplicationContext());
        //音量
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        setVolume();
    }

    private void setVolume() {
        //STREAM_MUSIC等有很多不同的声音控制，如铃声等
        int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        sb_volume.setMax(max);
        sb_volume.setProgress(current);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean result = super.onKeyDown(keyCode, event);
        if (KeyEvent.KEYCODE_VOLUME_UP == keyCode || KeyEvent.KEYCODE_VOLUME_DOWN == keyCode) {
            setVolume();
        }

        return result;
    }

    @Override
    protected void initListener() {
        super.initListener();
        iv_download.setOnClickListener(this);
        iv_play_control.setOnClickListener(this);
        iv_play_list.setOnClickListener(this);
        iv_loop_model.setOnClickListener(this);
        iv_previous.setOnClickListener(this);
        iv_next.setOnClickListener(this);
        sb_progress.setOnSeekBarChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        musicPlayerManager.addOnMusicPlayerListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        musicPlayerManager.removeOnMusicPlayerListener(this);
    }

    private void stopRecordRotate() {
        //EventBus.getDefault().post(new OnStopRecordEvent(currentSong));
        rt.stopThumbAnimation();
        rv.stopAlbumRotate();
    }

    private void startRecordRotate() {
        //EventBus.getDefault().post(new OnStartRecordEvent(currentSong));
        rv.startAlbumRotate();
        rt.startThumbAnimation();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_play_control:
                playOrPause();
                break;
//            case R.id.iv_play_list:
//                showPlayListDialog();
//                break;
            //case R.id.lv:
            //    showRecordView();
            //    break;
//            case R.id.iv_previous:
//                Song song = playListManager.previous();
//                playListManager.play(song);
//                break;
//            case R.id.iv_next:
//                Song songNext = playListManager.next();
//                playListManager.play(songNext);
//                break;
//            case R.id.iv_loop_model:
//                int loopModel = playListManager.changeLoopModel();
//                showLoopModel(loopModel);
//                break;
//            case R.id.iv_download:
//                download();
//                break;
           }
    }

        private void playOrPause() {
//            if (musicPlayerManager.isPlaying()) {
//                pause();
//            } else {
//                play();
//            }
            if (isPlayer) {
                pause();
            } else {
                play();
            }
            isPlayer = !isPlayer;
    }

    private void play() {
        //playListManager.resume();
        startRecordRotate();
        //musicPlayerManager.play("http://dev-courses-misuc.ixuea.com/assets/s1.mp3",new Song());
        musicPlayerManager.play("https://api.bzqll.com/music/netease/url?key=579621905&id=526307800&br=999000",new Song());
    }

    private void pause() {
        //playListManager.pause();
        stopRecordRotate();
        musicPlayerManager.pause();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    //有两个progressbar，所以要区分
        if (fromUser) {
            if (seekBar.getId() == R.id.sb_volume) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            } else {
                musicPlayerManager.seekTo(progress);
                if (!musicPlayerManager.isPlaying()) {
                    musicPlayerManager.resume();
                }
            }

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onProgress(long progress, long total) {
        tv_start_time.setText(TimeUtil.formatMSTime((int) progress));
        sb_progress.setProgress((int) progress);
        //lv.show(progress);
    }

    @Override
    public void onPaused(Song data) {
        iv_play_control.setImageResource(R.drawable.selector_music_play);
        stopRecordRotate();
    }

    @Override
    public void onPlaying(Song data) {
        iv_play_control.setImageResource(R.drawable.selector_music_pause);

//        if (currentSong!=null) {
//            //停止这首音乐黑胶唱片的滚动
//            EventBus.getDefault().post(new OnStopRecordEvent(currentSong));
//        }
//
//        this.currentSong=data;

        startRecordRotate();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer, Song data) {
        setInitData(data);
    }

    public void setInitData(Song data) {
        sb_progress.setMax((int) data.getDuration());
        sb_progress.setProgress(sp.getLastSongProgress());
        tv_start_time.setText(TimeUtil.formatMSTime((int) sp.getLastSongProgress()));
        tv_end_time.setText(TimeUtil.formatMSTime((int) data.getDuration()));

        //rv.setAlbumUri(data.getBanner());
        getActivity().setTitle(data.getTitle());

        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(data.getArtist_name());

        if (StringUtils.isNotBlank(data.getBanner())) {
            //设置高斯模糊
            //ImageUtil.showImageBlur(getActivity(), iv_album_bg, data.getBanner());
            final RequestOptions requestOptions = bitmapTransform(new BlurTransformation(50, 5));
            //requestOptions.placeholder(R.drawable.default_album);
            requestOptions.error(R.drawable.default_album);
            Glide.with(getActivity()).asDrawable().load(ImageUtil.getImageURI(data.getBanner())).apply(requestOptions).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    //使图片渐变而不是快速转变
                    AlbumDrawableUtil albumDrawableUtil = new AlbumDrawableUtil(iv_album_bg.getDrawable(), resource);
                    iv_album_bg.setImageDrawable(albumDrawableUtil.getDrawable());
                    albumDrawableUtil.start();
                }
            });
        }

        //if (data.getLyric() != null && StringUtils.isNotBlank(data.getLyric().getContent())) {
        //    fetchLyric();
        //} else {
        //    //直接设置歌词信息，存在于本地
        //    //setLyric();
        //}

        //scrollToCurrentSongPosition(currentSong);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onError(MediaPlayer mp, int what, int extra) {

    }
}
