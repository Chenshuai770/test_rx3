package com.cs.test_rx3;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {
    private ViewPager viewpager;
    // 默认轮播时间
    private int delay = 3000;
    private MyAdapter myAdapter;
    // 轮播当前位置
    private int mCurrentPosition = 0;
    private LinearLayout ponit;
    private String[] Imgs = {
            "http://ww3.sinaimg.cn/large/610dc034jw1f9em0sj3yvj20u00w4acj.jpg",
            "http://ww3.sinaimg.cn/large/610dc034jw1f9nuk0nvrdj20u011haci.jpg",
            "http://ww3.sinaimg.cn/large/610dc034jw1f9rc3qcfm1j20u011hmyk.jpg",
            "http://ww3.sinaimg.cn/large/610dc034jw1f9em0sj3yvj20u00w4acj.jpg"
    };
    private int size;
    private List<View> mViews = new ArrayList<>();
    private ImageView imageView;
    private View view;
    private Subscription subscribe;
    private ImageView[] mIndicators;
    private int ponitpos;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        Log.d("TAG", size + "");
    }
    /**
     * 加载views
     * 加载url数据,
     * 绑定
     * 偷天换日
     */
    private void initView() {
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        ponit = (LinearLayout) findViewById(R.id.ll_ponit);
        myAdapter = new MyAdapter(this, mViews, size);
        viewpager.setAdapter(myAdapter);
        mViews.clear();
        //载入图片的时候需要用的item,默认用网络图片进行加载
        view = LayoutInflater.from(this).inflate(R.layout.item, null);
        imageView = (ImageView) view.findViewById(R.id.image);
        Glide.with(this)
                .load(Imgs[Imgs.length - 1])
                .centerCrop()
                .into(imageView);
        mViews.add(view);

        for (int i = 0; i < Imgs.length; i++) {
            view = LayoutInflater.from(this).inflate(R.layout.item, null);
            imageView = (ImageView) view.findViewById(R.id.image);
            Glide.with(this)
                    .load(Imgs[i])
                    .centerCrop()
                    .into(imageView);
            mViews.add(view);
        }

        view = LayoutInflater.from(this).inflate(R.layout.item, null);
        imageView = (ImageView) view.findViewById(R.id.image);
        Glide.with(this)
                .load(Imgs[0])
                .centerCrop()
                .into(imageView);
        mViews.add(view);
        myAdapter.notifyDataSetChanged();
        //加入小圆点
        size = Imgs.length;
        mIndicators = new ImageView[size];
        for (int i = 0; i < size; i++) {
            mIndicators[i] = new ImageView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(10,10);
            lp.setMargins(10, 0, 10, 0);
            mIndicators[i].setLayoutParams(lp);
            mIndicators[i].setBackgroundResource(R.drawable.selector_home_banner_points);
            ponit.addView(mIndicators[i]);

        }

        //进行监听
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int max = mViews.size() - 1;
                mCurrentPosition = position;
                if (position == 0) {
                    mCurrentPosition = max - 1;
                } else if (position == max) {
                    mCurrentPosition = 1;
                }
                ponitpos = mCurrentPosition - 1;
                int c = viewpager.getCurrentItem();
                Log.e("c",c+"");
                for (int i = 0; i <mViews.size(); i++) {
                    if(c==0||c==mViews.size()-1){
                        break;
                    }else{
                        c = c-1;
                        break;
                    }
                }
                /*switch (c){
                    case 0:
                        break;
                    case 1:
                        c =0;
                        break;
                    case 2:
                        c = 1;
                        break;
                    case 3:
                        c =2;
                        break;
                    case 4:
                        break;
                }*/
                Log.e("c",c+"");

                // TODO: 2016/11/17  这里实现的方式有两种,从不同思路去实现,有兴趣可以参考以上代码,新手会遇到坑
                for (int i = 0; i < mIndicators.length; i++) {
                    //mIndicators[i].setSelected(c==i);
                    mIndicators[i].setSelected(ponitpos==i);
                }

            }
            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 0) {
                    viewpager.setCurrentItem(mCurrentPosition, false);
                }
            }
        });

        //调用rxjava来执行
        subscribe = Observable.interval(delay, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        mCurrentPosition++;
                        if (viewpager != null)
                            viewpager.setCurrentItem(mCurrentPosition, false);
                    }
                });
        viewpager.setCurrentItem(1, false);
        // 初始化指示器图片集合
        myAdapter.setMyViewListenner(new MyAdapter.MyViewListenner() {
            @Override
            public void onItemClick(int possiton) {
                Toast.makeText(MainActivity.this, "你点击了第" + possiton + "个", Toast.LENGTH_SHORT).show();
            }
        });
    }
    //    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_DOWN:
                // 手指按下或者滑动的过程中停止轮播
                // 手指按下或者滑动的过程中停止轮播
                subscribe.unsubscribe();
                break;
            case MotionEvent.ACTION_UP:
                // 手指离开屏幕开始轮播
                subscribe = Observable.interval(delay, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long aLong) {
                                mCurrentPosition++;
                                if (viewpager != null)
                                    viewpager.setCurrentItem(mCurrentPosition, false);
                            }
                        });

                break;
        }
        return super.dispatchTouchEvent(ev);
    }

}
