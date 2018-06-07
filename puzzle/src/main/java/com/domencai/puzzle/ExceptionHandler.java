package com.domencai.puzzle;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Domen、on 2017/9/27.
 */

public class ExceptionHandler implements Thread.UncaughtExceptionHandler{

    private static ExceptionHandler handler = new ExceptionHandler();
    private Thread.UncaughtExceptionHandler defaultHandler;
    private File localErrorSave;
    private Context context;
    private StringBuilder sb = new StringBuilder();
    private ExceptionHandler(){}
    public static ExceptionHandler getInstance(){
        return handler;
    }

    public void initConfig(Context context) {
        this.context = context;
        File saveSpacePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/007");
        localErrorSave = new File(saveSpacePath,"error.txt");
        if (!saveSpacePath.exists()){
            saveSpacePath.mkdirs();
        }
        if (!localErrorSave.exists()){
            try {
                localErrorSave.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        writeErrorToLocal(t, e);
    }

    private void writeErrorToLocal(Thread t, Throwable e) {
        try {
            BufferedWriter fos = new BufferedWriter(new FileWriter(localErrorSave,true));
            PackageManager packageManager = context.getPackageManager();
            String line = "\n----------------------------------------------------------------------------------------\n";
            sb.append(line);
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            sb.append(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis
                    ()))).append("<---->").append("包名::").append(packageInfo.packageName).append
                    ("<---->版本名::").append(packageInfo.versionName).append("<---->版本号::").append
                    (packageInfo.versionCode).append("\n");
            sb.append("手机制造商::").append(Build.MANUFACTURER).append("\n");
            sb.append("手机型号::").append(Build.MODEL).append("\n");
            sb.append("CPU架构::").append(Build.CPU_ABI).append("\n");
            sb.append(e.toString()).append("\n");
            StackTraceElement[] trace = e.getStackTrace();
            for (StackTraceElement traceElement : trace)
                sb.append("\n\tat ").append(traceElement);
            sb.append("\n");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Throwable[] suppressed = e.getSuppressed();
                for (Throwable se : suppressed)
                    sb.append("\tat ").append(se.getMessage());
            }
            fos.write(sb.toString());
            fos.close();


        } catch (IOException | PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
            defaultHandler.uncaughtException(t,e1);
        }
    }
}
