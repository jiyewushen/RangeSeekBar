package com.cx.librangeseekbar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Chen on 2017/12/31.
 */

public class RangeSeekBar extends ConstraintLayout {
    private View leftView;
    private View rightView;
    private View progressView;
    private ViewDragHelper mDragHelper;
    private int maxValue = 100;
    private int leftValue = 0;
    private int rightValue = maxValue;
    private float percent = 0.0f;
    private Rect mRect;
    private Bitmap mBitmap;
    private OnSeekBarChangeListener mChangeListener;
    private ConstraintSet mSet;

    public interface OnSeekBarChangeListener {
        void onChanged(int start, int end);

        void onStop();
    }

    public int getMaxValue() {
        return maxValue;
    }

    public int getLeftValue() {
        return leftValue;
    }

    public int getRightValue() {
        return rightValue;
    }


    public OnSeekBarChangeListener getChangeListener() {
        return mChangeListener;
    }

    public void setChangeListener(OnSeekBarChangeListener changeListener) {
        mChangeListener = changeListener;
    }

    public RangeSeekBar(Context context) {
        super(context);
        init(context);
    }

    public RangeSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RangeSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setLeftAndRight(int leftValue, int rightValue, int maxValue, Bitmap bitmap) {
        setMaxValue(maxValue);
        setLeftAndRight(leftValue, rightValue, bitmap);
    }

    public void setLeftAndRight(int leftValue, int rightValue) {
        setLeftAndRight(leftValue, rightValue, null);
    }

    public void setLeftAndRight(int leftValue, int rightValue, Bitmap bitmap) {
        if (leftValue < 0 || rightValue < 0 || rightValue > maxValue) return;
        if (leftValue != this.leftValue || rightValue != this.rightValue) {
            mSet.clone(this);
            mSet.connect(rightView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, (int) ((maxValue - rightValue) / percent));
            mSet.connect(leftView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, (int) (leftValue / percent));
            mSet.applyTo(this);
            if (bitmap != null) setBitmap(bitmap);
            else setProgressBitmap();
        }
    }

    public void setBitmap(Bitmap bitmap) {
        if (bitmap == null) return;
        if (mBitmap != null && !mBitmap.isRecycled()) mBitmap.recycle();
        if (mRect != null)
            mBitmap = Bitmap.createScaledBitmap(bitmap, mRect.width(), mRect.height(), false);
        setProgressBitmap();
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        rightValue = maxValue;
        percent = maxValue * 1.0f / (RangeSeekBar.this.getWidth() - rightView.getWidth() - RangeSeekBar.this.leftView.getWidth());
    }


    private void init(Context context) {
        this.post(new Runnable() {
            @Override
            public void run() {
                int width = getWidth() - rightView.getWidth() - leftView.getWidth();
                percent = maxValue * 1.0f / width;
                mRect = new Rect(0, 0, progressView.getWidth(), progressView.getHeight());
            }
        });
        mDragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {
            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                mSet.clone(RangeSeekBar.this);
                if (changedView == rightView) {
                    mSet.connect(changedView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, getWidth() - rightView.getWidth() - left);
                } else {
                    mSet.connect(changedView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, left);
                }
                mSet.applyTo(RangeSeekBar.this);
                setProgressBitmap();
                calculateValue();
                if (mChangeListener != null) {
                    mChangeListener.onChanged(leftValue, rightValue);
                }
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                if (mChangeListener != null) mChangeListener.onStop();
            }

            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return child == leftView || child == rightView;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                if (child == rightView) {
                    final int leftViewRight = RangeSeekBar.this.leftView.getRight();
                    final int rightBound = getWidth() - RangeSeekBar.this.rightView.getWidth();
                    return Math.min(Math.max(leftViewRight, left), rightBound);
                } else {
                    final int rightViewLeft = RangeSeekBar.this.rightView.getLeft() - RangeSeekBar.this.leftView.getWidth();
                    final int leftBound = 0;
                    return Math.min(Math.max(left, leftBound), rightViewLeft);
                }

            }
        });
//        int leftId = leftView.getId();
//        int rightId = rightView.getId();
//        int cardId = constraintLayout.findViewById(R.id.card_view).getId();
//        mSet = new ConstraintSet();
//        mSet.clone(constraintLayout);
//        mSet.connect(leftId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
//        mSet.connect(leftId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
//        mSet.connect(leftId, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
//
//        mSet.connect(cardId, ConstraintSet.START, leftId, ConstraintSet.END);
//        mSet.connect(cardId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
//        mSet.connect(cardId, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
//        mSet.connect(cardId, ConstraintSet.END, rightId, ConstraintSet.START);
//        mSet.constrainWidth(cardId, ConstraintSet.MATCH_CONSTRAINT);
//
//        mSet.connect(rightId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
//        mSet.connect(rightId, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
//        mSet.connect(rightId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
//        mSet.applyTo(constraintLayout);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        final int action = MotionEventCompat.getActionMasked(ev);
//        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
//            mDragHelper.cancel();
//            return false;
//        }
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }


    public void calculateValue() {
        leftValue = (int) (percent * (RangeSeekBar.this.leftView.getRight() - RangeSeekBar.this.leftView.getWidth()));
        rightValue = (int) (percent * (RangeSeekBar.this.rightView.getLeft() - RangeSeekBar.this.leftView.getWidth()));
        if (rightValue > maxValue) rightValue = maxValue;
        if (leftValue > maxValue) leftValue = maxValue;
    }

    private void setProgressBitmap() {
        if (mBitmap != null && !mBitmap.isRecycled()) {
            int x = (int) (leftValue / percent);
            int w = (int) ((rightValue - leftValue) / percent);
            if (w < 2) return;
//            progressView.setImageBitmap(Bitmap.createBitmap(mBitmap,x, 0,w, mRect.height()));
        }
    }

    public void addLeftView(View view,int widthType,int heightType) {
        leftView = view;
        view.setId(View.generateViewId());
        this.addView(view);
        ConstraintSet set = new ConstraintSet();
        set.clone(this);
        mSet.constrainWidth(view.getId(),widthType);
        mSet.constrainHeight(view.getId(),heightType);
        mSet.connect(view.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        mSet.connect(view.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        mSet.connect(view.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        set.applyTo(this);
    }
    public void addRightView(View view,int widthType,int heightType) {
        rightView = view;
        view.setId(View.generateViewId());
        this.addView(view);
        ConstraintSet set = new ConstraintSet();
        set.clone(this);
        mSet.constrainWidth(view.getId(),widthType);
        mSet.constrainHeight(view.getId(),heightType);
        mSet.connect(view.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        mSet.connect(view.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        mSet.connect(view.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        set.applyTo(this);
    }
    public void addProgressView(View view,int widthType) {
        addProgressView(view,widthType,ConstraintSet.MATCH_CONSTRAINT);
    }
    public void addProgressView(View view,int widthType,int heightType) {
        progressView = view;
        view.setId(View.generateViewId());
        this.addView(view);
        ConstraintSet set = new ConstraintSet();
        int id=view.getId();
        set.clone(this);
        mSet.constrainWidth(id,widthType);
        mSet.constrainHeight(id,heightType);
        mSet.connect(id, ConstraintSet.START, leftView.getId(), ConstraintSet.END);
        mSet.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        mSet.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        mSet.connect(id, ConstraintSet.END, rightView.getId(), ConstraintSet.START);
        set.applyTo(this);
    }
}
