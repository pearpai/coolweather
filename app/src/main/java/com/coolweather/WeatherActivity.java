package com.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.app.APP;
import com.coolweather.gson.Weather;
import com.coolweather.service.AutoUpdateService;
import com.coolweather.util.Constant;
import com.coolweather.util.HttpUtil;
import com.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @author wuyunfeng
 * @mail wuyunfeng2017@gmail.com
 * @create 2017/7/19.
 */
public class WeatherActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;

    private Button navButton;

    public SwipeRefreshLayout swipeRefresh;

    private ScrollView weatherLayout;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private TextView aqiText;

    private TextView pm25Text;

    private TextView comfortText;

    private TextView carWashText;

    private TextView sportText;

    private ImageView bingPicImg;

    private String weatherIdT;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_weather);

        // 初始化控件

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        navButton = (Button) findViewById(R.id.nav_button);

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        swipeRefresh.setColorSchemeColors(getResources().getColor( R.color.colorPrimary));

        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);

        titleCity = (TextView) findViewById(R.id.title_city);

        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);

        degreeText = (TextView) findViewById(R.id.degree_text);

        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);

        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);

        aqiText = (TextView) findViewById(R.id.aqi_text);

        pm25Text = (TextView) findViewById(R.id.pm25_text);

        comfortText = (TextView) findViewById(R.id.comfort_text);

        sportText = (TextView) findViewById(R.id.sport_text);

        carWashText = (TextView) findViewById(R.id.car_wash_text);

        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String weatherString = prefs.getString("weather", null);



        if (weatherString != null) {
            // 有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            assert weather != null;
            weatherIdT = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            // 无缓存时 去服务器查询天气
            weatherIdT = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherIdT);
        }

        swipeRefresh.setOnRefreshListener(() -> requestWeather(weatherIdT));

        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(APP.getContext()).load(bingPic).into(bingPicImg);
        } else {
            loadBingPic();
        }

        navButton.setOnClickListener(view -> {
          drawerLayout.openDrawer(GravityCompat.START);
        });

    }

    /**
     * 根据天气id请求城市天气信息
     *
     * @param weatherId 城市天气id
     */
    public void requestWeather(final String weatherId) {
        String weatherUrl = Constant.WEATHER_URL + weatherId;

        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(APP.getContext(), "获取天气信息失败", Toast.LENGTH_SHORT).show();
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(() -> {
                    if (weather != null && "ok".equals(weather.status)) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                        editor.putString("weather", responseText);
                        editor.apply();
                        showWeatherInfo(weather);
                        weatherIdT = weatherId;
                    } else {
                        Toast.makeText(APP.getContext(), "获取天气失败", Toast.LENGTH_SHORT).show();
                    }
                    swipeRefresh.setRefreshing(false);
                });
            }
        });
        loadBingPic();
    }

    /**
     * 处理并展示 weather 实体类中的数据
     *
     * @param weather 天气信息
     */
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;

        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();

        weather.forecastList.forEach(forecast -> {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);

            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);

            forecastLayout.addView(view);

        });

        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }

        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String sport = "运动建议：" + weather.suggestion.sport.info;

        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);

        weatherLayout.setVisibility(View.VISIBLE);

        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);

    }

    /**
     * 加载必应每日一图
     */
    private void loadBingPic() {
        HttpUtil.sendOkHttpRequest(Constant.BING_PIC_URL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(() -> Glide.with(APP.getContext()).load(bingPic).into(bingPicImg));
            }
        });
    }

}
