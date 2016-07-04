package com.example.yuanmengzeng.hexagonblock;

import java.lang.ref.WeakReference;
import java.sql.Date;
import java.util.Calendar;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yuanmengzeng.hexagonblock.CustomView.HexagonHeap;
import com.example.yuanmengzeng.hexagonblock.CustomView.HexagonView;
import com.example.yuanmengzeng.hexagonblock.CustomView.HorizontalLineBlock;


/**
 *
 * Created by yuanmengzeng on 2016/5/17.
 */
public class MainActivity extends Activity implements View.OnClickListener,View.OnTouchListener{

    private final static int SECOND_STAGE_SOCRE = 12000;
    private final static int THIRD_STAGE_SOCRE = 20000;
    private final static String HIGHTEST_SCORE = "hexagon_block_hightest_score";
    private HexagonHeap hexagonHeap;
    private HexagonView hexblock;
    private HorizontalLineBlock leftBlock,centerBlock,rightBlock;
    private EditText editText;
    private TextView topScoreTx;
    private HexagonPositinHandler positinHandler ;
    private ScoreManager scoreManager ;
    private SoundManager soundManager;
    private MediaPlayer mediaPlayer;
    private int topScore ;
    private ImageView coverImg;

    private View cover;

    private View loadingCircle;

    private AnimatorSet loadingCircleAnim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ZYMLog.info(" ----------------------------- app start");
        setContentView(R.layout.main_canvas_layout);
        initCoverView();
//        initGameView();
    }


    private void initGameView(){

        ZYMLog.info("ZYM width is "+getResources().getDisplayMetrics().widthPixels);
        ZYMLog.info("ZYM height is "+getResources().getDisplayMetrics().heightPixels);
        hexblock = (HexagonView) findViewById(R.id.hexBlock);
        editText = (EditText)findViewById(R.id.editScore);

        topScoreTx = (TextView)findViewById(R.id.hightest_socre);
        TextView sumScoreTx = (TextView)findViewById(R.id.sum_score_tx);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/f.ttf");   //设置显示总分的字体
        sumScoreTx.setTypeface(typeface);
        topScore = CommonUtils.readPrefsInt(this,HIGHTEST_SCORE);
        String text = ""+topScore;
        topScoreTx.setText(text);


        scoreManager = new ScoreManager(MainActivity.this,(TextView)findViewById(R.id.sum_score_tx),(TextView)findViewById(R.id.step_score_tx));
        scoreManager.setTopScoreInfo(topScoreTx,topScore);
        positinHandler.setScoreManager(scoreManager);



        leftBlock.setOnTouchListener(this);
        centerBlock.setOnTouchListener(this);
        rightBlock.setOnTouchListener(this);
        findViewById(R.id.buzzer).setOnClickListener(this);
        findViewById(R.id.buzzer).setTag(true);
        findViewById(R.id.animatorTest).setOnClickListener(this);
        findViewById(R.id.reboot_game).setOnClickListener(this);

        hexblock.setHexContentColor(getResources().getColor(R.color.yellow));

        /*for(int i =0;i<10000;i++){
            positinHandler.changeBlockTypeRandomly(leftBlock);

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
    }
    private void initCoverView(){

        coverImg = (ImageView) findViewById(R.id.cover_image);
        cover = findViewById(R.id.cover);
        cover.setVisibility(View.VISIBLE);
        loadingCircle = findViewById(R.id.loading_circle);
        loadingCircleAnim = new AnimatorSet();

        hexagonHeap = (HexagonHeap)findViewById(R.id.hexagonHeap);
        leftBlock = (HorizontalLineBlock)findViewById(R.id.left_bottom_block);
        centerBlock = (HorizontalLineBlock)findViewById(R.id.center_bottom_block);
        rightBlock = (HorizontalLineBlock)findViewById(R.id.right_bottom_block);

        positinHandler = new HexagonPositinHandler(this);
        positinHandler.setHexagonHeap(hexagonHeap);
        positinHandler.changeBlockTypeRandomly(leftBlock);
        positinHandler.changeBlockTypeRandomly(centerBlock);
        positinHandler.changeBlockTypeRandomly(rightBlock);

        /*topScoreTx = (TextView)findViewById(R.id.hightest_socre);
        final TextView sumScoreTx = (TextView)findViewById(R.id.sum_score_tx);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/30.ttf");   //设置显示总分的字体
        sumScoreTx.setTypeface(typeface);
        topScore = CommonUtils.readPrefsInt(this,HIGHTEST_SCORE);
        String text = ""+topScore;
        topScoreTx.setText(text);*/


        findViewById(R.id.skim_btn).setOnClickListener(this);


        final MyHandler myHandler = new MyHandler(new WeakReference<>(this));
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ZYMLog.info("postdelay^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
                scoreManager = new ScoreManager(MainActivity.this,(TextView)findViewById(R.id.sum_score_tx),(TextView)findViewById(R.id.step_score_tx));
                scoreManager.setTopScoreInfo(topScoreTx,topScore);
                if(soundManager == null){
                    soundManager = new SoundManager(MainActivity.this, new SoundManager.SoundCallBack() {
                        @Override
                        public void onBgSoundLoadSuc(MediaPlayer mp) {
                            ZYMLog.info("onBgSoundLoadSuc^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
                            mediaPlayer = mp;
                            if(isNormalDay){ //正常日子  非生日
                                skimCover();
                            }else {          //生日那天
                                mp.start();
                                if(cover.getVisibility()==View.VISIBLE){
                                    coverImg.setImageResource(R.drawable.cover2);
                                    findViewById(R.id.loading_hint).setVisibility(View.GONE);
                                    findViewById(R.id.skim_btn).setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });
                }
                ReqTimeThread reqTimeThread = new ReqTimeThread(myHandler);
                reqTimeThread.start();
            }
        },500);

        coverTimerTask();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.animatorTest:
                /*ObjectAnimator animator  = ObjectAnimator.ofFloat(helloTxt,"translationY",0,helloTxt.getMeasuredWidth(),helloTxt.getMeasuredHeight());
                animator.setDuration(1000).setInterpolator(new AccelerateDecelerateInterpolator());
                animator.start();*/
                String text = editText.getText().toString();
                int score = Integer.valueOf(text);
                scoreManager.addScore(score);
                playScoreSound(score);
                startScaleAnim(loadingCircle);
                soundManager.playBlockExpandSound();
                break;
            case R.id.buzzer:
                if(v.getTag()!=null&&v.getTag() instanceof Boolean){
                    boolean isEnable =(boolean)v.getTag();
                    if (isEnable){
                        ZYMLog.info("buzzer tag is boolean");
                        ((ImageView)v).setImageResource(R.drawable.sound_off_64);
                        v.setTag(false);
                        soundManager.setBgEnable(false);
                    }else {
                        ((ImageView)v).setImageResource(R.drawable.sound_on_64);
                        v.setTag(true);
                        soundManager.setBgEnable(true);
                    }
                }
                break;
            case R.id.reboot_game:
//                Intent intent = new Intent(MainActivity.this,FirstActivity.class);
//                finish();
//                startActivity(intent);
                reStartGame();
                break;
            case R.id.skim_btn:
                skimCover();
        }
    }



    @Override
    public boolean onTouch(final View v, MotionEvent event) {
        float rawX = event.getRawX();
        float rawY = event.getRawY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
//                ZYMLog.info("---------------action down");
                moveToPosition(v, (int) rawX, (int) rawY);
                positinHandler.expandBlockSize((HorizontalLineBlock) v);
                soundManager.playBlockExpandSound();
                break;
            case MotionEvent.ACTION_MOVE:
//                ZYMLog.info("---------------action move");
                moveToPosition(v, (int) rawX, (int) rawY);
                boolean isRefBlockChanged = positinHandler.matchBlock((HorizontalLineBlock) v);
                if(isRefBlockChanged){
                    soundManager.playBlockMatchSound();
                }
                break;
            case MotionEvent.ACTION_UP:
//                v.setVisibility(View.INVISIBLE);
                boolean isMatched = positinHandler.checkBlockGroupMatched((HorizontalLineBlock) v);   //检测当前block是否匹配上了
                if(isMatched){
                    positinHandler.fixBlockWithAnim((HorizontalLineBlock) v, new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            v.setVisibility(View.VISIBLE);
                            soundManager.playBlockPlaceSound();
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            positinHandler.revertBlockSize((HorizontalLineBlock) v);    //回复初始大小
                            positinHandler.setHexagonViewMatched((HorizontalLineBlock)v);
                            positinHandler.changeBlockTypeRandomly((HorizontalLineBlock)v);
                            boolean isClearable = positinHandler.checkClearLine();  //若匹配上了,则检测是否有整行需要消除，并加分
                            if(isClearable){
                                playScoreSound(scoreManager.getStepScore());
                            }
                            v.setVisibility(View.INVISIBLE);
                            moveToInitPosition(v);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });

                }else {
                    positinHandler.revertBlockSize((HorizontalLineBlock) v);    //回复初始大小
                    moveToInitPosition(v);
                    soundManager.playBlockRevertSound();
                }
                if(scoreManager.getSumScore()>THIRD_STAGE_SOCRE){  //超过20000分，屌，虐个狗（《不能说的秘密》四手联弹）
                    soundManager.playBgSound(2);
//                    positinHandler.setClearAlpha(0x10);
                    positinHandler.setClearAlpha(0x13);
                }else if(scoreManager.getSumScore()>SECOND_STAGE_SOCRE){//超过8000分，高手，换个有意思的音乐
                    soundManager.playBgSound(1);
//                    positinHandler.setClearAlpha(0x13);
                    positinHandler.setClearAlpha(0x18);
                }
//                ZYMLog.info("----------------action up");
                break;


        }
        return true;
    }


    private void startScaleAnim(View view){
        ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(view,"scaleX",1.0f,1.5f,1.0f,0.5f,1.0f);
        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(view,"scaleY",1.0f,1.5f,1.0f,0.5f,1.0f);
        scaleXAnim.setRepeatCount(ValueAnimator.INFINITE);
        scaleYAnim.setRepeatCount(ValueAnimator.INFINITE);

        if(loadingCircleAnim.isRunning()) return;
        loadingCircleAnim.setInterpolator(new DecelerateInterpolator());
        loadingCircleAnim.setDuration(1000);
        loadingCircleAnim.playTogether(scaleXAnim,scaleYAnim);
        loadingCircleAnim.start();
    }

    private boolean isNormalDay = true;
    private void coverTimerTask(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isNormalDay&&cover.getVisibility()!=View.GONE){
                    cover.setVisibility(View.GONE);
                    initGameView();
                    if(soundManager!=null){
                        soundManager.stopBgSound();
                    }
                    ZYMLog.info("ZYM coverTimerTask run");
                }
            }
        },5000);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        ZYMLog.info(" ----------------------------- app onWindowFocusChanged");
        if(cover.getVisibility()==View.VISIBLE){
            startScaleAnim(loadingCircle);
        }

        if(positinHandler!=null){
            positinHandler.setInitPositionInfo(leftBlock, 0);
            positinHandler.setInitPositionInfo(centerBlock, 1);
            positinHandler.setInitPositionInfo(rightBlock, 2);
        }
    }


    private void moveToPosition(View view,int rawX,int rawY){
        int left = rawX-view.getMeasuredWidth()/2;
        int bottom = rawY-CommonUtils.getStatusHeight(this)-((ViewGroup)view).getChildAt(0).getMeasuredHeight();
        int top = bottom-view.getMeasuredHeight();
        if(top<=0) top = 0;
        if(left<=0) left = 0;
        if(left+view.getMeasuredWidth()>getResources().getDisplayMetrics().widthPixels){
            left = getResources().getDisplayMetrics().widthPixels-view.getMeasuredWidth();
        }
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)view.getLayoutParams();
        if(lp!=null){
            lp.leftMargin = left;
            lp.topMargin = top;
            lp.gravity = Gravity.LEFT|Gravity.TOP;
            view.setLayoutParams(lp);
//            ZYMLog.info("leftMargin is "+left+"   topMargin is "+top);
        }else { //如果不能使用margin方式设置view的位置，则使用layout方式
            view.layout(left, top, left + view.getMeasuredWidth(), top + view.getMeasuredHeight());
        }
    }


    private void moveToInitPosition(View v){
        switch (v.getId()){
            case R.id.left_bottom_block:
                positinHandler.moveToInitPosition(v,0);
                break;
            case R.id.center_bottom_block:
                positinHandler.moveToInitPosition(v,1);
                break;
            case R.id.right_bottom_block:
                positinHandler.moveToInitPosition(v,2);
                break;
        }
    }


    /**
     * 根据得分使用不同的音效
     * @param score 得分
     */
    private void playScoreSound(int score){
        ZYMLog.info("step score is "+score);
        double rootScore = Math.sqrt(score);
        for(int i =9;i>=0;i--){
            if(rootScore>=ScoreManager.SCORE_LEVEL[i]){
                soundManager.playRowClearSound(i);
                break;
            }
        }
    }


    /**
     * 检测日子是否对
     * @param date date
     */
    private void calibreBirthDay(Date date){
        int month,day;
        if(date!=null){
            month = date.getMonth()+1;
            day = date.getDate();
        }else {
            Calendar calendar = Calendar.getInstance();
            month = calendar.get(Calendar.MONTH)+1;
            day = calendar.get(Calendar.DATE);
        }
        if(month==7 && day== 30){   //7.30
            isNormalDay = false;
            soundManager.playBgSound(3);
        }else {
            skimCover();
        }
        ZYMLog.info("ZYM month is "+month+"  day is "+day);
    }


    private int skimTimes = 0;
    private void skimCover(){
        skimTimes++;
        if(skimTimes>1){
            if(!mediaPlayer.isPlaying()){
                mediaPlayer.start();
            }
            if(loadingCircleAnim.isRunning()){
                loadingCircleAnim.cancel();
            }
            if(cover.getVisibility()!=View.GONE){
                cover.setVisibility(View.GONE);
                initGameView();
            }
            isNormalDay = true;
//            skimTimes = 0;
        }
    }

    private ExitDialog exitDialog;
    @Override
    public void onBackPressed() {
        if(exitDialog==null){
            exitDialog = new ExitDialog(this, new ExitDialog.DismissListner() {
                @Override
                public void onDismiss(boolean exit) {
                    if(!exit){
                        soundManager.resumeSound();
                    }else {
                        soundManager.stopBgSound();
                    }
                }
            });
        }
        exitDialog.show();
        if(soundManager!=null){
            soundManager.pauseSound();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(soundManager!=null){
            soundManager.pauseSound();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(soundManager!=null){
            soundManager.releaseAll();
        }
        if(topScoreTx==null) return;
        String text = topScoreTx.getText().toString();
        topScore = Integer.valueOf(text);
        CommonUtils.writePrefsInt(this,HIGHTEST_SCORE,topScore);
    }

    @Override
    protected void onResume() {
        ZYMLog.info(" ----------------------------- app display");
        super.onResume();
        if(soundManager!=null){
            soundManager.resumeSound();
        }
    }

    private static class MyHandler extends Handler{

        private WeakReference<MainActivity> mainActivityRefs;

        public MyHandler(WeakReference<MainActivity> mainActivityRefs){
            this.mainActivityRefs = mainActivityRefs;
        }


        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CommonData.LOAD_TIME_DATA_SUCS:     //加载网络时间成功
                    Date date = (Date) msg.obj;
                    if(date!=null){
                        mainActivityRefs.get().calibreBirthDay(date);
                    }else {
                        mainActivityRefs.get().calibreBirthDay(null);
                    }
                    break;
                case CommonData.LOAD_TIME_DATA_FAIL:
                    mainActivityRefs.get().calibreBirthDay(null);
                    break;
            }
        }
    }


    private void reStartGame(){
        soundManager.playBgSound(0);
        hexagonHeap.reset();
        scoreManager.reset();
    }

}
