package com.example.wangzhicheng.dropemoji;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.Random;

/**
 * Created by wangzhicheng on 2017/2/27.
 */

public class DropImgView extends FrameLayout {
    private int mWidth,mHeight;

    DropImg.ViewPosInfo viewPosInfo;
    private Interpolator line = new LinearInterpolator();//线性
    private Interpolator acc = new AccelerateInterpolator();//加速
    private Interpolator dce = new DecelerateInterpolator();//减速
    private Interpolator accdec = new AccelerateDecelerateInterpolator();//先加速后减速

    // 在init中初始化
    private Interpolator[] interpolators ;

    static Random sRNG = new Random();

    static float lerp(float a, float b, float f) {
        return (b - a) * f + a;
    }

    static float randfrange(float a, float b) {
        return lerp(a, b, sRNG.nextFloat());
    }
    public DropImgView(Context context, DropImg.ViewPosInfo viewPosInfo) {
        super(context);
        this.viewPosInfo = viewPosInfo;
        init();
    }

    public void addImg(){
        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(viewPosInfo.imgIdList.get(sRNG.nextInt(viewPosInfo.imgIdList.size())));
        final ViewGroup.LayoutParams wrap = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(imageView,wrap);
        getAnimator(imageView).start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        post(new Runnable() {
            public void run() {
                for (int i=0;i<viewPosInfo.num;i++)
                addImg();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    private void init(){
        // 初始化插补器
        interpolators = new Interpolator[4];
        interpolators[0] = line;
        interpolators[1] = acc;
        interpolators[2] = dce;
        interpolators[3] = accdec;
    }

    private ValueAnimator getBezierValueAnimator(final View target) {
        BezierEvaluator evaluator = new BezierEvaluator(getPointF(2),getPointF(1));
        ValueAnimator animator = ValueAnimator.ofObject(evaluator,new PointF(randfrange(0, mWidth),viewPosInfo.isRise?mHeight:0),new PointF(sRNG.nextInt(mWidth),viewPosInfo.isRise?0:mHeight));//随机
        animator.addUpdateListener(new BezierListenr(target));
        animator.setTarget(target);
        animator.setDuration(3000);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ViewGroup viewGroup= (ViewGroup) getParent();
                viewGroup.removeView(target);
            }
        });
        return animator;
    }

    private Animator getAnimator(View target){
        ValueAnimator bezierValueAnimator = getBezierValueAnimator(target);
        AnimatorSet finalSet = new AnimatorSet();
        finalSet.playSequentially(bezierValueAnimator);
        finalSet.setInterpolator(interpolators[sRNG.nextInt(4)]);//实现随机变速
        finalSet.setTarget(target);
        return finalSet;
    }
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }


    private PointF getPointF(int scale) {
        PointF pointF = new PointF();
        pointF.x = sRNG.nextInt((mWidth - 100));
        pointF.y = sRNG.nextInt((mHeight - 100))/scale;
        return pointF;
    }

    private class BezierListenr implements ValueAnimator.AnimatorUpdateListener {

        private View target;

        public BezierListenr(View target) {
            this.target = target;
        }
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            //这里获取到贝塞尔曲线计算出来的的x y值 赋值给view
            PointF pointF = (PointF) animation.getAnimatedValue();
            target.setX(pointF.x);
            target.setY(pointF.y);
            target.setAlpha(1-animation.getAnimatedFraction());
        }
    }

    public class BezierEvaluator implements TypeEvaluator<PointF> {
        private PointF pointF1;//途径的两个点
        private PointF pointF2;
        public BezierEvaluator(PointF pointF1,PointF pointF2){
            this.pointF1 = pointF1;
            this.pointF2 = pointF2;
        }
        @Override
        public PointF evaluate(float time, PointF startValue,
                               PointF endValue) {

            float timeLeft = 1.0f - time;
            PointF point = new PointF();//结果

            PointF point0 = (PointF)startValue;//起点

            PointF point3 = (PointF)endValue;//终点
            //代入公式
            point.x = timeLeft * timeLeft * timeLeft * (point0.x)
                    + 3 * timeLeft * timeLeft * time * (pointF1.x)
                    + 3 * timeLeft * time * time * (pointF2.x)
                    + time * time * time * (point3.x);

            point.y = timeLeft * timeLeft * timeLeft * (point0.y)
                    + 3 * timeLeft * timeLeft * time * (pointF1.y)
                    + 3 * timeLeft * time * time * (pointF2.y)
                    + time * time * time * (point3.y);
            return point;
        }
    }
}
