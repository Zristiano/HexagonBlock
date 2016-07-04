package com.example.yuanmengzeng.hexagonblock;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Date;
import android.os.Handler;
import android.os.Message;

/**
 *
 * Created by yuanmengzeng on 2016/6/21.
 */
public class ReqTimeThread extends Thread{

    private Handler handler;

    public ReqTimeThread(Handler handler)
    {
        this.handler = handler;
    }

    @Override
    public void run() {

        Message message = new Message();

        try {
            URL timeUrl = new URL("http://www.baidu.com");
//            URL timeUrl = new URL(CommonData.REQUEST_TIMESTAMP);
            URLConnection connct =  timeUrl.openConnection();
            ZYMLog.info("reqTimeThread run ");
            connct.setConnectTimeout(2000);
            connct.connect();
            ZYMLog.info("ZYM  longTime is "+connct.getDate());
            Date date = new Date(connct.getDate());
            message.what = CommonData.LOAD_TIME_DATA_SUCS;
            message.obj = date;
            handler.sendMessage(message);

        }catch(Exception e){
            ZYMLog.error(""+e);
            handler.sendEmptyMessage(CommonData.LOAD_TIME_DATA_FAIL);
        }

    }
}
