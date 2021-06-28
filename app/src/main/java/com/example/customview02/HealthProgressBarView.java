package com.example.customview02;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Email 3301360040@qq.com
 * Created by ZheYu on 2021/6/28.
 * Description: 线性渐变进度条
 */
public class HealthProgressBarView extends View {
    private Paint mCurrentPaint;
    private Paint mTotalPaint;
    private final int DEFAULT_SIZE = 8;
    private int mProgressSize = DEFAULT_SIZE;
    private HealthProgressListener mListener;
    private int mProgressMargin;
    private int mCurrentStartColor = Color.parseColor("#5CDCAD");
    private int mCurrentEndColor = Color.parseColor("#1AB77E");
    private int mTotalColor = Color.parseColor("#F8F8F8");
    private LinearGradient mLinearGradient; //线性渐变色
    private final int DEFAULT_DURATION_TIME = 2000;
    private int mDurationTime = DEFAULT_DURATION_TIME;
    private Activity mActivity;
    private RegisterProgressLifecycleCallbacks mLifecycleCallback = new RegisterProgressLifecycleCallbacks();
    private ValueAnimator mAnimator;
    private float mCurrentProgress; // 初始进度
    private float mTotalSize = 360f; // 总进度

    public HealthProgressBarView(Context context) {
        this(context, null);
    }

    public HealthProgressBarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HealthProgressBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mActivity = (Activity) context;
        mActivity.getApplication().registerActivityLifecycleCallbacks(mLifecycleCallback);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.HealthProgressView);
        mProgressSize = (int) array.getDimension(R.styleable.HealthProgressView_progress_size, dip2px(mProgressSize));
        mCurrentStartColor = array.getColor(R.styleable.HealthProgressView_progress_current_start_color, mCurrentStartColor);
        mCurrentEndColor = array.getColor(R.styleable.HealthProgressView_progress_current_end_color, mCurrentEndColor);
        mTotalColor = array.getColor(R.styleable.HealthProgressView_progress_total_color, mTotalColor);
        mDurationTime = array.getInt(R.styleable.HealthProgressView_progress_duration_time, mDurationTime);
        array.recycle();
        mProgressMargin = mProgressSize / 2;

        // 当前进度画笔
        mCurrentPaint = new Paint();
        mCurrentPaint.setAntiAlias(true);
        mCurrentPaint.setStrokeWidth(mProgressSize);
        mCurrentPaint.setStrokeCap(Paint.Cap.ROUND);
        mCurrentPaint.setStyle(Paint.Style.STROKE);

        // 总进度画笔
        mTotalPaint = new Paint();
        mTotalPaint.setAntiAlias(true);
        mTotalPaint.setStrokeWidth(mProgressSize);
        mTotalPaint.setColor(mTotalColor);
        mTotalPaint.setStrokeCap(Paint.Cap.ROUND);
        mTotalPaint.setStyle(Paint.Style.STROKE);
    }

    private int dip2px(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, mProgressSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (null == mLinearGradient) {
            mLinearGradient = new LinearGradient(0, 0, (getMeasuredWidth()) - mProgressMargin,
                    0, mCurrentStartColor, mCurrentEndColor, Shader.TileMode.MIRROR);
            mCurrentPaint.setShader(mLinearGradient);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(mProgressMargin, mProgressMargin, getWidth() - mProgressMargin, mProgressMargin, mTotalPaint);
        if (mCurrentProgress == 0) return;
        float rate = (getWidth() * (mCurrentProgress / mTotalSize)) - mProgressMargin;
        canvas.drawLine(mProgressMargin, mProgressMargin, rate, mProgressMargin, mCurrentPaint);
        invalidate();
    }

    public synchronized void setHealthProgressBar(float progressRatio, float totalSize) {
        this.mTotalSize = totalSize;
        mAnimator = ObjectAnimator.ofFloat(0, progressRatio);
        mAnimator.setDuration(mDurationTime);
        mAnimator.setInterpolator(new DecelerateInterpolator());
        mAnimator.addUpdateListener(animation -> {
            mCurrentProgress = (float) animation.getAnimatedValue();
            if (mListener != null) {
                mListener.currentProgress(Math.round((mCurrentProgress / totalSize) * 100));
            }
        });
    }

    public interface HealthProgressListener {
        void currentProgress(int progress);
    }

    public void setOnProgressChangedListener(HealthProgressListener listener) {
        this.mListener = listener;
    }

    private class RegisterProgressLifecycleCallbacks extends DefaultActivityLifecycleCallbacks {
        @Override
        public void onActivityResumed(@NonNull Activity activity) {
            super.onActivityResumed(activity);
            if (mActivity == activity) {
                mAnimator.start();
            }
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {
            super.onActivityPaused(activity);
            if (mActivity == activity) {
                mAnimator.cancel();
            }
        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
            super.onActivityDestroyed(activity);
            if (mActivity == activity) {
                mAnimator.cancel();
                mActivity.getApplication().unregisterActivityLifecycleCallbacks(mLifecycleCallback);
            }
        }
    }
}
