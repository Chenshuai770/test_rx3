package com.cs.test_rx3;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by chenshuai on 2016/11/17.
 */

public class MyAdapter extends PagerAdapter {
    private Context context;
    private List<View> mViews;
    private int size;

    // 轮播控件监听
    private MyViewListenner myViewListenner;
    private int vPonsiton;

    public MyAdapter(Context context, List<View> mViews, int size) {
        this.context = context;
        this.mViews = mViews;
        this.size = size;
    }

    public interface MyViewListenner{
        void onItemClick(int possiton);
    }

    public void setMyViewListenner(MyViewListenner listenner){
        this.myViewListenner=listenner;
    }
    public int getCount() {
        return mViews != null? mViews.size():0;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view=mViews.get(position);
        if (myViewListenner != null){
            view.setOnClickListener(new View.OnClickListener() {
                // 处理极端情况，此情况出现在轮播最后一张图切换到第一张图，ViewPaper实现轮播原理决定的。
                //因为view层次和数据层次进行分开处理了,所有需要额外写下
                @Override
                public void onClick(View view) {
                    vPonsiton = position;
                    //  java.lang.ArithmeticException: divide by zero size为0的情况下
                    if (vPonsiton >size && size!=0) {
                        vPonsiton = vPonsiton % size;
                    }
                    myViewListenner.onItemClick(vPonsiton);
                }
            });
        }
        container.addView(view);
        return view;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
       container.removeView((View) object);
    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }
}
