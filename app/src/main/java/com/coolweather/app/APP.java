package com.coolweather.app;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePalApplication;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wuyunfeng
 * @mail wuyunfeng2017@gmail.com
 * @create 2017/7/17.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class APP extends Application {

    private static Context context;


    @Override
    public void onCreate() {
        super.onCreate();
        context =  getApplicationContext();
        LitePalApplication.initialize(context);

    }

    public static Context getContext(){
        return context;
    }

}
