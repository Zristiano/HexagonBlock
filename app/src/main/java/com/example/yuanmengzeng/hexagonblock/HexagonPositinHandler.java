package com.example.yuanmengzeng.hexagonblock;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import com.example.yuanmengzeng.hexagonblock.CustomView.HexagonHeap;
import com.example.yuanmengzeng.hexagonblock.CustomView.HexagonView;
import com.example.yuanmengzeng.hexagonblock.CustomView.HorizontalLineBlock;
import com.example.yuanmengzeng.hexagonblock.CustomView.BaseBlock;







import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

/**
 *
 * Created by yuanmengzeng on 2016/5/25.
 */
public class HexagonPositinHandler {

    private Context mContext;

    private HexagonHeap hexagonHeap;

    private ScoreManager scoreManager;

    private int clearAlpha = 0;  //每次消除小六边形将会减少的透明度

    private SecureRandom secureRandom;  //产生随机数类


    /**
     * 底部三个消除模块（Block）的位置信息
     * 0--左边的block依次存储的是View 的底部坐标 和 MarginLeft
     * 1--中间的block依次存储的是View 的底部坐标 和 CenterLeft
     * 2--右边的block依次存储的是View 的底部坐标 和 MarginRight
     */
    private List<Integer[]> location = new LinkedList<>();

    /**
     * constructor
     * @param context context
     */
    public HexagonPositinHandler(Context context){

        mContext = context;
        try
        {
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
        }
        catch (Exception e)
        {
            secureRandom = new SecureRandom();
            ZYMLog.error("ZYM  no such SecureRandom ");
        }
    }

    public void setHexagonHeap(HexagonHeap hexagonHeap) {
        this.hexagonHeap = hexagonHeap;
    }

    public void setInitPositionInfo(View view, int order){
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)view.getLayoutParams();
        if (lp == null) return;
        Integer initLocation[] = new Integer[2];
        int viewLocation[] = new int[2];
        view.getLocationInWindow(viewLocation);
        initLocation[0] = viewLocation[1] + view.getMeasuredHeight() - CommonUtils.getStatusHeight(mContext); //View 的底部坐标(相对于父控件)
        if(order==0){
            initLocation[1]=viewLocation[0];  //view的起始x坐标
        }else if(order == 1){
            initLocation[1] = viewLocation[0]+view.getMeasuredWidth()/2; //view 的中心的x坐标
        }else if(order ==2){
            initLocation[1] = viewLocation[0]+view.getMeasuredWidth();  //view的终点x坐标
        }
        location.add(order,initLocation);

    }

    public void setScoreManager(ScoreManager manager){
        scoreManager = manager;
    }

    /**
     * 移动至底部block的初始位置
     * @param order 0-左block的位置   1-中间block的位置  2-右block的位置
     */
    public void moveToInitPosition(final View view , final int order){
        final FrameLayout.LayoutParams lp  =  (FrameLayout.LayoutParams)view.getLayoutParams();
        if(lp==null) return;
        if(view.getTag()!=null && view.getTag().equals(true)){  //tag为true说明block被匹配上了，因此要变形，使用动画从屏幕底部出现
            lp.topMargin = location.get(order)[0]- view.getMeasuredHeight();
            if(order == 0){
                lp.leftMargin = location.get(order)[1];
            }else if(order == 1){
                lp.leftMargin = location.get(order)[1]-view.getMeasuredWidth()/2;
            }else if(order == 2){
                lp.leftMargin = location.get(order)[1]-view.getMeasuredWidth();
            }
            view.setLayoutParams(lp);
            view.setVisibility(View.VISIBLE);
            ObjectAnimator transYAnim = ObjectAnimator.ofFloat(view,"translationY",view.getMeasuredHeight()+(mContext.getResources().getDisplayMetrics().heightPixels-location.get(order)[0]),-view.getMeasuredHeight()/2,0);
            transYAnim.setInterpolator(new AccelerateDecelerateInterpolator());
            transYAnim.setDuration(800);
            transYAnim.start();
        }else {
            int  endleft=0;
            if(order == 0){
                endleft = location.get(order)[1];
            }else if(order == 1){
                endleft = location.get(order)[1]-view.getMeasuredWidth()/2;
            }else if(order == 2){
                endleft = location.get(order)[1]-view.getMeasuredWidth();
            }

            ValueAnimator topAnim = ValueAnimator.ofInt(lp.topMargin,location.get(0)[0]-view.getMeasuredHeight());
            topAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    lp.topMargin = (int)animation.getAnimatedValue();
                    view.setLayoutParams(lp);
                }
            });

            ValueAnimator leftAnim = ValueAnimator.ofInt(lp.leftMargin,endleft);
            leftAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    lp.leftMargin = (int)animation.getAnimatedValue();
                    view.setLayoutParams(lp);
                }
            });

            AnimatorSet animSet = new AnimatorSet();
            animSet.setDuration(150);
            animSet.setInterpolator(new DecelerateInterpolator());
            animSet.playTogether(topAnim,leftAnim);
            animSet.start();

        }
        view.setTag(null);  //清除block的tag
    }


    /**
     * 选出底案大六边形中的与block相匹配的小六边形，并设置为block的tag
     * @param block  用于填充的移动模块里的单个六边形
     */
    public boolean matchBlock(View block){
        int hexagonHeapLocation[] = new int[2];
        hexagonHeap.getLocationInWindow(hexagonHeapLocation); //得到大六边形的起始坐标
        int hexBlockLocation[] = new int[2];
        block.getLocationInWindow(hexBlockLocation);     //得到可移动六边形的起始坐标
        int centerX = hexBlockLocation[0]-hexagonHeapLocation[0]+block.getMeasuredWidth()/2;
        int centerY = hexBlockLocation[1]-hexagonHeapLocation[1]+block.getMeasuredHeight()/2;
        HexagonView tempHex = (HexagonView)hexagonHeap.getHexagon(centerX, centerY);
        if(tempHex!=null){
            HexagonView tagView = null;
            if(block.getTag()!=null && block.getTag() instanceof  HexagonView){  //得到待匹配六边形里所存储的tag
                tagView = (HexagonView)(block.getTag());
            }
            if(!tempHex.equals(tagView)){ //底案大六边形对应的小六边形不是所存的tag时

                if(tagView!=null&&!HexagonView.STATE.matched.equals(tagView.getTag())){ //之前存的tag要变为默认的灰色
                    tagView.setHexContentColor(mContext.getResources().getColor(R.color.ver3_dark_gray));
                }

                if(HexagonView.STATE.matched.equals(tempHex.getTag())){ //之前已被匹配
                    return false;
                }else {
                    block.setTag(tempHex);
                    return true;
                }
            }else {
                return true;
            }

        }else{
            return false;
        }
    }


    private ArrayList<Object> lastMatchedView = new ArrayList<>();

    /**
     * 将底案大六边形中的与blockGroup相匹配的图形点亮
     * @param blockGroup b
     *
     * @return  底案大六边形中的与blockGroup相匹配的图形是否改变
     */
    public boolean matchBlock(HorizontalLineBlock blockGroup){
        boolean isMatchedAll = false;
        boolean ischange = false;   //底案大六边形中的与blockGroup相匹配的图形是否改变
        for(int i =0;i<blockGroup.getChildCount();i++){
            isMatchedAll = matchBlock(blockGroup.getChildAt(i));
            if(!isMatchedAll){
                break;   //blockGroup中有一个小六边形无匹配时 ，跳出
            }
        }
        if(isMatchedAll){  //若全匹配上了 ，将底案大六边形中的与blockGroup相匹配的图形点亮
            for (int i = 0; i<blockGroup.getChildCount();i++)
            {
                if(i>=lastMatchedView.size()){
                    lastMatchedView.add(i,new Object());
                }
                if(!blockGroup.getChildAt(i).getTag().equals(lastMatchedView.get(i))){  //底案大六边形中的与blockGroup相匹配的图形改变了
                    ischange = true;
                    lastMatchedView.remove(i);
                    lastMatchedView.add(i,blockGroup.getChildAt(i).getTag());
                }
                ((HexagonView)(blockGroup.getChildAt(i)).getTag()).setHexContentColor(((HexagonView)(blockGroup.getChildAt(i))).getHexContentColor());
            }
        }else {
            for (int i = 0; i<blockGroup.getChildCount();i++){
                if(blockGroup.getChildAt(i).getTag()!=null && blockGroup.getChildAt(i).getTag() instanceof HexagonView && !HexagonView.STATE.matched.equals(((HexagonView) blockGroup.getChildAt(i).getTag()).getTag())){
                    ((HexagonView)(blockGroup.getChildAt(i)).getTag()).setHexContentColor(mContext.getResources().getColor(R.color.ver3_dark_gray));                }
            }
            lastMatchedView.clear();
        }
        blockGroup.setTag(isMatchedAll);
        return ischange;
    }


    /**
     * 检测是否匹配
     * @param blockGroup 用以匹配的block
     * @return 是否匹配上
     */
    public boolean checkBlockGroupMatched(HorizontalLineBlock blockGroup){
        boolean tag;
        if(blockGroup.getTag()==null) return false;
        try {
            tag = (boolean) blockGroup.getTag();
        }catch (Exception e){
            tag = false;
        }
        return tag;
    }

    /**
     * 将大六边形中被block匹配上的小六边形标志为matched,
     * @param blockGroup  用以匹配的block
     */
    public void setHexagonViewMatched(HorizontalLineBlock blockGroup){
        for(int i = 0; i<blockGroup.getChildCount();i++){
            ((HexagonView)((blockGroup.getChildAt(i).getTag()))).setTag(HexagonView.STATE.matched);
            blockGroup.getChildAt(i).setTag(null);
        }
    }


    TreeMap<Integer,Integer> counter = new TreeMap<>();
    /**
     * 随机改变block的类型
     * @param blockGroup  用以匹配的block
     */
    public void changeBlockTypeRandomly(HorizontalLineBlock blockGroup){
        /*int random = (int)(Math.random()*95);
        Random random1 = new Random(random);
        random = random1.nextInt(96);*/

        int random = secureRandom.nextInt(95);
        ZYMLog.info("zym random is "+random);

        int type =0;

        switch (random)
        {
            case 0:
            case 5:
            case 10:
            case 15:
            case 20:
            case 28:
            case 42:
            case 35:
            case 40:
            case 45:
            case 68:
            case 79:
            case 60:
            case 84:
            case 70:
            case 93:
                type = 0; // 16
                break;
            case 1:
            case 29:
            case 41:
            case 80:
            case 66:
            case 94:
                type = 1; // 6
                break;
            case 2:
            case 30:
            case 27:
            case 61:
            case 85:
            case 71:
                type = 2; // 6
                break;
            case 3:
            case 31:
            case 62:
            case 72:
            case 65:
                type = 3; // 5
                break;
            case 4:
            case 32:
            case 63:
            case 73:
            case 75:
                type = 4; // 5
                break;
            case 6:
            case 33:
            case 64:
            case 74:
            case 43:
            case 86:
                type = 5; // 6
                break;
            case 7:
            case 34:
            case 67:
                type = 6; // 3
                break;
            case 8:
            case 36:
            case 69:
                type = 7; // 3
                break;
            case 9:
            case 37:
            case 76:
                type = 8; // 3
                break;
            case 11:
            case 38:
            case 77:
                type = 9; // 3
                break;
            case 12:
            case 39:
            case 78:
            case 50:
                type = 10; // 4
                break;
            case 13:
            case 44:
            case 81:
            case 51:
                type = 11; // 4
                break;
            case 14:
                type = 12; // 1
                break;
            case 16:
            case 46:
            case 82:
            case 52:
            case 90:
                type = 13; // 5
                break;
            case 17:
            case 47:
            case 83:
                type = 14; // 3
                break;
            case 18:
            case 48:
                type = 15; // 2
                break;
            case 19:
                type = 16; // 1
                break;
            case 25:
            case 49:
                type = 17; // 2
                break;
            case 21:
            case 87:
                type = 18; // 2
                break;
            case 22:
            case 53:
            case 54:
            case 88:
            case 23:
                type = 19; // 5
                break;
            case 55:
            case 89:
            case 24:
                type = 20; // 3
                break;
            case 56:
            case 91:
                type = 21; // 2
                break;
            case 57:
            case 92:
                type = 22; // 2
                break;
            case 58:
            case 26:
                type = 23; // 2
                break;
            case 59:
                type = 24; // 1
                break;
            default:
                type = 24;
                break;
        }

        /*if(random<=16){
            type = BaseBlock.SINGLE_BLOCK;
        }else if (random<=22){
            type = BaseBlock.HORIZONTAL_4_BLOCK;
        }else if (random<=28){
            type = BaseBlock.LEFT_TOP_4_BLOCK;
        }else if (random<=33){
            type = BaseBlock.LEFT_BOTTOM_4_BLOCK;
        }else if (random<=39){
            type = BaseBlock.CENTER_RHOMBUS_BLOCK;
        }else if (random<=44){
            type = BaseBlock.LEFT_RHOMBUS_BLOCK;
        }else if (random<=47){
            type = BaseBlock.RIGHT_RHOMBUS_BLOCK;
        }else if (random<=50){
            type = BaseBlock.ARC_ROTATE_0;
        }else if (random<=53){
            type = BaseBlock.ARC_ROTATE_60;
        }else if (random<=56){
            type = BaseBlock.ARC_ROTATE_120;
        }else if (random<=60){
            type = BaseBlock.ARC_ROTATE_180;
        }else if (random<=64){
            type = BaseBlock.ARC_ROTATE_240;
        }else if (random<=65){
            type = BaseBlock.ARC_ROTATE_300;
        }else if (random<=68){
            type = BaseBlock.GUN_180_RIGHT;
        }else if (random<=73){
            type = BaseBlock.GUN_180_LEFT;
        }else if (random<=78){
            type = BaseBlock.GUN_0_LEFT;
        }else if (random<=81){
            type = BaseBlock.GUN_0_RIGHT;
        }else if (random<=82){
            type = BaseBlock.GUN_60_RIGHT;
        }else if (random<=84){
            type = BaseBlock.GUN_60_LEFT;
        }else if (random<=86){
            type = BaseBlock.GUN_120_LEFT;
        }else if (random<=88){
            type = BaseBlock.GUN_120_RIGHT;
        }else if (random<=90){
            type = BaseBlock.GUN_240_LEFT;
        }else if (random<=92){
            type = BaseBlock.GUN_240_RIGHT;
        }else if (random<=94){
            type = BaseBlock.GUN_300_LEFT;
        }else if (random<=95){
            type = BaseBlock.GUN_300_RIGHT;
        }*/
        changeBlockType(blockGroup,type);  //改变block的样式
        if(counter.get(type)==null){
            counter.put(type,1);
        }else {
            int count = counter.get(type)+1;
            counter.put(type,count);
        }

        ZYMLog.info("counter type is "+counter);
    }

    /**
     * 将block的尺寸增大至与底案六边形里的小六边形相匹配的尺寸
     * @param block b
     */
    public void expandBlockSize(HorizontalLineBlock block){
        block.expandSizeWithAnim(hexagonHeap.getChildWidth(), hexagonHeap.getChildHeight());
        block.setTag(false);  //将block的匹配tag设置为false
    }

    /**
     * 将block的尺寸恢复成初始尺寸
     * @param block b
     */
    public void revertBlockSize(HorizontalLineBlock block){
        if(block.getAnimatorSet()!=null && block.getAnimatorSet().isRunning()){
            block.getAnimatorSet().cancel();
        }
        block.expandSize(0, 0);  //恢复默认尺寸
        lastMatchedView.clear();  //清楚保留的上次匹配
    }

    /**
     * 改变block的样式
     * @param block b
     * @param blockType 样式
     */
    public void changeBlockType(HorizontalLineBlock block,int blockType){
        block.removeAllViews();
        block.setBlockType(blockType);
        revertBlockSize(block);

    }


    /**
     * 播放将匹配上的block固定到大六边形的动画
     * @param block b
     */
    public void fixBlockWithAnim(final HorizontalLineBlock block,Animation.AnimationListener animListner){
        int count = block.getChildCount();
        HexagonView hexagon[] = new HexagonView[count];
        int refBlockLeft=10000,refBlockTop=10000;  //底案大六边形中映射block形状的模块的左坐标和高坐标
        for(int i =0;i<count;i++){
            hexagon[i] = (HexagonView) block.getChildAt(i).getTag();
            int location[] = new int[2];
            hexagon[i].getLocationInWindow(location);
            refBlockLeft = Math.min(refBlockLeft,location[0]);  // 找出各小六边形中左坐标最小的作为映射模块的左坐标
            refBlockTop = Math.min(refBlockTop,location[1]); // 找出各小六边形中高坐标最小的作为映射模块的高坐标
        }

        int blockLocation[] = new int[2];
        block.getLocationInWindow(blockLocation);

        int xDiff = refBlockLeft - blockLocation[0];  //block与映射模块的x坐标差值
        int yDiff = refBlockTop - blockLocation[1];   //block与映射模块的y坐标差值

        /**
         * 动画效果
         */
        TranslateAnimation translateAnim = new TranslateAnimation(0.0f,xDiff,0.0f,yDiff);
        translateAnim.setInterpolator(new LinearInterpolator());

        AlphaAnimation alphaAnim = new AlphaAnimation(1.0f,0.3f);
        alphaAnim.setInterpolator(new AccelerateInterpolator());

        AnimationSet animSet = new AnimationSet(false);
        animSet.setDuration(200);
        animSet.setAnimationListener(animListner);
        animSet.addAnimation(translateAnim);
        animSet.addAnimation(alphaAnim);

        block.startAnimation(animSet);
    }

    /**
     * 检测大六边形中是否有哪些行被全部点亮，并消除加分
     * @return true 有某行需要被消除，false 则无
     */
    public boolean checkClearLine(){
        int layerCount = hexagonHeap.getHEXAGON_LAYER();
        int hexagonCount = hexagonHeap.getChildCount();
        int sum = 0;
        List<List<HexagonView>> clearableLines = new LinkedList<>();

        /**0
         * （水平）“——”消除
         */
        for(int i =0;i<layerCount*2-1;i++)
        {
            int cur_layer_count = (2 * layerCount - 1) - Math.abs(i - (layerCount - 1));             //第i层有多少个小六边形
            List<HexagonView> checkedHexagons = new LinkedList<>();
            for(int j =0;j<cur_layer_count;j++)    //检查每一行的小六边形是否满足消除条件（处于匹配状态）
            {
                if(HexagonView.STATE.matched.equals(hexagonHeap.getChildAt(sum+j).getTag())){ //此小六边形处于匹配状态
                    checkedHexagons.add((HexagonView)(hexagonHeap.getChildAt(sum+j)));
                }else {   // 只要有一个小六边形不处于匹配状态，则跳出当前行
                    break;
                }
            }
            if(checkedHexagons.size()==cur_layer_count){  //当前行的小六边形全部处于匹配状态
                clearableLines.add(checkedHexagons);
            }
            sum += cur_layer_count;
        }

        /**
         *   "/" 消除
         */
        for(int i =0;i<layerCount*2-1;i++)
        {
            int cur_layer_count = (2*layerCount-1)-Math.abs(i-(layerCount-1));
            List<HexagonView> checkedHexagons = new LinkedList<>();
            int orderSum = 0;
            if(i<layerCount){
                for(int j =0;j<cur_layer_count;j++)
                {
                    int order;  //每一行开头计算的第一个小六边形距离改行起始小六边形的距离（个）
                    if(j<layerCount){
                        order = orderSum+i;
                    }else {
                        order = orderSum+i-(j-(layerCount-1));
                    }
                    if(HexagonView.STATE.matched.equals(hexagonHeap.getChildAt(order).getTag())){ //此小六边形处于匹配状态
                        checkedHexagons.add((HexagonView)(hexagonHeap.getChildAt(order)));
                    }else {   // 只要有一个小六边形不处于匹配状态，则跳出当前行
                        break;
                    }
                    orderSum += (2*layerCount-1)-Math.abs(j-(layerCount-1));
                }
            }else {
                int offsetCount = 0;
                for(int k=0;k<i-layerCount+1;k++){
                    offsetCount+=(2*layerCount-1)-Math.abs(k-(layerCount-1));
                }
                orderSum+=offsetCount;
                for(int j=0; j<cur_layer_count; j++)
                {
                    int order;  //每一行开头计算的第一个小六边形距离改行起始小六边形的距离（个）
                    if(j<2*layerCount-1-i){
                        order = orderSum+i;
                    }else {
                        order = orderSum+i-(j-(cur_layer_count-layerCount));
                    }
                    if(HexagonView.STATE.matched.equals(hexagonHeap.getChildAt(order).getTag())){ //此小六边形处于匹配状态
                        checkedHexagons.add((HexagonView)(hexagonHeap.getChildAt(order)));
                    }else {   // 只要有一个小六边形不处于匹配状态，则跳出当前行
                        break;
                    }
                    orderSum += (2*layerCount-1)-Math.abs(j+i-(layerCount-1)-(layerCount-1));
                }
            }
            if(checkedHexagons.size()==cur_layer_count){  //当前行的小六边形全部处于匹配状态
                clearableLines.add(checkedHexagons);
            }
        }

        /**
         *   "\"消除
         */
        int startOrder = hexagonCount - ((2*layerCount-1)-Math.abs(layerCount-1)); //大六边形最下面一行的起始小六边形的序数
        for(int i=0;i<2*layerCount-1;i++)
        {
            int cur_layer_count = (2*layerCount-1)-Math.abs(i-(layerCount-1));
            List<HexagonView> checkedHexagons = new LinkedList<>();
            if(i<layerCount){
                int order=startOrder;
                for(int j = 0; j<cur_layer_count; j++)
                {
                    if(HexagonView.STATE.matched.equals(hexagonHeap.getChildAt(order).getTag())){ //此小六边形处于匹配状态
                        checkedHexagons.add((HexagonView)(hexagonHeap.getChildAt(order)));
                    }else {   // 只要有一个小六边形不处于匹配状态，则跳出当前行
                        break;
                    }
                    if(j<layerCount-1){
                        order -= (2*layerCount-1)-Math.abs(j+1-(layerCount-1));
                    }else {
                        order -= (2*layerCount-1)-Math.abs(j+1-(layerCount-1))+1;  //多减一位
                    }
                }
                startOrder++;
            }else {
                int offsetCount = 0;
                for(int k=0;k<i-layerCount+1;k++){
                    offsetCount+=(2*layerCount-1)-Math.abs(k-(layerCount-1));
                }
                int order = hexagonCount - offsetCount -1;
                for(int j = 0; j<cur_layer_count; j++)
                {
                    if(HexagonView.STATE.matched.equals(hexagonHeap.getChildAt(order).getTag())){ //此小六边形处于匹配状态
                        checkedHexagons.add((HexagonView)(hexagonHeap.getChildAt(order)));
                    }else {   // 只要有一个小六边形不处于匹配状态，则跳出当前行
                        break;
                    }
                    if(j<2*layerCount-i-2){  // layerCount - (i-(layerCount-1)) -1
                        order -= (2*layerCount-1)-Math.abs((2*layerCount-1)-cur_layer_count+j+1-(layerCount-1));    //减去上一行的长度
                    }else {
                        order -= (2*layerCount-1)-Math.abs((2*layerCount-1)-cur_layer_count+j+1-(layerCount-1))+1;  //多减一位
                    }
                }

            }

            if(checkedHexagons.size() == cur_layer_count){
                clearableLines.add(checkedHexagons);
            }
        }

        remvClearableLines(clearableLines);
        return clearableLines.size()!=0;

    }

    /**
     *  消除底案大六边形中的亮行
     * @param clearableLines  可消除的亮行
     */
    private void remvClearableLines(List<List<HexagonView>> clearableLines){

        AnimatorSet clearBlockAnimSet = new AnimatorSet();
        ArrayList <Animator> clearBlockAnims = new ArrayList<>();
        for(int i = 0;i<clearableLines.size();i++){
            List<HexagonView> clearableLine = clearableLines.get(i);
            for(int j =0;j<clearableLine.size();j++){
//                (clearableLine.get(j)).setHexContentColor(mContext.getResources().getColor(R.color.ver3_dark_gray));
                clearBlockAnims.add(clearableLine.get(j).prepareColorAnim(clearAlpha));
                (clearableLine.get(j)).setTag(HexagonView.STATE.eliminated);
            }
        }
        clearBlockAnimSet.playTogether(clearBlockAnims);
        clearBlockAnimSet.start();
        scoreManager.addScore(clearableLines);
    }


    public void setClearAlpha(int clearAlpha) {
        this.clearAlpha = clearAlpha;
    }
}
