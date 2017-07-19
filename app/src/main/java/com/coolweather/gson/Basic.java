package com.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @author wuyunfeng
 * @mail wuyunfeng2017@gmail.com
 * @create 2017/7/18.
 */
public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }

}
