package com.example.yuanmengzeng.hexagonblock;

import org.w3c.dom.Text;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.example.yuanmengzeng.hexagonblock.CustomView.HexagonView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * 得分管理
 *
 * Created by yuanmengzeng on 2016/6/12.
 */
public class ScoreManager {

    private Context mContext;

    private TextView sumScoreTx ;

    private TextView stepScoreTx ;

    private TextView topScoreTx;

    private int sumScore = 0;

    private int stepScore = 0;

    private int topScore = 0;

    public final static int SCORE_LEVEL[] = {11,17,25,33,43,54,66,79,93,108};

    public ScoreManager(Context context,TextView sumScoreTx, TextView stepScoreTx){
        mContext = context;
        this.sumScoreTx = sumScoreTx;
        this.stepScoreTx = stepScoreTx;
    }


    public void addScore(int score){
        stepScore = score;
        String text = score+"";
        stepScoreTx.setText(text);
        if(score<=40){
            startStepScoreAnim(false);
        }else{
            startStepScoreAnim(true);
        }
    }

    /**
     * 每一步显示步骤分的TextView的动画
     * @param isLineCleared  底案大六边形是否有某行被消除
     */
    private void  startStepScoreAnim(boolean isLineCleared){

        Animation AlphaAnim ;
        Animation translationAnim;
        final AnimationSet animSet = new AnimationSet(true);
        animSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                stepScoreTx.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                stepScoreTx.setVisibility(View.INVISIBLE);
                startSumScoreAnim();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        if(isLineCleared){
            Animation scaleInAnim = new ScaleAnimation(0.3f,1.5f,0.3f,1.5f,stepScoreTx.getPivotX(),stepScoreTx.getPivotY());
            scaleInAnim.setDuration(400);
            scaleInAnim.setInterpolator(new DecelerateInterpolator());
            scaleInAnim.setFillAfter(true);


            int sumTxLoc[] = new int[2];
            int stepTxLoc[] = new int[2];
            sumScoreTx.getLocationInWindow(sumTxLoc);
            stepScoreTx.getLocationInWindow(stepTxLoc);
            Animation scaleOutAnim = new ScaleAnimation(1.5f,0.5f,1.5f,0.5f,stepScoreTx.getPivotX(),stepScoreTx.getPivotY());
            Animation translateAnim = new TranslateAnimation(0,0,0,sumTxLoc[1]+sumScoreTx.getMeasuredHeight()/2-stepTxLoc[1]);
            AlphaAnim = new AlphaAnimation(1.0f,0.2f);

            animSet.setDuration(600);
            animSet.setInterpolator(new AccelerateInterpolator());
            animSet.addAnimation(scaleOutAnim);
            animSet.addAnimation(translateAnim);
            animSet.addAnimation(AlphaAnim);

            scaleInAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    stepScoreTx.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    stepScoreTx.startAnimation(animSet);
                    stepScoreTx.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            stepScoreTx.startAnimation(scaleInAnim);
        }else {

            AlphaAnim= new AlphaAnimation(1.0f,0.2f); // 显示步骤得分的view从显示到消失
            translationAnim = new TranslateAnimation(0,0,0,-stepScoreTx.getMeasuredHeight()*3);   // 向上滑动3倍高度的距离
            animSet.setInterpolator(new AccelerateInterpolator());
            animSet.setDuration(500);
            animSet.addAnimation(AlphaAnim);
            animSet.addAnimation(translationAnim);
            stepScoreTx.startAnimation(animSet);
        }
    }


    private void startSumScoreAnim(){
        ValueAnimator stepScoreAnimator = ValueAnimator.ofInt(0, stepScore);
        double duration = (Math.log1p(stepScore)-Math.log1p(40))*200+300;
        if(duration<0.0) duration = 200;
        stepScoreAnimator.setDuration((int)duration);
        stepScoreAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                String sumText = sumScore+ value + "";
                sumScoreTx.setText(sumText);
            }
        });
        stepScoreAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                sumScore+= stepScore;
                String text = sumScore + "";
                sumScoreTx.setText(text);
                if(topScoreTx!=null){
                    if(sumScore>topScore){
                        topScore = sumScore;
                        text = ""+topScore;
                        topScoreTx.setText(text);
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        stepScoreAnimator.start();
    }


    /**
     * 加步骤分
     * @param clearableLines 可消除的行
     */
    public void addScore(List<List<HexagonView>> clearableLines){
        int curStepScore = caculateScore(clearableLines);
        addScore(curStepScore);
    }


    /**
     * 计算步骤分
     * @param clearableLines 可消除的行
     */
    public int caculateScore(List<List<HexagonView>> clearableLines){
        int score = 0 ;
        if(clearableLines==null||clearableLines.size()==0){  //没有可消除的行
            score =  40;                                   //基本得分40分
        }else {
            sort(clearableLines);  //排序
            int lineCount = clearableLines.size();    //可消除行的数量
            int sumClearedHexCount = 0;
            for (int i = 0;i<lineCount ; i++){
                sumClearedHexCount += clearableLines.get(i).size();  //当前消除行所含小六边形数量
                float bonus = 0.0f;
                if(sumClearedHexCount>20){
                    bonus = ((float)sumClearedHexCount/20)*(sumClearedHexCount-20)*(sumClearedHexCount-20)*20;
                }
                score += 10*sumClearedHexCount*(lineCount+1)+40+bonus;
            }
        }
        return score;
    }


    /**
     * 将可消除的行按所包含的六边形个数从小到大排序
     * @param clearableLines  可消除的行
     */
    private void sort(List<List<HexagonView>> clearableLines){
        Collections.sort(clearableLines, new Comparator<List<HexagonView>>() {
            @Override
            public int compare(List<HexagonView> lhs, List<HexagonView> rhs) {
                if(lhs==null || rhs==null) return 0;
                if (lhs.size()>rhs.size()){
                    return 1;
                }else if (lhs.size()==rhs.size()){
                    return 0;
                }else {
                    return -1;
                }
            }
        });
    }

    public void setTopScoreInfo(TextView textView,int topScore){
        topScoreTx = textView;
        this.topScore = topScore;
    }

    public int getSumScore(){
        return sumScore;
    }

    public int getStepScore(){
        return stepScore;
    }

    public void reset(){
        stepScore = 0;
        sumScore = 0;
        String text = ""+0;
        sumScoreTx.setText(text);
        stepScoreTx.setText(text);
    }
}
