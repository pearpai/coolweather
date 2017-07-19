package com.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @author wuyunfeng
 * @mail wuyunfeng2017@gmail.com
 * @create 2017/7/18.
 */
public class Forecast {

    public String date;

    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public More more;

    public class Temperature {

        public String max;

        public String min;

    }

    public class More {

        @SerializedName("txt_d")
        public String info;

    }

}
