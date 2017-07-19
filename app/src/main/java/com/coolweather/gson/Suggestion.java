package com.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @author wuyunfeng
 * @mail wuyunfeng2017@gmail.com
 * @create 2017/7/18.
 */
public class Suggestion {

    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    public Sport sport;

    public class Comfort {

        @SerializedName("txt")
        public String info;

    }

    public class CarWash {

        @SerializedName("txt")
        public String info;

    }

    public class Sport {

        @SerializedName("txt")
        public String info;

    }


}
