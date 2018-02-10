package com.cx.librangeseekbar;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by cx on 2018/2/10.
 */

class BaseView extends ConstraintLayout {
    protected View leftView;
    protected View rightView;
    protected View progressView;
    protected ConstraintSet mSet;

    public BaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseView(Context context) {
        super(context);
    }

    public BaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void addLeftView(View view, int widthType){
        addLeftView(view,widthType, ConstraintSet.MATCH_CONSTRAINT);
    }

    public void addLeftView(View view, int widthType, int heightType) {
        leftView = view;
        view.setId(View.generateViewId());
        this.addView(view);
        mSet.clone(this);
        mSet.constrainWidth(view.getId(),widthType);
        mSet.constrainHeight(view.getId(),heightType);
        mSet.connect(view.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        mSet.connect(view.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        mSet.connect(view.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        mSet.applyTo(this);
    }

    public void addRightView(View view, int widthType){
        addRightView(view,widthType,ConstraintSet.MATCH_CONSTRAINT);
    }

    public void addRightView(View view, int widthType, int heightType) {
        rightView = view;
        view.setId(View.generateViewId());
        this.addView(view);
        mSet.clone(this);
        mSet.constrainWidth(view.getId(),widthType);
        mSet.constrainHeight(view.getId(),heightType);
        mSet.connect(view.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        mSet.connect(view.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        mSet.connect(view.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        mSet.applyTo(this);
    }

    public void addProgressView(View view, int widthType) {
        addProgressView(view,widthType,ConstraintSet.MATCH_CONSTRAINT);
    }

    public void addProgressView(View view, int widthType, int heightType) {
        progressView = view;
        view.setId(View.generateViewId());
        this.addView(view);
        int id=view.getId();
        mSet.clone(this);
        mSet.constrainWidth(id,widthType);
        mSet.constrainHeight(id,heightType);
        mSet.connect(id, ConstraintSet.START, leftView.getId(), ConstraintSet.END);
        mSet.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        mSet.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        mSet.connect(id, ConstraintSet.END, rightView.getId(), ConstraintSet.START);
        mSet.applyTo(this);
    }
}
