package com.example.wangzhicheng.dropemoji;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wangzhicheng on 2017/2/27.
 */

public class DropImg {

    private final Context mContext;
    private View mAnchor;
    ViewPosInfo viewPosInfo;

    public static class ViewPosInfo{   //用来保存一些位置信息
        public int imgId=-1;    //表情源文件
        public List<Integer>imgIdList=new ArrayList<>();  //表情id管理
        public int num=20;   //表情数量
        public boolean isRise=true;
    }

    public DropImg(Context context,int imgId) {
        this.mContext = context;
        mAnchor = ((Activity) mContext).findViewById(android.R.id.content);
        viewPosInfo=new ViewPosInfo();
        viewPosInfo.imgIdList.add(imgId);
    }

    public DropImg addImg(int id){
        viewPosInfo.imgIdList.add(id);
        return this;
    }

    public DropImg addImg(List<Integer> list){
        viewPosInfo.imgIdList.addAll(list);
        return this;
    }

    public DropImg setImgNum(int num){
        viewPosInfo.num=num;
        return this;
    }
    public DropImg setRise(boolean rise){
        viewPosInfo.isRise=rise;
        return this;
    }

    public void show(){
        DropImgView dropImgView=new DropImgView(mContext,viewPosInfo);
        if (mAnchor instanceof FrameLayout){
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            ((ViewGroup) mAnchor).addView(dropImgView, ((ViewGroup) mAnchor).getChildCount(), lp);
        }else{
            //否则先将根view的子view替换为framelayout，然后将原来的子view添加到framelayout上
            FrameLayout frameLayout = new FrameLayout(mContext);
            ViewGroup parent = (ViewGroup) mAnchor.getParent();
            parent.removeView(mAnchor);
            parent.addView(frameLayout, mAnchor.getLayoutParams());
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            frameLayout.addView(mAnchor, lp);
            frameLayout.addView(dropImgView);
        }
    }
}
