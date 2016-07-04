package com.example.yuanmengzeng.hexagonblock.CustomView;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.util.AttributeSet;
import android.view.View;

import com.example.yuanmengzeng.hexagonblock.R;
import com.example.yuanmengzeng.hexagonblock.ZYMLog;

import java.util.zip.InflaterInputStream;
/**
 *
 *              实际图形沿逆时针旋转90°
 *
 *
 *           |←←←←←  六边形画布的“高” hexHeight →→→→→→|
 *
 *           |-----|→→ offset  六边形左上顶点距画布起始图标的距离
 *           ______________________________    ___
 *           |     /                 \     |     ↑
 *           |    /                   \    |     ↑
 *           |   /                     \   |     ↑
 *           |  /                       \  |     ↑
 *           | /                         \ |     ↑
 *           |/                           \|    六边形画布的"宽"
 *           |\                           /|    hexWidth
 *           | \                         / |     ↓
 *           |  \                       /  |     ↓
 *           |   \                     /   |     ↓
 *           |    \                   /    |     ↓
 *           |_____\_________________/_____|    _↓_
 *
 *                 |←←← 六边形边长 →→→|
 *                    sidelength
 *
 *
 *
 *
 *
 */





/**
 *
 * Created by yuanmengzeng on 2016/5/17.
 */
public class HexagonView extends View {

    private Context context ;

    private float width;        //整体view的宽，包括padding
    private float height;        //整体view的高，包括padding
    private float hexWidth ;        // 六边形画布的宽
    private float hexHeight;        //  六边形画布的高
    private float offset;      // 六边形左上顶点距画布起始坐标的距离
    private float sideLength;  // 六边形边长

    private ShapeDrawable mShapeDrawable;

    private Path linePath;
    private Paint linePaint;

    private int hexagonAlpha = 0xff;

    private  float strokeWidth;  //画笔宽度
    private int hexContentColor;  //六边形填充颜色
    private int hexContentColorResId = R.color.ver3_dark_gray;

    private int hexBackGroundColor;   //六边形背景色
    private int hexBackGroundColorResId = R.color.black;


    public int CLEARED_ALPHA = 0xFF;

    public enum STATE{
        idle,normal,moving,matched,eliminated
    }


    public HexagonView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public HexagonView(Context context, int hexContentColorResId) {
        super(context);
        this.context = context;
        this.hexContentColorResId = hexContentColorResId;
        init();
    }

    public HexagonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public HexagonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init(){
        strokeWidth = 3*getResources().getDisplayMetrics().density;
        hexagonAlpha = 0xff;
        CLEARED_ALPHA = 0xff;
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStrokeWidth(strokeWidth);
        linePaint.setStyle(Paint.Style.STROKE);
        linePath = new Path();
        hexContentColor = getResources().getColor(hexContentColorResId);
        hexBackGroundColor = getResources().getColor(hexBackGroundColorResId);
        mShapeDrawable = new ShapeDrawable();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        hexWidth = MeasureSpec.getSize(widthMeasureSpec);
        hexHeight = MeasureSpec.getSize(heightMeasureSpec);
        hexHeight = hexWidth*2/(float)Math.sqrt(3);  //画布的宽度
        offset = hexWidth/(float)(2*Math.sqrt(3));   //六边形左上顶点距画布起始坐标的距离
        sideLength = hexHeight - offset*2;           //六边形边长

        width = hexWidth+2*getPaddingLeft();
        height = hexHeight+2*getPaddingTop();
        setMeasuredDimension((int)width, (int)height);
    }

    private void setBackGroundAlpha(int alpha){

        hexBackGroundColor = Color.argb(alpha,Color.red(hexBackGroundColor),Color.green(hexBackGroundColor),Color.blue(hexBackGroundColor));
    }



    @Override
    protected void onDraw(Canvas canvas) {
//        ZYMLog.info("hexagonView onDraw");
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        linePath.reset();
        linePath.moveTo(hexWidth / 2+paddingLeft, paddingRight);
        linePath.lineTo(hexWidth+paddingLeft, offset+paddingRight);
        linePath.lineTo(hexWidth+paddingLeft, offset + sideLength+paddingRight);
        linePath.lineTo(hexWidth / 2+paddingLeft, hexHeight+paddingRight);
        linePath.lineTo(paddingLeft, offset + sideLength+paddingRight);
        linePath.lineTo(paddingLeft, offset+paddingRight);
        linePath.lineTo(hexWidth / 2+paddingLeft, paddingRight);
        linePath.close();
        linePaint.setColor(hexBackGroundColor);
        canvas.drawPath(linePath, linePaint);

        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setColor(hexContentColor);
        float scale = 1-strokeWidth/hexWidth;
        canvas.scale(scale,scale,hexWidth/2,hexHeight/2);
        canvas.drawPath(linePath, linePaint);

        /*PathShape pathShape = new PathShape(linePath,hexWidth,hexHeight);
        mShapeDrawable.setShape(pathShape);
        mShapeDrawable.getPaint().setColor(Color.BLUE);
        mShapeDrawable.setBounds(0, 0, (int) hexWidth, (int) hexHeight);
        canvas.scale(0.9f,0.9f,hexWidth/2,hexHeight/2);
        mShapeDrawable.draw(canvas);*/
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        ZYMLog.info("ZYM left is "+left);
//        ZYMLog.info("ZYM top is "+top);
//        ZYMLog.info("ZYM right is "+right);
//        ZYMLog.info("ZYM bottom is "+bottom);
    }

    public void setHexContentColor(int color){
        hexagonAlpha = hexagonAlpha<0x85?0x85:hexagonAlpha;
//        hexagonAlpha = 0x85;
//        hexagonAlpha = 0x66;
        setBackGroundAlpha(hexagonAlpha);
        hexContentColor = Color.argb(hexagonAlpha,Color.red(color),Color.green(color),Color.blue(color));
        invalidate();
    }

    public int getHexContentColor(){
        return hexContentColor;
    }

    private int animStartColor;

    /**
     * 消除行时的动画
     * @param cleardAlpha  每次消除，小六边形减少的透明度
     * @return 返回动画对象
     */
    public ValueAnimator prepareColorAnim(int cleardAlpha){
        animStartColor = hexContentColor;
        CLEARED_ALPHA -= cleardAlpha;
        ValueAnimator colorValueAnimator = ValueAnimator.ofFloat(0, 2.0f);
        colorValueAnimator.setDuration(600);
        colorValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float ratio = (float)animation.getAnimatedValue();
                int originBlue = Color.blue(animStartColor);
                int originGreen = Color.green(animStartColor);
                int originRed = Color.red(animStartColor);
                int originAlpha = Color.alpha(animStartColor);
                float nowBlue, nowGreen, nowRed,nowAlpha=0;
                int color;
                if(ratio<=1.0f){
                    nowBlue = (0xff - originBlue)*ratio + originBlue;
                    nowGreen = (0xff - originGreen)*ratio + originGreen;
                    nowRed = (0xff - originRed)*ratio + originRed;
                    nowAlpha = (0xff-originAlpha)*ratio+originAlpha;
                    color  = Color.argb((int)nowAlpha, (int)nowRed, (int)nowGreen, (int)nowBlue);
                } else if (ratio<=1.5f){
                    color = hexContentColor;
                }else{
                    ratio = (2.0f-ratio)*2;
                    int baseColor = getResources().getColor(R.color.ver3_dark_gray);
                    nowRed = (0xff - Color.red(baseColor))*ratio + Color.red(baseColor);
                    nowBlue = (0xff - Color.blue(baseColor))*ratio + Color.blue(baseColor);
                    nowGreen = (0xff - Color.green(baseColor))*ratio + Color.green(baseColor);
                    nowAlpha = (0xff - CLEARED_ALPHA)*ratio+CLEARED_ALPHA;
                    hexagonAlpha = (int)nowAlpha;
                    color = Color.argb((int)nowAlpha,(int)nowRed,(int)nowBlue,(int)nowGreen);
                }
//                ZYMLog.info("nowAlpha is "+nowAlpha);
                setHexContentColor(color);
            }
        });
        return colorValueAnimator;
    }

    public int  getSideLength(){
        return (int)sideLength;
    }

    public void reset(){
        hexBackGroundColorResId = R.color.black;
        hexContentColorResId = R.color.ver3_dark_gray;
        setTag(null);
        init();
        invalidate();
    }
}
