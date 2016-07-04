package com.example.yuanmengzeng.hexagonblock.CustomView;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationSet;
import android.widget.FrameLayout;

import com.example.yuanmengzeng.hexagonblock.R;
import com.example.yuanmengzeng.hexagonblock.ZYMLog;

/**
 *
 * Created by yuanmengzeng on 2016/5/25.
 */
public class HorizontalLineBlock extends BaseBlock {

    private int hexCount ;
    private int blockType =SINGLE_BLOCK;
    private int hexagonColorId ;
    private int width,height;

    private int standardWidth, standardHeight;  //大六边形里的小六边形的标准宽高，用于填充的移动模块的小六边形的宽高应该略小于此宽高

    private int singleBlockWidth,singleBlockHeight;

    public HorizontalLineBlock(Context context,int type) {
        super(context);
        blockType = type;
        init();
    }

    public HorizontalLineBlock(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttrs(context,attrs);
        init();
    }

    private void getAttrs(Context context, AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BlockGroup);
        blockType = typedArray.getInt(R.styleable.BlockGroup_shape_type,SINGLE_BLOCK);
        hexagonColorId = typedArray.getResourceId(R.styleable.BlockGroup_hexagon_color,R.color.ver3_dark_gray);
        typedArray.recycle();
    }

    private void init(){
        ZYMLog.info("blockType is "+blockType);
        switch (blockType){
            case SINGLE_BLOCK:
                hexagonColorId = R.color.single_block;
                hexCount = 1;
                break;
            case ARC_ROTATE_0:
            case ARC_ROTATE_60:
            case ARC_ROTATE_120:
            case ARC_ROTATE_180:
            case ARC_ROTATE_240:
            case ARC_ROTATE_300:
                hexagonColorId = R.color.ARC_ROTATE_0;
                hexCount = 4;
                break;

            case HORIZONTAL_4_BLOCK:
            case LEFT_TOP_4_BLOCK:
            case LEFT_BOTTOM_4_BLOCK:
                hexagonColorId = R.color.HORIZONTAL_4_BLOCK;
                hexCount = 4;
                break;
            case LEFT_RHOMBUS_BLOCK:
            case CENTER_RHOMBUS_BLOCK:
            case RIGHT_RHOMBUS_BLOCK:
                hexagonColorId = R.color.RHOMBUS_BLOCK;
                hexCount = 4;
                break;
            case GUN_0_LEFT:
            case GUN_0_RIGHT:
            case GUN_60_LEFT:
            case GUN_60_RIGHT:
            case GUN_120_LEFT:
            case GUN_120_RIGHT:
                hexCount = 4;
                hexagonColorId = R.color.gun_120;
                break;
            case GUN_180_LEFT:
            case GUN_180_RIGHT:
            case GUN_240_LEFT:
            case GUN_240_RIGHT:
            case GUN_300_LEFT:
            case GUN_300_RIGHT:
                hexagonColorId = R.color.gun_300;
                hexCount = 4;
                break;
        }
        for (int i=0;i<hexCount;i++){
            addView(new HexagonView(getContext(),hexagonColorId));
        }
    }

    public void setBlockType(int blockType) {
        this.blockType = blockType;
        init();
    }

    int initWidthMeasureSpec = 0;
    int initHeightMeasureSpec = 0;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(initWidthMeasureSpec==0) initWidthMeasureSpec = widthMeasureSpec;
        int initMeasureHeight = (int)(MeasureSpec.getSize(initWidthMeasureSpec)*2/(float)Math.sqrt(3));
        initHeightMeasureSpec = MeasureSpec.makeMeasureSpec(initMeasureHeight,MeasureSpec.getMode(heightMeasureSpec));
        measureSize(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int startLeft=0,startTop=0;
        HexagonView childView;
        switch (blockType){
            case SINGLE_BLOCK:
                childView = (HexagonView)getChildAt(0);
                childView.layout(0,0,childView.getMeasuredWidth(),childView.getMeasuredHeight());
                break;
            case HORIZONTAL_4_BLOCK:
                for(int i=0; i<getChildCount(); i++){
                    childView=(HexagonView)getChildAt(i);
                    childView.layout(startLeft,startTop,startLeft+childView.getMeasuredWidth(),startTop+childView.getMeasuredHeight());
                    startLeft += childView.getMeasuredWidth();
                }
                break;
            case LEFT_BOTTOM_4_BLOCK:
                for(int i =0 ; i<getChildCount();i++){
                    childView =(HexagonView) getChildAt(i);
                    childView.layout(startLeft,startTop,startLeft+childView.getMeasuredWidth(),startTop+childView.getMeasuredHeight());
                    startLeft += childView.getMeasuredWidth()/2;
                    startTop += childView.getMeasuredHeight()*3/4;
                }
                break;
            case LEFT_TOP_4_BLOCK:
                startLeft = getChildAt(0).getMeasuredWidth()/2*3;
                for(int i =0; i<getChildCount(); i++){
                    childView = (HexagonView)getChildAt(i);
                    childView.layout(startLeft,startTop,startLeft+childView.getMeasuredWidth(),startTop+childView.getMeasuredHeight());
                    startLeft -= childView.getMeasuredWidth()/2;
                    startTop += childView.getMeasuredHeight()*3/4;
                }
                break;
            case LEFT_RHOMBUS_BLOCK:
                for (int i = 0; i<getChildCount(); i++){
                    childView = (HexagonView)getChildAt(i);
                    childView.layout(startLeft,startTop,startLeft+childView.getMeasuredWidth(),startTop+childView.getMeasuredHeight());
                    if(i==0){
                        startLeft+=singleBlockWidth;
                    }else if(i == 1) {
                        startLeft = (int)(0.5*singleBlockWidth);
                        startTop += (int)(0.75*singleBlockHeight);
                    }else if(i == 2){
                        startLeft += singleBlockWidth;
                    }
                }
                break;
            case CENTER_RHOMBUS_BLOCK:
                startLeft = getChildAt(0).getMeasuredWidth()/2;
                for(int i = 0; i<getChildCount(); i++){
                    childView = (HexagonView)getChildAt(i);
                    childView.layout(startLeft,startTop,startLeft+childView.getMeasuredWidth(),startTop+childView.getMeasuredHeight());
                    if(i==0){
                        startLeft = 0;
                        startTop += childView.getMeasuredHeight()*0.75;
                    }else if(i == 1){
                        startLeft += childView.getMeasuredWidth();
                    }else if(i == 2){
                        startLeft = childView.getMeasuredWidth()/2;
                        startTop += childView.getMeasuredHeight()*0.75;
                    }
                }
                break;
            case RIGHT_RHOMBUS_BLOCK:
                startLeft = getChildAt(0).getMeasuredWidth()/2;
                for(int i = 0; i<getChildCount(); i++){
                    childView = (HexagonView)getChildAt(i);
                    childView.layout(startLeft,startTop,startLeft+childView.getMeasuredWidth(),startTop+childView.getMeasuredHeight());
                    if(i == 0){
                        startLeft += childView.getMeasuredWidth();
                    }else if(i == 1){
                        startLeft = 0;
                        startTop += childView.getMeasuredHeight()*0.75;
                    }else if (i == 2){
                        startLeft += childView.getMeasuredWidth();
                    }
                }
                break;
            case ARC_ROTATE_0:
                startLeft = getChildAt(0).getMeasuredWidth()/2;
                for(int i = 0; i<getChildCount(); i++){
                    childView = (HexagonView)getChildAt(i);
                    childView.layout(startLeft,startTop,startLeft+childView.getMeasuredWidth(),startTop+childView.getMeasuredHeight());
                    if(i == 0){
                        startLeft +=childView.getMeasuredWidth();
                    }else if (i == 1){
                        startLeft = 0;
                        startTop += childView.getMeasuredHeight()*0.75;
                    }else if (i == 2){
                        startLeft += childView.getMeasuredWidth()*2;
                    }
                }
                break;
            case ARC_ROTATE_60:
                for (int i = 0; i<getChildCount();i++){
                    childView = (HexagonView)getChildAt(i);
                    childView.layout(startLeft,startTop,startLeft+childView.getMeasuredWidth(),startTop+childView.getMeasuredHeight());
                    if(i==0){
                        startLeft += childView.getMeasuredWidth();
                    }else if (i ==1){
                        startLeft += childView.getMeasuredWidth()/2;
                        startTop += childView.getMeasuredHeight()*0.75;
                    }else if (i == 2){
                        startLeft -= childView.getMeasuredWidth()/2;
                        startTop += childView.getMeasuredHeight()*0.75;
                    }
                }
                break;
            case ARC_ROTATE_120:
                startLeft += getChildAt(0).getMeasuredWidth();
                for (int i = 0;i<getChildCount();i++){
                    childView = (HexagonView)getChildAt(i);
                    childView.layout(startLeft,startTop,startLeft+childView.getMeasuredWidth(),startTop+childView.getMeasuredHeight());
                    if(i==0){
                        startLeft += childView.getMeasuredWidth()/2;
                        startTop += childView.getMeasuredHeight()*0.75;
                    }else if (i == 1){
                        startLeft -= childView.getMeasuredWidth()/2;
                        startTop += childView.getMeasuredHeight()*0.75;
                    }else if (i == 2){
                        startLeft -= childView.getMeasuredWidth();
                    }
                }
                break;
            case ARC_ROTATE_180:
                for (int i=0; i <getChildCount();i++){
                    childView = (HexagonView)getChildAt(i);
                    childView.layout(startLeft,startTop,startLeft+childView.getMeasuredWidth(),startTop+childView.getMeasuredHeight());
                    if (i==0){
                        startLeft += childView.getMeasuredWidth()/2;
                        startTop += childView.getMeasuredHeight()*0.75;
                    }else if(i==1){
                        startLeft += childView.getMeasuredWidth();
                    }else if (i==2){
                        startLeft += childView.getMeasuredWidth()/2;
                        startTop -= childView.getMeasuredHeight()*0.75;
                    }
                }
                break;
            case ARC_ROTATE_240:
                startLeft = getChildAt(0).getMeasuredWidth()/2;
                for (int i=0; i <getChildCount();i++){
                    childView = (HexagonView)getChildAt(i);
                    childView.layout(startLeft,startTop,startLeft+childView.getMeasuredWidth(),startTop+childView.getMeasuredHeight());
                    if (i==0){
                        startLeft -= childView.getMeasuredWidth()/2;
                        startTop += childView.getMeasuredHeight()*0.75;
                    }else if(i==1){
                        startLeft += childView.getMeasuredWidth()/2;
                        startTop += childView.getMeasuredHeight()*0.75;
                    }else if (i==2){
                        startLeft += childView.getMeasuredWidth();
                    }
                }
                break;
            case ARC_ROTATE_300:
                startLeft = getChildAt(0).getMeasuredWidth()/2;
                for (int i=0; i <getChildCount();i++){
                    childView = (HexagonView)getChildAt(i);
                    childView.layout(startLeft,startTop,startLeft+childView.getMeasuredWidth(),startTop+childView.getMeasuredHeight());
                    if (i==0){
                        startLeft += childView.getMeasuredWidth();
                    }else if(i==1){
                        startLeft -= childView.getMeasuredWidth()*1.5;
                        startTop += childView.getMeasuredHeight()*0.75;
                    }else if (i==2){
                        startLeft += childView.getMeasuredWidth()*0.5;
                        startTop += childView.getMeasuredHeight()*0.75;
                    }
                }
                break;
            case GUN_0_LEFT:
                for (int i=0; i <getChildCount();i++){
                    childView = (HexagonView)getChildAt(i);
                    childView.layout(startLeft,startTop,startLeft+childView.getMeasuredWidth(),startTop+childView.getMeasuredHeight());
                    if (i<2){
                        startLeft+=childView.getMeasuredWidth();
                    }else if (i==2){
                        startLeft-=childView.getMeasuredWidth()*1.5;
                        startTop += childView.getMeasuredHeight()*0.75;
                    }
                }
                break;
            case GUN_0_RIGHT:
                for (int i=0; i <getChildCount();i++){
                    childView = (HexagonView)getChildAt(i);
                    childView.layout(startLeft,startTop,startLeft+childView.getMeasuredWidth(),startTop+childView.getMeasuredHeight());
                    if (i<2){
                        startLeft+=childView.getMeasuredWidth();
                    }else if (i==2){
                        startLeft-=childView.getMeasuredWidth()*0.5;
                        startTop += childView.getMeasuredHeight()*0.75;
                    }
                }
                break;
            case GUN_60_LEFT:
                startLeft = getChildAt(0).getMeasuredWidth()/2;
                for (int i=0; i <getChildCount();i++){
                    childView = (HexagonView)getChildAt(i);
                    childView.layout(startLeft,startTop,startLeft+childView.getMeasuredWidth(),startTop+childView.getMeasuredHeight());
                    if (i<2){
                        startLeft += childView.getMeasuredWidth()*0.5;
                        startTop += childView.getMeasuredHeight()*0.75;
                    }else if (i==2){
                        startLeft-=childView.getMeasuredWidth()*1.5;
                        startTop -= childView.getMeasuredHeight()*0.75;
                    }
                }
                break;
            case GUN_60_RIGHT:
                for (int i=0; i <getChildCount();i++){
                    childView = (HexagonView)getChildAt(i);
                    childView.layout(startLeft,startTop,startLeft+childView.getMeasuredWidth(),startTop+childView.getMeasuredHeight());
                    if (i<2){
                        startLeft += childView.getMeasuredWidth()*0.5;
                        startTop += childView.getMeasuredHeight()*0.75;
                    }else if (i==2){
                        startLeft-=childView.getMeasuredWidth();
                    }
                }
                break;
            case GUN_120_LEFT:
                for (int i=0; i <getChildCount();i++){
                    childView = (HexagonView)getChildAt(i);
                    childView.layout(startLeft,startTop,startLeft+childView.getMeasuredWidth(),startTop+childView.getMeasuredHeight());
                    if (i==0){
                        startLeft += childView.getMeasuredWidth();
                    }else if (i>0){
                        startLeft-=childView.getMeasuredWidth()*0.5;
                        startTop += childView.getMeasuredHeight()*0.75;
                    }
                }
                break;
            case GUN_120_RIGHT:
                startTop += getChildAt(0).getMeasuredHeight()*0.75;
                for (int i=0; i <getChildCount();i++){
                    childView = (HexagonView)getChildAt(i);
                    childView.layout(startLeft,startTop,startLeft+childView.getMeasuredWidth(),startTop+childView.getMeasuredHeight());
                    if (i==0){
                        startLeft += childView.getMeasuredWidth()*0.5;
                        startTop += childView.getMeasuredHeight()*0.75;
                    }else if (i>0){
                        startLeft+=childView.getMeasuredWidth()*0.5;
                        startTop -= childView.getMeasuredHeight()*0.75;
                    }
                }
                break;
            case GUN_180_LEFT:
                startLeft += getChildAt(0).getMeasuredWidth()*0.5;
                for (int i=0; i <getChildCount();i++){
                    childView = (HexagonView)getChildAt(i);
                    childView.layout(startLeft,startTop,startLeft+childView.getMeasuredWidth(),startTop+childView.getMeasuredHeight());
                    if (i==0){
                        startLeft -= childView.getMeasuredWidth()*0.5;
                        startTop += childView.getMeasuredHeight()*0.75;
                    }else if (i>0){
                        startLeft+=childView.getMeasuredWidth();
                    }
                }
                break;
            case GUN_180_RIGHT:
                startLeft += getChildAt(0).getMeasuredWidth()*1.5;
                for (int i=0; i <getChildCount();i++){
                    childView = (HexagonView)getChildAt(i);
                    childView.layout(startLeft,startTop,startLeft+childView.getMeasuredWidth(),startTop+childView.getMeasuredHeight());
                    if (i==0){
                        startLeft -= childView.getMeasuredWidth()*1.5;
                        startTop += childView.getMeasuredHeight()*0.75;
                    }else if (i>0){
                        startLeft+=childView.getMeasuredWidth();
                    }
                }
                break;
            case GUN_240_LEFT:
                startLeft += getChildAt(0).getMeasuredWidth();
                for (int i=0; i <getChildCount();i++){
                    childView = (HexagonView)getChildAt(i);
                    childView.layout(startLeft,startTop,startLeft+childView.getMeasuredWidth(),startTop+childView.getMeasuredHeight());
                    if (i==0){
                        startLeft -= childView.getMeasuredWidth();
                    }else if (i>0){
                        startLeft += childView.getMeasuredWidth()*0.5;
                        startTop += childView.getMeasuredHeight()*0.75;
                    }
                }
                break;
            case GUN_240_RIGHT:
                for (int i=0; i <getChildCount();i++){
                    childView = (HexagonView)getChildAt(i);
                    childView.layout(startLeft,startTop,startLeft+childView.getMeasuredWidth(),startTop+childView.getMeasuredHeight());
                    if (i<2){
                        startLeft += childView.getMeasuredWidth()*0.5;
                        startTop += childView.getMeasuredHeight()*0.75;
                    }else if (i==2){
                        startLeft += childView.getMeasuredWidth()*0.5;
                        startTop -= childView.getMeasuredHeight()*0.75;
                    }
                }
                break;
            case GUN_300_LEFT:
                startLeft += getChildAt(0).getMeasuredWidth();
                for (int i=0; i <getChildCount();i++){
                    childView = (HexagonView)getChildAt(i);
                    childView.layout(startLeft,startTop,startLeft+childView.getMeasuredWidth(),startTop+childView.getMeasuredHeight());
                    if (i<2){
                        startLeft -= childView.getMeasuredWidth()*0.5;
                        startTop += childView.getMeasuredHeight()*0.75;
                    }else if (i==2){
                        startLeft += childView.getMeasuredWidth()*1.5;
                        startTop -= childView.getMeasuredHeight()*0.75;
                    }
                }
                break;
            case GUN_300_RIGHT:
                startLeft += getChildAt(0).getMeasuredWidth();
                for (int i=0; i <getChildCount();i++){
                    childView = (HexagonView)getChildAt(i);
                    childView.layout(startLeft,startTop,startLeft+childView.getMeasuredWidth(),startTop+childView.getMeasuredHeight());
                    if (i<2){
                        startLeft -= childView.getMeasuredWidth()*0.5;
                        startTop += childView.getMeasuredHeight()*0.75;
                    }else if (i==2){
                        startLeft += childView.getMeasuredWidth();
                    }
                }
                break;
        }

    }


    private AnimatorSet animatorSet;

    public void expandSizeWithAnim(int width,int height){
        ObjectAnimator widthAnimator = ObjectAnimator.ofInt(this,"standardWidth",singleBlockWidth,width);
        ObjectAnimator heightAnimator = ObjectAnimator.ofInt(this,"standardHeight",singleBlockHeight,height);
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(widthAnimator,heightAnimator);
        animatorSet.setDuration(150);
        animatorSet.start();
    }
    public void expandSize(int width, int height){   //若width和height同时为零，则为当前block的大小为xml中定义的大小,此处设置的宽高是小六边形的宽高

//        if(width<=MeasureSpec.getSize(initWidthMeasureSpec))  width = 0;  //当block的小六边形的宽高小于初始宽高时，直接置0，这样measureSize()时，会自动回复到默认的初始宽高
//        if(height<=MeasureSpec.getSize(initHeightMeasureSpec)) height = 0;
        standardWidth = width;
        standardHeight = height;
        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth,MeasureSpec.UNSPECIFIED);
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.UNSPECIFIED);
        measure(childWidthMeasureSpec, childHeightMeasureSpec);
        requestLayout();
    }

    int widthMeasureSpec,heightMeasureSpec;
    public void measureSize(int widthMeasureSpec, int heightMeasureSpec){
        int childWidthPadding = 0  ,  childHeightPadding = 0;
        int lastChildHeight = childHeight;
        if(standardWidth==0 && standardHeight==0){
            childWidth = MeasureSpec.getSize(initWidthMeasureSpec);   //此时用于填充的移动模块的各个小六边形的宽高为默认宽高，单个模块宽高与小六边形宽高一致
            singleBlockWidth = childWidth;
            childHeight = (int)(childWidth*2/(float)Math.sqrt(3));  //画布的高度
            singleBlockHeight = childHeight;

        }else {                                                  //此时用于填充的移动模块的各个小六边形的宽高为标准宽高*0.8，单个模块宽高为标准宽高
            childWidth = (int)(standardWidth*0.8);
            singleBlockWidth = standardWidth;
            childHeight = (int)(childWidth*2/(float)Math.sqrt(3));
            singleBlockHeight = standardHeight;
            childWidthPadding = (standardWidth-childWidth)/2;
            childHeightPadding =(standardHeight-childHeight)/2;
        }
        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth,MeasureSpec.getMode(widthMeasureSpec));
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight,MeasureSpec.getMode(heightMeasureSpec));
//        ZYMLog.info("childWidth is "+childWidth+"  childHeight is "+childHeight);
        for(int i =0;i<getChildCount();i++){
            View childView = getChildAt(i);
            childView.setPadding(childWidthPadding,childHeightPadding,childWidthPadding,childHeightPadding);
            childView.measure(childWidthMeasureSpec,childHeightMeasureSpec);
        }
        switch (blockType){
            case SINGLE_BLOCK:
                width = singleBlockWidth;
                height = singleBlockHeight;
                break;
            case HORIZONTAL_4_BLOCK:
                width = singleBlockWidth*4;
                height = singleBlockHeight;
                break;
            case LEFT_TOP_4_BLOCK:
            case LEFT_BOTTOM_4_BLOCK:
                width = (int)(singleBlockWidth*2.5);
                height = (int)(singleBlockHeight*3.25);
                break;
            case CENTER_RHOMBUS_BLOCK:
                width = (singleBlockWidth*2);
                height = (int)(singleBlockHeight*2.5);
                break;
            case LEFT_RHOMBUS_BLOCK:
            case RIGHT_RHOMBUS_BLOCK:
                width = (int)(singleBlockWidth*2.5);
                height = (int)(singleBlockHeight*1.75);
                break;
            case ARC_ROTATE_0:
            case ARC_ROTATE_180:
            case GUN_0_LEFT:
            case GUN_0_RIGHT:
            case GUN_180_LEFT:
            case GUN_180_RIGHT:
                width = singleBlockWidth*3;
                height = (int)(singleBlockHeight*1.75);
                break;
            case ARC_ROTATE_60:
            case ARC_ROTATE_120:
            case ARC_ROTATE_240:
            case ARC_ROTATE_300:
            case GUN_60_LEFT:
            case GUN_120_RIGHT:
            case GUN_240_RIGHT:
            case GUN_300_LEFT:
                width = (int)(singleBlockWidth*2.5);
                height = (int)(singleBlockHeight*2.5);
                break;
            case GUN_60_RIGHT:
            case GUN_120_LEFT:
            case GUN_240_LEFT:
            case GUN_300_RIGHT:
                width = singleBlockWidth*2;
                height = (int)(singleBlockHeight*2.5);

        }

        /**
         *  调整当前block的位置
         */
        int lastWidth = MeasureSpec.getSize(this.widthMeasureSpec);
        int lastHeight = MeasureSpec.getSize(this.heightMeasureSpec);
        MarginLayoutParams lp = (MarginLayoutParams)getLayoutParams();
        if(lastWidth!=0 && lastHeight!=0){
            int widthDiff = width - lastWidth;
            int heightDiff = height - lastHeight + (childHeight-lastChildHeight);
            lp.leftMargin -= widthDiff/2;
            lp.topMargin  -= heightDiff;
            setLayoutParams(lp);
        }
//        ZYMLog.info("leftMargin is "+lp.leftMargin+"   topMargin is "+lp.topMargin);

        this.widthMeasureSpec = MeasureSpec.makeMeasureSpec(width,MeasureSpec.getMode(widthMeasureSpec));   //重绘制当前模块的宽高
        this.heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,MeasureSpec.getMode(heightMeasureSpec));
    }


    public int getStandardWidth() {
        return standardWidth;
    }

    public int getStandardHeight() {
        return standardHeight;
    }

    public void setStandardWidth(int standardWidth) {

        this.standardWidth = standardWidth;
//        ZYMLog.info("   W---- standardWidth is "+standardWidth+"   singleBlockHeight is "+singleBlockHeight );
        expandSize(standardWidth, singleBlockHeight);
    }

    public void setStandardHeight(int standardHeight) {
        this.standardHeight = standardHeight;
//        ZYMLog.info("   H---- singleBlockWidth is "+singleBlockWidth+"   standardHeight is "+standardHeight );
        expandSize(singleBlockWidth,standardHeight);
    }

    public AnimatorSet getAnimatorSet() {
        return animatorSet;
    }
}
