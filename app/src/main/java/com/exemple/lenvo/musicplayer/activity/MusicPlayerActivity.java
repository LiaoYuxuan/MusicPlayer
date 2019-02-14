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
import com.exemple.lenvo.musicplayer.domain.Lyric;
import com.exemple.lenvo.musicplayer.domain.Song;
import com.exemple.lenvo.musicplayer.listener.OnLyricClickListener;
import com.exemple.lenvo.musicplayer.listener.OnMusicPlayerListener;
import com.exemple.lenvo.musicplayer.manager.MusicPlayerManager;
import com.exemple.lenvo.musicplayer.parser.LyricsParser;
import com.exemple.lenvo.musicplayer.service.MusicPlayerService;
import com.exemple.lenvo.musicplayer.util.AlbumDrawableUtil;
import com.exemple.lenvo.musicplayer.util.ImageUtil;
import com.exemple.lenvo.musicplayer.util.TimeUtil;
import com.exemple.lenvo.musicplayer.view.LyricView;
import com.exemple.lenvo.musicplayer.view.RecordThumbView;
import com.exemple.lenvo.musicplayer.view.RecordView;

import org.apache.commons.lang3.StringUtils;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class MusicPlayerActivity extends BaseTitleActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, OnMusicPlayerListener, OnLyricClickListener {

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
    private LyricView lv;

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

       // musicPlayerManager.play("http://dev-courses-misuc.ixuea.com/assets/s1.mp3",new Song());
        musicPlayerManager.play("http://dev-courses-misuc.ixuea.com/assets/s2.mp3",new Song());
        //musicPlayerManager.play("https://api.bzqll.com/music/netease/url?key=579621905&id=526307800&br=999000",new Song());

//        Lyric lyric = new Lyric();
//        lyric.setStyle(10);
//        lyric.setContent("karaoke := CreateKaraokeObject;\nkaraoke.rows := 2;\nkaraoke.TimeAfterAnimate := 2000;\nkaraoke.TimeBeforeAnimate := 4000;\nkaraoke.clear;\nkaraoke.add('00:20.699', '00:27.055', '[●●●●●●]', '7356',RGB(255,0,0));\n\nkaraoke.add('00:27.487', '00:32.068', '一时失志不免怨叹', '347,373,1077,320,344,386,638,1096');\nkaraoke.add('00:33.221', '00:38.068', '一时落魄不免胆寒', '282,362,1118,296,317,395,718,1359');\nkaraoke.add('00:38.914', '00:42.164', '那通失去希望', '290,373,348,403,689,1147');\nkaraoke.add('00:42.485', '00:44.530', '每日醉茫茫', '298,346,366,352,683');\nkaraoke.add('00:45.273', '00:49.029', '无魂有体亲像稻草人', '317,364,380,351,326,351,356,389,922');\nkaraoke.add('00:50.281', '00:55.585', '人生可比是海上的波浪', '628,1081,376,326,406,371,375,1045,378,318');\nkaraoke.add('00:56.007', '01:00.934', '有时起有时落', '303,362,1416,658,750,1438');\nkaraoke.add('01:02.020', '01:04.581', '好运歹运', '360,1081,360,760');\nkaraoke.add('01:05.283', '01:09.453', '总嘛要照起来行', '303,338,354,373,710,706,1386');\nkaraoke.add('01:10.979', '01:13.029', '三分天注定', '304,365,353,338,690');\nkaraoke.add('01:13.790', '01:15.950', '七分靠打拼', '356,337,338,421,708');\nkaraoke.add('01:16.339', '01:20.870', '爱拼才会赢', '325,1407,709,660,1430');\nkaraoke.add('01:33.068', '01:37.580', '一时失志不免怨叹', '307,384,1021,363,357,374,677,1029');\nkaraoke.add('01:38.660', '01:43.656', '一时落魄不免胆寒', '381,411,1067,344,375,381,648,1389');\nkaraoke.add('01:44.473', '01:47.471', '那通失去希望', '315,365,340,369,684,925');\nkaraoke.add('01:48.000', '01:50.128', '每日醉茫茫', '338,361,370,370,689');\nkaraoke.add('01:50.862', '01:54.593', '无魂有体亲像稻草人', '330,359,368,376,325,334,352,389,898');\nkaraoke.add('01:55.830', '02:01.185', '人生可比是海上的波浪', '654,1056,416,318,385,416,373,1032,342,363');\nkaraoke.add('02:01.604', '02:06.716', '有时起有时落', '303,330,1432,649,704,1694');\nkaraoke.add('02:07.624', '02:10.165', '好运歹运', '329,1090,369,753');\nkaraoke.add('02:10.829', '02:15.121', '总嘛要照起来行', '313,355,362,389,705,683,1485');\nkaraoke.add('02:16.609', '02:18.621', '三分天注定', '296,363,306,389,658');\nkaraoke.add('02:19.426', '02:21.428', '七分靠打拼', '330,359,336,389,588');\nkaraoke.add('02:21.957', '02:26.457', '爱拼才会赢', '315,1364,664,767,1390');\nkaraoke.add('02:50.072', '02:55.341', '人生可比是海上的波浪', '656,1086,349,326,359,356,364,1095,338,340');\nkaraoke.add('02:55.774', '03:01.248', '有时起有时落', '312,357,1400,670,729,2006');\nkaraoke.add('03:01.787', '03:04.369', '好运歹运', '341,1084,376,781');\nkaraoke.add('03:05.041', '03:09.865', '总嘛要起工来行', '305,332,331,406,751,615,2084');\nkaraoke.add('03:10.754', '03:12.813', '三分天注定', '309,359,361,366,664');\nkaraoke.add('03:13.571', '03:15.596', '七分靠打拼', '320,362,349,352,642');\nkaraoke.add('03:16.106', '03:20.688', '爱拼才会赢', '304,1421,661,706,1490');\n");
//        setLyric(lyric);

        Lyric lyric = new Lyric();
        lyric.setStyle(0);
        lyric.setContent("[ti:爱的代价]\n[ar:李宗盛]\n[al:滚石香港黄金十年 李宗盛精选]\n[ly:李宗盛]\n[mu:李宗盛]\n[ma:]\n[pu:]\n[by:ttpod]\n[total:272073]\n[offset:0]\n[00:00.300]爱的代价 - 李宗盛\n[00:01.979]作词：李宗盛\n[00:03.312]作曲：李宗盛\n[00:06.429]\n[00:16.282]还记得年少时的梦吗\n[00:20.575]像朵永远不调零的花\n[00:24.115]陪我经过那风吹雨打\n[00:27.921]看世事无常\n[00:29.653]看沧桑变化\n[00:32.576]那些为爱所付出的代价\n[00:36.279]是永远都难忘的啊\n[00:40.485]所有真心的痴心的话\n[00:43.779]永在我心中虽然已没有他\n[00:50.073]走吧 走吧\n[00:54.868]人总要学着自己长大\n[00:58.829]走吧 走吧\n[01:02.616]人生难免经历苦痛挣扎\n[01:06.316]走吧 走吧\n[01:10.795]为自己的心找一个家\n[01:14.399]也曾伤心流泪\n[01:16.742]也曾黯然心碎\n[01:18.845]这是爱的代价\n[01:21.579]\n[01:40.358]也许我偶尔还是会想他\n[01:44.553]偶尔难免会惦记着他\n[01:48.378]就当他是个老朋友啊\n[01:51.891]也让我心疼也让我牵挂\n[01:56.617]只是我心中不再有火花\n[02:00.507]让往事都随风去吧\n[02:04.660]所有真心的痴心的话\n[02:07.625]仍在我心中\n[02:09.563]虽然已没有他\n[02:14.454]走吧 走吧\n[02:18.580]人总要学着自己长大\n[02:24.499]走吧 走吧\n[02:26.586]人生难免经历苦痛挣扎\n[02:30.293]走吧 走吧\n[02:34.828]为自己的心找一个家\n[02:38.482]也曾伤心流泪\n[02:40.767]也曾黯然心碎\n[02:42.742]这是爱的代价\n[02:45.509]\n[03:22.502]走吧 走吧\n[03:26.581]人总要学着自己长大\n[03:32.414]走吧 走吧\n[03:34.496]人生难免经历苦痛挣扎\n[03:40.425]走吧 走吧\n[03:42.616]为自己的心找一个家\n[03:46.398]也曾伤心流泪\n[03:48.852]也曾黯然心碎\n[03:50.645]这是爱的代价\n");
//        lyric.setContent("[00:00.00] 作曲 : 江潮\n" +
//                "[00:01.00] 作词 : 孟君酱\n" +
//                "[00:03.37]编曲：江潮\n" +
//                "[00:04.75]吉他：蔡剑\n" +
//                "[00:06.34]和声：江潮\n" +
//                "[00:07.81]rap：江潮\n" +
//                "[00:09.11]混音：ET\n" +
//                "[00:10.31]制作人：江潮\n" +
//                "[00:11.50]出品：新乐尘符\n" +
//                "[00:12.70]轻轻贴近你的耳朵\n" +
//                "[00:16.00]莎朗嘿哟\n" +
//                "[00:18.03]情话永远不嫌太多\n" +
//                "[00:20.34]对你说\n" +
//                "[00:24.00]一全听你的\n" +
//                "[00:26.69]二给你好的\n" +
//                "[00:28.90]数到三永远爱你一个\n" +
//                "[00:34.67]四不会犯错\n" +
//                "[00:37.35]五不会啰嗦\n" +
//                "[00:39.64]每天为你打call\n" +
//                "[00:41.35]cook也不错\n" +
//                "[00:44.68]轻轻贴近你的耳朵\n" +
//                "[00:47.98]莎朗嘿哟\n" +
//                "[00:49.98]情话永远不嫌太多\n" +
//                "[00:52.31]对你说\n" +
//                "[00:55.33]打开你的爱情手册\n" +
//                "[00:58.66]就在此刻\n" +
//                "[01:00.68]为你唱的专属情歌\n" +
//                "[01:03.00]要记得\n" +
//                "[01:12.14]\n" +
//                "[01:17.32]说你想说的\n" +
//                "[01:20.00]做你想做的\n" +
//                "[01:22.29]别怕失败因为你有我\n" +
//                "[01:28.01]陪你看日落\n" +
//                "[01:30.65]陪你等雨过\n" +
//                "[01:32.96]陪你一起唱完\n" +
//                "[01:34.66]我们爱的歌\n" +
//                "[01:38.01]轻轻贴近你的耳朵\n" +
//                "[01:41.33]莎朗嘿哟\n" +
//                "[01:43.32]情话永远不嫌太多\n" +
//                "[01:45.63]对你说\n" +
//                "[01:48.64]打开你的爱情手册\n" +
//                "[01:51.94]就在此刻\n" +
//                "[01:53.98]为你唱的专属情歌\n" +
//                "[01:56.30]要记得\n" +
//                "[01:59.71]江潮：\n" +
//                "[02:10.34]我轻轻靠近你的耳朵 说爱你不嫌太多\n" +
//                "[02:13.12]如果相遇的几率 亿万分之一那么多\n" +
//                "[02:15.85]请相信我的真真真心比宇宙还辽阔\n" +
//                "[02:18.54]我会牵着你的手 直到你全部接受\n" +
//                "[02:20.91]我靠近你的耳朵\n" +
//                "[02:23.98]莎朗嘿哟 hu~\n" +
//                "[02:31.32]打开你的爱情手册\n" +
//                "[02:34.63]就在此刻\n" +
//                "[02:36.65]为你唱的专属情歌\n" +
//                "[02:38.94]要记得\n" +
//                "[02:42.67]我轻轻靠近你的耳朵 说爱你不嫌太多\n" +
//                "[02:45.22]如果相遇的几率 亿万分之一那么多\n" +
//                "[02:47.86]请相信我的真真真心比宇宙还辽阔\n" +
//                "[02:50.61]我会牵着你的手 直到你全部接受\n" +
//                "[02:53.05]打开你的爱情手册\n" +
//                "[02:55.97]就在这一刻\n" +
//                "[02:57.99]这首专属情歌\n" +
//                "[03:00.30]请记得\n" +
//                "[03:13.44]");
        setLyric(lyric);
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

        lv.setOnClickListener(this);
        rv.setOnClickListener(this);

        sb_progress.setOnSeekBarChangeListener(this);
        sb_volume.setOnSeekBarChangeListener(this);

        //lv.setOnLongClickListener(this);

        //lv.setLyricListener(this);

        lv.setOnLyricClickListener(this);
        //playListManager.addPlayListListener(this);

        //vp.addOnPageChangeListener(this);
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
            case R.id.lv:
                lyric_container.setVisibility(View.GONE);
                rl_player_container.setVisibility(View.VISIBLE);
                break;
            case R.id.rv:
                lyric_container.setVisibility(View.VISIBLE);
                rl_player_container.setVisibility(View.GONE);
                break;
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
        //musicPlayerManager.play("https://api.bzqll.com/music/netease/url?key=579621905&id=526307800&br=999000",new Song());
        musicPlayerManager.resume();
    }

    private void pause() {
        //playListManager.pause();
        stopRecordRotate();
        musicPlayerManager.pause();
    }

    private void setLyric(Lyric lyric) {
        LyricsParser parser = LyricsParser.parse(lyric.getStyle(), lyric.getContent());
        parser.parse();
        if (parser.getLyric() != null) {
            lv.setData(parser.getLyric());
        }
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
        lv.show(progress);
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

        //测试使用，记得改回来
        sb_progress.setMax((int) data.getDuration());
        //sb_progress.setMax(MusicPlayerManagerImpl.player.getDuration());
       //sb_progress.setMax(Song);
        sb_progress.setProgress(sp.getLastSongProgress());
        tv_start_time.setText(TimeUtil.formatMSTime((int) sp.getLastSongProgress()));
        //tv_end_time.setText(TimeUtil.formatMSTime(MusicPlayerManagerImpl.player.getDuration()));
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

    @Override
    public void onLyricClick(long time) {
        musicPlayerManager.seekTo((int) time);
        if (!musicPlayerManager.isPlaying()) {
            musicPlayerManager.resume();
        }
    }
}
