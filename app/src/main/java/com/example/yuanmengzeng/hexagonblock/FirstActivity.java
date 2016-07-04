package com.example.yuanmengzeng.hexagonblock;

import java.lang.ref.WeakReference;
import java.sql.Date;
import java.util.Calendar;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

/**
 * 启动activity，用于显示封面图 Created by yuanmengzeng on 2016/6/28.
 */
public class FirstActivity extends Activity implements View.OnClickListener,SoundManager.SoundCallBack
{


    private ImageView coverImg;

    private View cover;

    private View loadingCircle;

    private AnimatorSet loadingCircleAnim;

    private boolean isNormalDay = true;

    private SoundManager soundManager;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cover);
        initView();
    }

    private void initView()
    {
        coverImg = (ImageView) findViewById(R.id.cover_image);
        cover = findViewById(R.id.cover);
        cover.setVisibility(View.VISIBLE);
        loadingCircle = findViewById(R.id.loading_circle);
        loadingCircleAnim = new AnimatorSet();

        findViewById(R.id.skim_btn).setOnClickListener(this);

        coverTimerTask();

        final MyHandler myHandler = new MyHandler(new WeakReference<>(this));
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isFinishing()) return;
                ReqTimeThread reqTimeThread = new ReqTimeThread(myHandler);
                reqTimeThread.start();
//                soundManager = SoundManager.getInstance();
//                soundManager.setSoundCallBack(FirstActivity.this);
//                soundManager.playBgSound(FirstActivity.this,0);
//                soundManager.initSound(FirstActivity.this);
            }
        },500);


    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.skim_btn:
                skimCover();
                break;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if (cover.getVisibility() == View.VISIBLE)
        {
            startScaleAnim(loadingCircle);
        }
    }

    /**
     * 开始制定view的比例动画
     * 
     * @param view v
     */
    private void startScaleAnim(View view)
    {
        ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 1.5f, 1.0f, 0.5f, 1.0f);
        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 1.5f, 1.0f, 0.5f, 1.0f);
        scaleXAnim.setRepeatCount(ValueAnimator.INFINITE);
        scaleYAnim.setRepeatCount(ValueAnimator.INFINITE);

        if (loadingCircleAnim.isRunning())
            return;
        loadingCircleAnim.setInterpolator(new DecelerateInterpolator());
        loadingCircleAnim.setDuration(1000);
        loadingCircleAnim.playTogether(scaleXAnim, scaleYAnim);
        loadingCircleAnim.start();
    }

    /**
     * 封面计时器，封面显示5秒后自动结束
     */
    private void coverTimerTask()
    {
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if(isFinishing())return;
                if (isNormalDay && cover.getVisibility() != View.GONE)
                {
                    skimCover();
                    ZYMLog.info("ZYM coverTimerTask run");
                }
            }
        }, 5000);
    }

    @Override
    public void onBgSoundLoadSuc(MediaPlayer mp)
    {
        ZYMLog.info("onBgSoundLoadSuc^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        mediaPlayer = mp;
        if (isNormalDay)
        { // 正常日子 非生日
            skimCover();
        }
        else
        { // 生日那天
            mp.start();
            if (cover.getVisibility() == View.VISIBLE)
            {
                coverImg.setImageResource(R.drawable.cover2);
                findViewById(R.id.loading_hint).setVisibility(View.GONE);
                findViewById(R.id.skim_btn).setVisibility(View.VISIBLE);
            }
        }
    }

    private int skimTimes = 0;

    /**
     * 累计调用此函数至少两次才能跳过cover
     */
    private void skimCover()
    {
        skimTimes++;
        if (skimTimes > 1)
        {
            if (!mediaPlayer.isPlaying())
            {
                mediaPlayer.start();
            }
            if (loadingCircleAnim.isRunning())
            {
                loadingCircleAnim.cancel();
            }
            if (cover.getVisibility() != View.GONE)
            {
                cover.setVisibility(View.GONE);
            }
            isNormalDay = true;
            // skimTimes = 0;
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private static class MyHandler extends Handler
    {

        private WeakReference<FirstActivity> firstActivityRefs;

        public MyHandler(WeakReference<FirstActivity> mainActivityRefs)
        {
            this.firstActivityRefs = mainActivityRefs;
        }

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case CommonData.LOAD_TIME_DATA_SUCS: // 加载网络时间成功
                    Date date = (Date) msg.obj;
                    if (date != null)
                    {
                        firstActivityRefs.get().calibreBirthDay(date);
                    }
                    else
                    {
                        firstActivityRefs.get().calibreBirthDay(null);
                    }
                    break;
                case CommonData.LOAD_TIME_DATA_FAIL:
                    firstActivityRefs.get().calibreBirthDay(null);
                    break;
            }
        }
    }

    /**
     * 检测日子是否对
     * 
     * @param date date
     */
    private void calibreBirthDay(Date date)
    {
        int month, day;
        if (date != null)
        {
            month = date.getMonth() + 1;
            day = date.getDate();
        }
        else
        {
            Calendar calendar = Calendar.getInstance();
            month = calendar.get(Calendar.MONTH) + 1;
            day = calendar.get(Calendar.DATE);
        }
        if (month == 6 && day == 28)
        {
            isNormalDay = false;
//            soundManager.playBgSound(FirstActivity.this,3);
        }
        else
        {
            skimCover();
        }
        ZYMLog.info("ZYM month is " + month + "  day is " + day);
    }


    @Override
    protected void onDestroy()
    {
        if (skimTimes <= 1)
        {
            if (soundManager != null)
            {
                soundManager.releaseAll();
//                soundManager.clearSoundPoolCount();
            }
        }
        ZYMLog.info("service firstActivity onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (skimTimes <= 1)
        {
            if (soundManager != null)
            {
                soundManager.pauseSound();
            }
        }
    }

    @Override
    protected void onResume()
    {
        ZYMLog.info(" ------------ FirstActivity ----------------- app display");
        super.onResume();
        if (soundManager != null)
        {
            soundManager.resumeSound();
        }
    }
}
