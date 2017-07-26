package com.huihuicai.wynne.indicator.view;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huihuicai.wynne.indicator.R;

/**
 * Created by ybm on 2017/7/25.
 */

public class MultiTreeSelector extends LinearLayout implements View.OnClickListener {

    private final int DEFAULT_SELECT_COLOR = 0xff003c82;
    private final int DEFAULT_UNSELECT_COLOR = 0xff484848;
    private final int DEFAULT_TEXT_SIZE = 14;

    private Paint mPaint;
    private int mTabCount = 0;
    private int mCurrentTab = 0;
    private int mIndicatorLen = 0;
    private int mIndicatorStart = 0;
    private int mIndicatorHeight = 0;
    private int mTextSize = 0;
    private int mTextMargin = 0;
    private int mSelectedColor;
    private int mUnSelectedColor;
    private int mIndicatorColor;
    private String mDefaultText;
    private boolean mIsCreate;

    private SelectListener mListener;

    public MultiTreeSelector(Context context) {
        this(context, null);
    }

    public MultiTreeSelector(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiTreeSelector(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(HORIZONTAL);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.selector);
        mSelectedColor = ta.getColor(R.styleable.selector_selected_color, DEFAULT_SELECT_COLOR);
        mUnSelectedColor = ta.getColor(R.styleable.selector_default_color, DEFAULT_UNSELECT_COLOR);
        mIndicatorColor = ta.getColor(R.styleable.selector_indicator_color, DEFAULT_SELECT_COLOR);
        mIndicatorHeight = ta.getDimensionPixelSize(R.styleable.selector_indicator_height, 8);
        mTextSize = ta.getDimensionPixelSize(R.styleable.selector_text_size, DEFAULT_TEXT_SIZE);
        mTextMargin = ta.getDimensionPixelSize(R.styleable.selector_tab_margin, 20);
        mDefaultText = ta.getString(R.styleable.selector_default_text);
        if (TextUtils.isEmpty(mDefaultText)) {
            mDefaultText = "请选择";
        }
        ta.recycle();

        createTab(mDefaultText);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mIndicatorColor);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (r - l > getMeasuredWidth()) {
            Log.e("onLayout", "导航的标题个数太多了");
        }
        if (mIsCreate) {
            int start = mCurrentTab - 1;
            int end = mCurrentTab;
            moveIndicator(start, end);
        }
    }

    public void setSelectListener(SelectListener listener) {
        mListener = listener;
    }

    private void createTab(String tabContent) {
        mIsCreate = true;
        TextView tab = new TextView(getContext());
        tab.setGravity(Gravity.CENTER);
        tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        tab.setTextColor(mSelectedColor);
        tab.setText(tabContent);
        tab.setTag(mTabCount);
        tab.setOnClickListener(this);
        addView(tab, resetParam());
        mCurrentTab = mTabCount++;
        for (int i = 0; i < mCurrentTab; i++) {
            TextView tv = (TextView) getChildAt(i);
            if (tv == null) {
                continue;
            }
            tv.setTextColor(mUnSelectedColor);
        }
        if (mListener != null) {
            mListener.select(mCurrentTab);
        }
    }

    private LayoutParams resetParam() {
        LayoutParams param = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        param.setMargins(mTextMargin, 20, 0, 20);
        return param;
    }

    public void newTab(String parentStr, String newStr) {
        if (TextUtils.isEmpty(parentStr)) {
            return;
        }
        if (TextUtils.isEmpty(newStr)) {
            newStr = mDefaultText;
        }
        createTab(newStr);
        TextView tv = (TextView) getChildAt(mCurrentTab - 1);
        if (tv == null) {
            return;
        }
        tv.setText(parentStr);
        tv.setLayoutParams(resetParam());
    }

    private void selectTab(final int position) {
        if (mTabCount <= position) {
            return;
        }
        mIsCreate = false;
        mCurrentTab = position;
        TextView tv = (TextView) getChildAt(position);
        tv.setTextColor(mSelectedColor);
        tv.setText(mDefaultText);
        tv.setLayoutParams(resetParam());
        post(new Runnable() {
            @Override
            public void run() {
                moveIndicator(mTabCount - 1, position);
                for (int i = getChildCount() - 1; i > position; i--) {
                    removeView(getChildAt(i));
                }
                mTabCount = getChildCount();
            }
        });

    }

    private void moveIndicator(int from, int to) {
        if (from < -1 || to >= getChildCount() || from >= getChildCount()) {
            return;
        }
        View tvFrom;
        View tvTo;
        if (from < 0) {
            tvFrom = getChildAt(0);
            mIndicatorLen = tvFrom.getMeasuredWidth();
            mIndicatorStart = tvFrom.getLeft();
            invalidate();
            return;
        }

        tvFrom = getChildAt(from);
        tvTo = getChildAt(to);
        if (tvFrom == null || tvTo == null) {
            return;
        }

        ValueAnimator moveAnim = ValueAnimator.ofInt(tvFrom.getLeft(), tvTo.getLeft());
        moveAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mIndicatorStart = (int) animation.getAnimatedValue();
            }
        });

        ValueAnimator lenAnim = ValueAnimator.ofInt(tvFrom.getMeasuredWidth(), tvTo.getMeasuredWidth());
        lenAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator value) {
                mIndicatorLen = (int) value.getAnimatedValue();
                invalidate();
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.playTogether(moveAnim, lenAnim);
        set.start();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        Log.e("dispatchDraw", "left:" + getLeft());

        canvas.drawLine(0, getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight(), mPaint);
        int count = canvas.save();
        canvas.drawRect(mIndicatorStart, getMeasuredHeight() - mIndicatorHeight,
                mIndicatorStart + mIndicatorLen, getMeasuredHeight(), mPaint);
        canvas.restoreToCount(count);
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        if (position >= getChildCount()) {
            return;
        }
        selectTab(position);
        if (mListener != null) {
            mListener.select(mCurrentTab);
        }
    }

    public interface SelectListener {
        void select(int level);
    }
}
