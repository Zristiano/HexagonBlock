package com.example.yuanmengzeng.hexagonblock;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.text.BoringLayout;

/**
 *  音效管理类
 * Created by yuanmengzeng on 2016/6/14.
 */
public class SoundManager implements SoundPool.OnLoadCompleteListener{

    private static SoundManager soundManager;

    private static boolean SOUND_ENABLE = true;
    private static boolean bg_ENABLE = true;
    private Context mContext;
    private SoundPool soundPool;
    private MediaPlayer mediaPlayer;
    private SoundCallBack mSoundCallBack;

    private int loadSucCount = 0;
    private int soundExpandId;   // Block变大的音效
    private int soundPlaceId;    // Block放置音效
    private int soundRevertId;    // Block回到原位音效
    private int soundMatchId;    // Block有匹配时的音效
    private int soundRowId[] = new int[10];



    public SoundManager(Context context,SoundCallBack callBack){
        mContext = context;
        mSoundCallBack = callBack;
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        soundPool.setOnLoadCompleteListener(this);
        mediaPlayer = new MediaPlayer();
        initSound();
        playBgSound(0);
    }



    /**
     * 异步加载音效
     */
    public void initSound(){
        if(loadSucCount==14) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    soundExpandId = soundPool.load(mContext,R.raw.expand,1);
                    soundRevertId = soundPool.load(mContext,R.raw.revert,1);
                    soundMatchId = soundPool.load(mContext,R.raw.match,1);
                    soundPlaceId = soundPool.load(mContext,R.raw.place,1);
                    for(int i = 0; i<10;i++){
                        soundRowId[i] = soundPool.load(mContext,R.raw.row0+i,1);
                    }
                }catch (Exception e){
                    ZYMLog.error(" "+e);
                }
            }
        }).start();
    }



    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        if (status!=0) return;
        loadSucCount++;
        ZYMLog.warn("onLoadComplete is called "+loadSucCount);
//        play1stBgSound();
    }


    public void playBlockExpandSound(){
        if(!SOUND_ENABLE) return;
        int streamId = soundPool.play(soundExpandId,1.0f,1.0f,1,0,1.0f);
        if(streamId==0){
            ZYMLog.error("playing block Expand sound fail");
        }
    }

    public void playRowClearSound(int i){
        if(!SOUND_ENABLE||i<0||i>9) return;
        int streamId = soundPool.play(soundRowId[i],1.0f,1.0f,1,0,1.0f);
        if(streamId==0){
            ZYMLog.error("playing block Clear sound fail");
        }
    }

    public void playBlockPlaceSound(){
        if(!SOUND_ENABLE) return;
        int streamId = soundPool.play(soundPlaceId,1.0f,1.0f,1,0,1.0f);
        if(streamId==0){
            ZYMLog.error("playing block Place sound fail");
        }
    }

    public void playBlockRevertSound(){
        if(!SOUND_ENABLE) return;
        int streamId = soundPool.play(soundRevertId,1.0f,1.0f,1,0,1.0f);
        if(streamId==0){
            ZYMLog.error("playing block Revert sound fail");
        }
    }

    public void playBlockMatchSound(){
        if(!SOUND_ENABLE) return;
        int streamId = soundPool.play(soundMatchId,1.0f,1.0f,1,0,1.0f);
        if(streamId==0){
            ZYMLog.error("playing block Match sound fail");
        }
    }



    int curBgId = 0;
    int bgSoundId[] = {0,0,0,0};
    String bgName[] = {"bg_default.mp3","bg_007.mp3","bg_four_hand.mp3","happybirthday.mp3"};
    /**
     * 播放背景音乐
     * @param order bg order
     */
    public void playBgSound(int order){
        curBgId = order;
        mediaPlayer.reset();
        try {
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
//                    mp.start();
                    mediaPlayer.setLooping(true);
                    for(int i=0;i<bgSoundId.length;i++){
                        bgSoundId[i]=0;
                    }
                    bgSoundId[curBgId] = mediaPlayer.getAudioSessionId();
                    mSoundCallBack.onBgSoundLoadSuc( mp);
                }
            });
            mediaPlayer.setVolume(0.3f,0.3f);
            AssetFileDescriptor assetFileDescriptor = mContext.getAssets().openFd(bgName[order]);
            mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(),assetFileDescriptor.getStartOffset(),assetFileDescriptor.getLength());
            mediaPlayer.prepareAsync();
        }catch (Exception e){
            ZYMLog.error(""+e);
        }
    }

    public void stopBgSound(){
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            bgSoundId[curBgId]=0;
        }
    }

    public void setBgEnable(boolean enable){
        bg_ENABLE = enable;
        if(bg_ENABLE) playBgSound(curBgId);
        else stopBgSound();
    }

    public void pauseSound(){
        soundPool.autoPause();
        if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }



    public void releaseAll(){
        ZYMLog.info("releaseAll");
        soundPool.release();
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    public void resumeSound(){
        ZYMLog.info("resumeSound");
        soundPool.autoResume();
        if(mediaPlayer!=null&&bg_ENABLE){
            mediaPlayer.start();
        }
    }


    public interface SoundCallBack{
        void onBgSoundLoadSuc(MediaPlayer mp);
    }

}
