package com.android.hutils.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.hutils.R;

/**
 * Created by huangbei on 19-8-25.
 */

public class StickMovableLayout extends LinearLayout implements NestedScrollingParent{

    private View mHead;
    private View mBody;
    private View mStick;

    private int mHeadId = -1;
    private int mBodyId = -1;
    private int mStickId = -1;

    private int mMaxScrollTop = -1;
    private int mOriginBottom = -1;

    public StickMovableLayout(Context context) {
        this(context,null);
    }

    public StickMovableLayout(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public StickMovableLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(attrs != null){
            TypedArray array = null;
            try{
                array = getContext().obtainStyledAttributes(attrs, R.styleable.StickMovableLayout);
                mHeadId = array.getResourceId(R.styleable.StickMovableLayout_head, 0);
                mBodyId = array.getResourceId(R.styleable.StickMovableLayout_body, 0);
                mStickId = array.getResourceId(R.styleable.StickMovableLayout_stick, 0);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        this.setOrientation(LinearLayout.VERTICAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mHeadId != 0) {
            mHead = findViewById(mHeadId);
        } else {
            mHead = getChildAt(0);
        }
        if (mBodyId != 0) {
            mBody = findViewById(mBodyId);
        } else {
            mBody = getChildAt(1);
        }
        if (mStickId != 0) {
            mStick = findViewById(mStickId);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(mMaxScrollTop == -1){
            if(mStick != null) {
                mMaxScrollTop = mStick.getMeasuredHeight();
            }else{
                mMaxScrollTop = 0;
            }
        }

        if(mOriginBottom == -1){
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mHead.getLayoutParams();
            mOriginBottom = params.topMargin + mHead.getMeasuredHeight() + params.bottomMargin;
        }
    }

    //决定parent是否消费滑动事件
    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        //只处理垂直方向的滑动
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL)!=0;
    }

    //parent消费滑动事件
    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        //向上滑动,若head未隐藏，则parent消费滑动事件
        if(dy>0 && mHead.getBottom()>mMaxScrollTop){
            int maxoffset = mHead.getBottom() - mMaxScrollTop;
            int offset = Math.min(maxoffset,dy);
            mHead.offsetTopAndBottom(offset*-1);
            mBody.layout(mBody.getLeft(), mBody.getTop()-offset, mBody.getRight(), mBody.getBottom());
            consumed[1]=offset;
        }
        super.onNestedPreScroll(target, dx, dy, consumed);
    }

    //parent消费滑动事件
    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        //向下滑动,先让子view消费，然后若head隐藏，则parent继续消费滑动事件
        if(dyUnconsumed<0 && mHead.getBottom()<mOriginBottom){
            int maxoffset = mOriginBottom - mHead.getBottom();
            int offset = Math.min(maxoffset,dyUnconsumed*-1);
            mHead.offsetTopAndBottom(offset);
            mBody.layout(mBody.getLeft(), mBody.getTop()+offset, mBody.getRight(), mBody.getBottom());
        }
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
    }

    //惯性滑动事件
    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return super.onNestedPreFling(target, velocityX, velocityY);
    }

    //惯性滑动事件
    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }
}
