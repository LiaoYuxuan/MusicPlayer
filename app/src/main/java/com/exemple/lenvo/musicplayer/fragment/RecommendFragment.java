package com.exemple.lenvo.musicplayer.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.exemple.lenvo.musicplayer.R;
import com.exemple.lenvo.musicplayer.activity.BaseWebViewActivity;
import com.exemple.lenvo.musicplayer.activity.MusicPlayerActivity;
import com.exemple.lenvo.musicplayer.adapter.BaseRecyclerViewAdapter;
import com.exemple.lenvo.musicplayer.adapter.RecommendAdapter;
import com.exemple.lenvo.musicplayer.api.Api;
import com.exemple.lenvo.musicplayer.domain.Advertisement;
import com.exemple.lenvo.musicplayer.domain.List;
import com.exemple.lenvo.musicplayer.domain.Song;
import com.exemple.lenvo.musicplayer.domain.response.ListResponse;
import com.exemple.lenvo.musicplayer.reactivex.HttpListener;
import com.exemple.lenvo.musicplayer.util.DataUtil;
import com.exemple.lenvo.musicplayer.util.ImageUtil;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.Calendar;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/*
 *   项目名：  Musicplayer
 *   包名：    com.exemple.lenvo.musicplayer.fragment
 *   文件名：  MeFragment
 *   创建者：  LYX
 *   创建时间：2019/2/4 12:20
 *   描述：    TODO
 */
public class RecommendFragment extends BaseCommonFragment implements OnBannerListener {

    private LRecyclerView rv;
    private RecommendAdapter adapter;
    private LRecyclerViewAdapter adapterWrapper;
    private GridLayoutManager layoutManager;
    private Banner banner;
    private LinearLayout ll_day_container;
    private TextView tv_day;
    private java.util.List<Advertisement> bannerData;
    //private PlayListManager playListManager;

    public static RecommendFragment newInstance() {

        Bundle args = new Bundle();

        RecommendFragment fragment = new RecommendFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initViews() {
        super.initViews();
        rv=findViewById(R.id.rv);
        rv.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(getActivity(), 3);
        rv.setLayoutManager(layoutManager);

        //layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
        //    @Override
        //    public int getSpanSize(int position) {
        //        //                先获取ItemType
        //        int itemViewType = getItemViewType(position);
        //        if (getHeaderView(itemViewType) != null || getFooterView(itemViewType) != null) {
        //            //                    当前位置的Item是header，占用列数spanCount一样
        //            return ((GridLayoutManager) layoutManager).getSpanCount();
        //        }
        //        //其他情况，表示正常的数据Item，暂用一列
        //        return adapter.setSpanSizeLookup(getItemRealPosition(position));
        //    }
        //});


    }

    @Override
    public void onStart() {
        super.onStart();
        //开始轮播
        banner.startAutoPlay();
    }

    @Override
    public void onStop() {
        super.onStop();
        //结束轮播
        banner.stopAutoPlay();
    }

    @Override
    protected void initDatas() {
        super.initDatas();
//        playListManager = MusicPlayerService.getPlayListManager(getActivity().getApplicationContext());

        adapter = new RecommendAdapter(getActivity());
        adapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerViewAdapter.ViewHolder holder, int position) {
//                Object data =  adapter.getData(position);
//                if (data instanceof Song) {
//                    //单曲
//                    ArrayList<Song> list = new ArrayList<>();
//                    list.add((Song) data);
//                    playListManager.setPlayList(list);
//                    playListManager.play((Song) data);
//                    startActivity(MusicPlayerActivity.class);
//                } else if (data instanceof List) {
//                    //歌单
//                    startActivityExtraId(ListDetailActivity.class,((List) data).getId());
//                } else if (data instanceof Advertisement) {
//                    //广告
//                    BaseWebViewActivity.start(getMainActivity(),((Advertisement) data).getTitle(),((Advertisement) data).getUri());
//                }
                startActivity(MusicPlayerActivity.class);
            }
        });

        adapterWrapper = new LRecyclerViewAdapter(adapter);
        adapterWrapper.setSpanSizeLookup(new LRecyclerViewAdapter.SpanSizeLookup() {
            @Override
            public int getSpanSize(GridLayoutManager gridLayoutManager, int position) {
                //                先获取ItemType
                int itemViewType = adapterWrapper.getItemViewType(position);
                if (position<adapterWrapper.getHeaderViewsCount() || position>(adapterWrapper.getHeaderViewsCount()+adapter.getItemCount())) {
                    //                    f当前位置的Item是header，占用列数spanCount一样
                    return ((GridLayoutManager) layoutManager).getSpanCount();
                }
                return adapter.setSpanSizeLookup(position);
            }
        });


        adapterWrapper.addHeaderView(createHeaderView());

        rv.setAdapter(adapterWrapper);

        //禁用下拉刷新和上拉加载，注意放置的位置
        rv.setPullRefreshEnabled(false);
        rv.setLoadMoreEnabled(false);

        fetchData();
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        fetchBannerData();
    }

    private void fetchBannerData() {
        Api.getInstance().advertisements().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpListener<ListResponse<Advertisement>>(getMainActivity()){
                    @Override
                    public void onSucceeded(ListResponse<Advertisement> data) {
                        super.onSucceeded(data);
                        showBanner(data.getData());
                    }
                });

    }

    private void showBanner(java.util.List<Advertisement> data) {
        //            //设置图片集合
        this.bannerData=data;
        banner.setImages(data);
        banner.start();
    }


    private View createHeaderView() {
        View top = getLayoutInflater().inflate(R.layout.header_music_recommend, (ViewGroup) rv.getParent(), false);
        banner = top.findViewById(R.id.banner);
        banner.setOnBannerListener(this);

        ll_day_container = top.findViewById(R.id.ll_day_container);
        tv_day = top.findViewById(R.id.tv_day);
        //rl_day_container = top.findViewById(R.id.rl_day_container);

        //设置日期
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        tv_day.setText(String.valueOf(day));

        //还有一个3D反转动画，这里就不设置了，详细的查看《详解Animation》课程
        //ll_day_container.setOnClickListener(this);

        return top;
    }

    private void fetchData() {
        //这里获取三种类型的数据，然后放到一个列表中
        //同时也是演示RecyclerView不同的ItemType的使用方法
        //详细的使用方法可以在我们的《详解RecyclerView》课程中学到

        Observable<ListResponse<List>> list = Api.getInstance().lists();
        final Observable<ListResponse<Song>> songs = Api.getInstance().songs();
        final Observable<ListResponse<Advertisement>> advertisements = Api.getInstance().advertisements();

        final ArrayList<Object> d = new ArrayList<>();
        d.add("推荐歌单");

        //为降低课程难度，不使用RxJava来合并请求
        list.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpListener<ListResponse<List>>(getMainActivity()) {
                    @Override
                    public void onSucceeded(final ListResponse<List> data) {
                        super.onSucceeded(data);
                        d.addAll(data.getData());

                        songs.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new HttpListener<ListResponse<Song>>(getMainActivity()) {
                                    @Override
                                    public void onSucceeded(ListResponse<Song> data) {
                                        super.onSucceeded(data);
                                        d.add("推荐单曲");
                                        d.addAll(DataUtil.fill(data.getData()));

                                        advertisements.subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new HttpListener<ListResponse<Advertisement>>(getMainActivity()){
                                                    @Override
                                                    public void onSucceeded(ListResponse<Advertisement> data) {
                                                        super.onSucceeded(data);
                                                        d.addAll(data.getData());

                                                        adapter.setData(d);
                                                        //rv.refreshComplete(Consts.DEFAULT_PAGE_SIZE);
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }

    @Override
    protected View getLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recommend,null);
    }

    @Override
    public void OnBannerClick(int position) {
        Advertisement advertisement = bannerData.get(position);
        //BaseWebViewActivity.start(getMainActivity(),"活动详情",banner.getUri());
        BaseWebViewActivity.start(getMainActivity(),"活动详情","http://www.ixuea.com");
    }

    public class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            //因为引入了一个Banner控件，所有这里要使用全类名
            Advertisement banner = (Advertisement) path;
            ImageUtil.show(getMainActivity(), imageView, banner.getBanner());
        }
    }
}
