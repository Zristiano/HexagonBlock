package com.example.yuanmengzeng.hexagonblock;

import android.util.Log;

/**
 *
 * Created by yuanmengzeng on 2016/5/17.
 */
public class ZYMLog  {
    private static final boolean sLogEnabled = true;

    /**
     * 获取打印log的语句的类名+方法名+行数，将其作为tag
     * @return “类名_方法名_行数“
     */
    private static String getLogTag()
    {
        try
        {
            Exception exception = new Exception();
            StackTraceElement[] elements = exception.getStackTrace();
            if(elements.length<3)
            {
                return "LogTag_error";
            }
            String className = elements[2].getClassName();
            int index = className.lastIndexOf(".");
            className = className.substring(index+1);
            return className+"_"+elements[2].getMethodName()+"_"+elements[2].getLineNumber();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            return "LogTag_error";
        }
    }

    /**
     * 使用“类名_方法名_行数“作为tag打印日志
     * @param string 需要打印的信息（一般）
     */

    public static void info(String string)
    {
        if(sLogEnabled) Log.i(getLogTag(), string);
    }

    /**
     * 使用“类名_方法名_行数“作为tag打印日志
     * @param string 需要打印的信息(警告)
     */
    public static void warn(String string) {
        if(sLogEnabled) Log.w(getLogTag(), string);
    }

    /**
     ** 使用“类名_方法名_行数“作为tag打印日志
     * @param string 需要打印的信息（错误）
     */
    public static void error(String string) {
        if(sLogEnabled) Log.e(getLogTag(),string);
    }
}
