package com.awesome.fling.tv;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class VideoOverlay extends ImageView implements SplashListener
{
    private AnimatorSet objectAnimatorSet = new AnimatorSet();

    public VideoOverlay(Context context)
    {
        super(context);
        init(null);
    }

    public VideoOverlay(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs);
    }

    public VideoOverlay(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs)
    {
        int animatorId = -1;

        if (attrs != null)
        {
            TypedArray attrArray = getContext().obtainStyledAttributes(attrs, R.styleable.VideoOverlay);
            final int count = attrArray.getIndexCount();
            for (int i = 0; i < count; ++i)
            {
                int attrIndex = attrArray.getIndex(i);
                switch (attrIndex)
                {
                    case R.styleable.VideoOverlay_object_animator:
                        animatorId = attrArray.getResourceId(attrIndex, -1);
                        break;
                }
            }
            attrArray.recycle();
        }

        if (animatorId != -1)
        {
            objectAnimatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), animatorId);

        }

        Splasher splasher = new Splasher(this);
        splasher.setSplashListener(this);
        objectAnimatorSet.addListener(splasher);
        objectAnimatorSet.setTarget(this);
    }

    public void onTomatoThrown()
    {
        setVisibility(View.VISIBLE);
        objectAnimatorSet.start();
    }

    @Override
    public void onSplashFinished()
    {
        setImageResource(R.drawable.tomato);
    }
}
