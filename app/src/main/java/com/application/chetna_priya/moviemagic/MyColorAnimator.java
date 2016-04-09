package com.application.chetna_priya.moviemagic;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;

/**
 * Created by chetna_priya on 4/7/2016.
 */
public class MyColorAnimator implements ValueAnimator.AnimatorUpdateListener, ValueAnimator.AnimatorListener {

    private Context mContext;
    private View itemView;
    private ValueAnimator animator;
    private AnimationEndCallback mCallback;

    public MyColorAnimator(Context context,ValueAnimator animator,View itemView, AnimationEndCallback callback)
    {
        this.mContext = context;
        this.animator = animator;
        this.itemView = itemView;
        try {
            mCallback = callback;
        }catch (ClassCastException e)
        {
            throw new ClassCastException(mContext.toString()
                    + " must implement OnItemSelectedListener");
        }
    }

    public interface AnimationEndCallback
    {
        void onAnimationEnd();
    }
    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        mCallback.onAnimationEnd();
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        itemView.setBackgroundColor((int) animator.getAnimatedValue());
    }
}
