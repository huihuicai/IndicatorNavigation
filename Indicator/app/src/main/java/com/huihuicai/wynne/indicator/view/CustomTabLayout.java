package com.huihuicai.wynne.indicator.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huihuicai.wynne.indicator.R;

/**
 * Created by ybm on 2017/7/26.
 */

public class CustomTabLayout extends LinearLayout {

    private int mMaxVisible = 3;
    private int mIndicatorColor;
    private int mSelectedColor;
    private int mUnselectColor;
    private int mIndicatorWidth;
    private int mIndicatorHeight;
    private int mTabPadding;
    private int mTextSize;
    //每个字View的宽度
    private float mTabWidth;
    //当前的position
    private int mCurrentPosition = 0;
    //使用viewpager滑动的偏移量
    private int mOffset = 0;
    //两个item的间隔
    private int mItemGap = 0;
    //子view的宽度
    private boolean mHasChild;
    private Paint mPaint;

    private ViewPager mViewPager;
    private OnPageChangeListener mPageChangeListener;
    private OnTabClickListener mTabClickListener;

    public interface OnPageChangeListener {

        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        void onPageSelected(int position);

        void onPageScrollStateChanged(int state);
    }

    public interface OnTabClickListener {
        void onTabClick(int position);
    }

    public CustomTabLayout(Context context) {
        this(context, null);
    }

    public CustomTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Indicator);
        mMaxVisible = ta.getInt(R.styleable.Indicator_max_visible, 3);
        mIndicatorColor = ta.getColor(R.styleable.Indicator_indicator_color, Color.BLACK);
        mIndicatorWidth = ta.getDimensionPixelSize(R.styleable.Indicator_indicator_width, 50);
        mIndicatorHeight = ta.getDimensionPixelSize(R.styleable.Indicator_indicator_height, 10);
        mTabPadding = ta.getDimensionPixelSize(R.styleable.Indicator_tab_padding, 20);
        mTextSize = ta.getDimensionPixelSize(R.styleable.Indicator_tab_text_size, 16);
        mSelectedColor = ta.getColor(R.styleable.Indicator_text_selected_color, Color.RED);
        mUnselectColor = ta.getColor(R.styleable.Indicator_text_unselect_color, Color.BLACK);
        ta.recycle();
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(mIndicatorColor);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(4);
        mPaint.setTextSize(mTextSize);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mHasChild = getChildCount() != 0;
        float maxTextWidth = 0;
        for (int i = 0; i < getChildCount(); i++) {
            TextView textView = initChildStyle(i);
            if (maxTextWidth < textView.getPaint().measureText(textView.getText().toString())) {
                maxTextWidth = textView.getPaint().measureText(textView.getText().toString());
            }
        }
        mIndicatorWidth = (int) maxTextWidth;
    }

    /**
     * viewpager的监听
     */
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mPageChangeListener = listener;
    }

    public void setOnTabClickListener(OnTabClickListener listener) {
        mTabClickListener = listener;
    }

    private TextView initChildStyle(final int position) {
        if (mHasChild && !(getChildAt(position) instanceof TextView)) {
            throw new IllegalArgumentException("This child is not textView");
        }
        TextView tab = mHasChild ? (TextView) getChildAt(position) : new TextView(getContext());
        tab.setGravity(Gravity.CENTER);
        tab.setTextColor(position == 0 ? mSelectedColor : mUnselectColor);
        tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        tab.setPadding(0, mTabPadding, 0, mTabPadding);
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewPager != null) {
                    mViewPager.setCurrentItem(position, true);
                } else {
                    if (mTabClickListener != null) {
                        setSelectedStatus(position);
                        mTabClickListener.onTabClick(position);
                    }
                }
            }
        });
        if (!mHasChild) {
            addView(tab);
        }
        return tab;
    }

    public void initIndicator(String[] titles) {
        if (titles == null || titles.length == 0) {
            throw new IllegalArgumentException("tab is null");
        }
        if (mHasChild) {
            mHasChild = false;
            removeAllViews();
        }
        float maxTextWidth = 0;
        for (int i = 0; i < titles.length; i++) {
            initChildStyle(i).setText(titles[i]);
            if (maxTextWidth < mPaint.measureText(titles[i])) {
                maxTextWidth = mPaint.measureText(titles[i]);
            }
        }
        mIndicatorWidth = (int) maxTextWidth + 20;
    }

    /**
     * 初始化tab
     */
    public void initIndicator(final ViewPager viewPager, String[] titles) {
        mViewPager = viewPager;
        initIndicator(titles);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mOffset = (int) (mTabWidth * positionOffset + position * mTabWidth);
                if (position >= mMaxVisible - 1 && positionOffset > 0 && getChildCount() > mMaxVisible) {
                    //指示器不动
                    int x = (int) ((position - (mMaxVisible - 1)) * mTabWidth + mTabWidth * positionOffset);
                    scrollTo(x, 0);
                }
                invalidate();
                if (mPageChangeListener != null) {
                    mPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                setSelectedStatus(position);
                mCurrentPosition = position;
                if (mPageChangeListener != null) {
                    mPageChangeListener.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (mPageChangeListener != null) {
                    mPageChangeListener.onPageScrollStateChanged(state);
                }
            }
        });
    }

    private void setSelectedStatus(int position) {
        if (getChildCount() > position && getChildAt(position) instanceof TextView) {
            TextView tv = (TextView) getChildAt(position);
            tv.setTextColor(mSelectedColor);
        }

        if (getChildCount() > mCurrentPosition && getChildAt(mCurrentPosition) instanceof TextView) {
            TextView tv = (TextView) getChildAt(mCurrentPosition);
            tv.setTextColor(mUnselectColor);
        }
        mOffset = (int) (position * mTabWidth);
        mCurrentPosition = position;
        invalidate();
    }

    private RectF mRect = new RectF();

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mTabWidth = mHasChild ? 1.0f * (r - l) / getChildCount() : 1.0f * (r - l) / mMaxVisible;
        if (mTabWidth <= 0) {
            mTabWidth = mHasChild ? 1.0f * getMeasuredWidth() / getChildCount() : 1.0f * getMeasuredWidth() / mMaxVisible;
        }
        for (int i = 0; i < getChildCount(); i++) {
            ViewGroup.LayoutParams params = getChildAt(i).getLayoutParams();
            params.width = (int) mTabWidth;
        }
        mItemGap = (int) ((mTabWidth - mIndicatorWidth) / 2);
        if (mItemGap < 0) mItemGap = 0;
        invalidate();
    }

    /**
     * 重绘indicator，也可用属性动画的方式
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.drawLine(0, getHeight(), getWidth() + mOffset, getHeight(), mPaint);
        int count = canvas.save();
        canvas.translate(mOffset + mItemGap, getHeight());
        mRect.top = -mIndicatorHeight;
        mRect.right = mIndicatorWidth;
        canvas.drawRect(mRect, mPaint);
        canvas.restoreToCount(count);
        super.dispatchDraw(canvas);
    }

}
