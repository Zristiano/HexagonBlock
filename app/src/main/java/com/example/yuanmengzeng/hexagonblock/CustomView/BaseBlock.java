package com.example.yuanmengzeng.hexagonblock.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 *
 * Created by yuanmengzeng on 2016/5/25.
 */
public  abstract class BaseBlock extends ViewGroup{

    protected int childHeight, childWidth;

    public BaseBlock(Context context) {
        super(context);
    }

    public BaseBlock(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseBlock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public static final int SINGLE_BLOCK          = 0;
    public static final int HORIZONTAL_4_BLOCK    = 1;
    public static final int LEFT_TOP_4_BLOCK      = 2;
    public static final int LEFT_BOTTOM_4_BLOCK   = 3;
    public static final int LEFT_RHOMBUS_BLOCK    = 4;
    public static final int CENTER_RHOMBUS_BLOCK  = 5;
    public static final int RIGHT_RHOMBUS_BLOCK   = 6;
    public static final int ARC_ROTATE_0          = 7;
    public static final int ARC_ROTATE_60         = 8;
    public static final int ARC_ROTATE_120        = 9;
    public static final int ARC_ROTATE_180        = 10;
    public static final int ARC_ROTATE_240        = 11;
    public static final int ARC_ROTATE_300        = 12;
    public static final int GUN_0_LEFT            = 13;
    public static final int GUN_0_RIGHT           = 14;
    public static final int GUN_60_LEFT           = 15;
    public static final int GUN_60_RIGHT          = 16;
    public static final int GUN_120_LEFT          = 17;
    public static final int GUN_120_RIGHT         = 18;
    public static final int GUN_180_LEFT          = 19;
    public static final int GUN_180_RIGHT         = 20;
    public static final int GUN_240_LEFT          = 21;
    public static final int GUN_240_RIGHT         = 22;
    public static final int GUN_300_LEFT          = 23;
    public static final int GUN_300_RIGHT         = 24;





    public int getChildHeight() {
        return childHeight;
    }

    public int getChildWidth() {
        return childWidth;
    }

    public void setChildHeight(int childHeight) {
        this.childHeight = childHeight;
    }

    public void setChildWidth(int childWidth) {
        this.childWidth = childWidth;
    }
}
