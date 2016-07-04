package com.example.yuanmengzeng.hexagonblock.CustomView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 *
 * Created by yuanmengzeng on 2016/6/9.
 */
public class HexagonImageView extends ImageView {

    private Bitmap mBitmap ;

    private Path linePath ;

    private  Paint bitmapPaint ;

    private float width,height; // 当前View的宽高

    private float sideLength; // 六边形的边长

    private BitmapShader bitmapShader;

    public HexagonImageView(Context context) {
        super(context);
        init();
    }

    public HexagonImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HexagonImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        super.setScaleType(ScaleType.CENTER_CROP);  //强制按比例缩放至对应尺寸
    }

    private void init(){
        Drawable drawable = getDrawable();
        if(drawable == null || !(drawable instanceof BitmapDrawable))
        {
            return;
        }
        mBitmap =((BitmapDrawable) drawable).getBitmap();
        linePath = new Path();
        bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = (float)(width*Math.sqrt(3)/2);
        sideLength = width/2;
        scaleBitmapMatrix();
        setMeasuredDimension((int) width, (int) height);
    }

    @Override
    public void setBackground(Drawable background) {
//        super.setBackground(background);
        if(background == null || !(background instanceof BitmapDrawable))
        {
            return;
        }
        mBitmap =((BitmapDrawable) background).getBitmap();
        linePath = new Path();
        bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
    }

    private void scaleBitmapMatrix(){
        if(mBitmap==null) return;
        float bitmapWidth = mBitmap.getWidth();
        float bitmapHeight = mBitmap.getHeight();
        float dx = (width-bitmapWidth )/2;
        float dy = (height-bitmapHeight)/2;
        Matrix shaderMatrix = new Matrix();
//        bitmapShader.getLocalMatrix(shaderMatrix);  //不能通过改变bitmapShader里原有的Matrix来设置矩阵，因为onMeasure会被多次调用，导致bitmapShader里的Matrix被多次修改
        shaderMatrix.set(null);
        float scale;
        if(width/bitmapWidth<height/bitmapHeight){
            scale = width/bitmapWidth;
        }else {
            scale = height/bitmapHeight;
        }
        shaderMatrix.postScale(scale, scale, bitmapWidth / 2, bitmapHeight / 2);
        shaderMatrix.postTranslate(dx, dy);
        bitmapShader.setLocalMatrix(shaderMatrix);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        linePath.reset();
        linePath.moveTo(sideLength / 2, 0);
        linePath.lineTo(sideLength * 3 / 2, 0);
        linePath.lineTo(width, height / 2);
        linePath.lineTo(sideLength * 3 / 2, height);
        linePath.lineTo(sideLength / 2, height);
        linePath.lineTo(0, height / 2);
        linePath.lineTo(sideLength / 2, 0);
        linePath.close();
        bitmapPaint.setShader(bitmapShader);
        canvas.drawPath(linePath,bitmapPaint);

    }
}
