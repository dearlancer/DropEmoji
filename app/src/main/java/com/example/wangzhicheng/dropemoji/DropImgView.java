package com.example.wangzhicheng.dropemoji;

import android.animation.TimeAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.Random;

/**
 * Created by wangzhicheng on 2017/2/27.
 */

public class DropImgView extends FrameLayout {
    private static final int BEZIER_COUNT = 10;
    private static final int POINT_COUNT = 4;

    DropImg.ViewPosInfo viewPosInfo;
    TimeAnimator mAnim;
    private ValueAnimator mAnimator;

    private Point[] mPoints = new Point[POINT_COUNT];

    static Random sRNG = new Random();

    static float lerp(float a, float b, float f) {
        return (b - a) * f + a;
    }

    static float randfrange(float a, float b) {
        return lerp(a, b, sRNG.nextFloat());
    }

    static float randSpeed(float speed) {
        return lerp(100, speed, sRNG.nextFloat());
    }

    public DropImgView(Context context, DropImg.ViewPosInfo viewPosInfo) {
        super(context);
        this.viewPosInfo = viewPosInfo;

    }

    public class FlyingImg extends ImageView {
        public float speed;

        public float dist;

        public float z;

        public FlyingImg(Context context, AttributeSet as) {
            super(context, as);
            setImageResource(viewPosInfo.imgId);
        }

        public void reset() {
            final float scale = lerp(0.5f, 1.2f, z);
            setScaleX(scale);
            setScaleY(scale);
            //setX(-scale * getWidth() + 1);
            setX(randfrange(0, DropImgView.this.getHeight() - scale * getHeight()));
            setY(randfrange(0, DropImgView.this.getHeight() - scale * getHeight()));
            speed = randSpeed(viewPosInfo.speed);
            dist = 0;
        }

        public void update(float dt) {
            dist += speed * dt;
            //setX(getX() + (speed * dt));
            // 根据Y轴漂移
            setY(getY() + randSpeed(speed) * dt);
        }
    }

    private void reset() {
        removeAllViews();
        final ViewGroup.LayoutParams wrap = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < viewPosInfo.num; i++) {
            FlyingImg nv = new FlyingImg(getContext(), null);
            addView(nv, wrap);
            nv.z = ((float) i / viewPosInfo.num);
            nv.z *= nv.z;
            nv.reset();
            nv.setX(randfrange(0, getWidth()));
        }
        if (mAnim != null) {
            mAnim.cancel();
        }
        mAnim = new TimeAnimator();
        mAnim.setTimeListener(new TimeAnimator.TimeListener() {
            public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime) {
                for (int i = 0; i < getChildCount(); i++) {
                    View v = getChildAt(i);
                    if (!(v instanceof FlyingImg))
                        continue;
                    FlyingImg nv = (FlyingImg) v;
                    nv.update(deltaTime / 200f);
                }
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        post(new Runnable() {
            public void run() {
                reset();
                mAnim.start();
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAnim.cancel();
    }
}
