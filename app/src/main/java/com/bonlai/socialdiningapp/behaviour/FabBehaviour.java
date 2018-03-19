package com.bonlai.socialdiningapp.behaviour;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by Bon Lai on 20/3/2018.
 */

public class FabBehaviour extends CoordinatorLayout.Behavior<FloatingActionButton> {
    private static final String TAG = "ScrollingFABBehavior";
    Handler mHandler;

    public FabBehaviour(Context context, AttributeSet attrs) {
        super();
    }

    public FabBehaviour() {
        super();
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
        Log.d(TAG,"onNestedScroll");
        Log.d(TAG,(String.valueOf(dyConsumed)));

        if (dyConsumed > 0 ) {
            child.animate().alpha(0.0f).setDuration(500);
            //child.setVisibility(View.INVISIBLE);
            Log.d(TAG,"scroll down");
        } else if (dyConsumed < 0 ) {
            child.animate().alpha(1.0f).setDuration(500);;
            //child.setVisibility(View.VISIBLE);
            Log.d(TAG,"scroll up");
        }
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }


}