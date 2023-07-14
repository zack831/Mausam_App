package com.zack83.mausam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.icu.util.IndianCalendar;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.kwabenaberko.openweathermaplib.constant.Units;
import com.kwabenaberko.openweathermaplib.implementation.OpenWeatherMapHelper;
import com.kwabenaberko.openweathermaplib.implementation.callback.CurrentWeatherCallback;
import com.kwabenaberko.openweathermaplib.implementation.callback.ThreeHourForecastCallback;
import com.kwabenaberko.openweathermaplib.model.currentweather.CurrentWeather;
import com.kwabenaberko.openweathermaplib.model.threehourforecast.ThreeHourForecast;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout home;
    private ProgressBar loading;
    private TextView cityName, temperature, condition,temperature2, windspeed;
    private TextInputEditText editcity;
    private ImageView imgview, srch_btn, icon;
    private RecyclerView rlvweather;
    private ArrayList<WeatherRvModal> weatherRvModalArrayList;
    private WeatherAdapter weatherAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private double longitude,latitude;
    private String cityname;

    private double lat,lon;
    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_main);
        home = findViewById(R.id.home);
        loading = findViewById(R.id.loading);
        cityName = findViewById(R.id.cityName);
        temperature = findViewById(R.id.temperature);
        condition = findViewById(R.id.condition);
        editcity = findViewById(R.id.editcity);
        imgview = findViewById(R.id.imgview);
        srch_btn = findViewById(R.id.srch_btn);
        icon = findViewById(R.id.icon);
        home = findViewById(R.id.home);
        rlvweather = findViewById(R.id.rlvweather);
        weatherRvModalArrayList = new ArrayList<>();
        weatherAdapter = new WeatherAdapter(this, weatherRvModalArrayList);
        rlvweather.setAdapter(weatherAdapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        try {
            cityname = getcityname(latitude,longitude);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        cityName.setText(cityname);
        getWeatherInfo(latitude,longitude,cityname);

        condition.setOnLongClickListener(view->{
            Toast.makeText(this, "ESTHER CHANG", Toast.LENGTH_SHORT).show();
            return false;
        });

        srch_btn.setOnClickListener(view -> {
            String city = editcity.getText().toString();
            if (city.isEmpty()){
                Toast.makeText(MainActivity.this, "Please Provide a city name", Toast.LENGTH_SHORT).show();
            }
            else {
//                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//                try {
//                    List<Address> addressList =geocoder.getFromLocationName("kolkata",1);
//                    lat = addressList.get(0).getLatitude();
//                    lon = addressList.get(0).getLatitude();
//                    city = addressList.get(0).getAdminArea();
//                    Log.v(TAG,"lan"+lon);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
                getWeatherInfo(latitude,longitude,city);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==PERMISSION_CODE){
             if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                 Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
             }else {
                 Toast.makeText(this, "Please Provide Necessary Permissions", Toast.LENGTH_SHORT).show();
                 finish();
             }
        }
    }

    private String getcityname(double latitude,double longitude) throws IOException {
        String cityname = "Not Found";
        Toast.makeText(MainActivity.this, "Latitude"+latitude, Toast.LENGTH_SHORT).show();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
        cityname = addresses.get(0).getAdminArea();
        Toast.makeText(this, "cityName:"+cityname, Toast.LENGTH_SHORT).show();
//        String stateName = addresses.get(0).getAddressLine(1);
//        String countryName = addresses.get(0).getAddressLine(2);


//        Geocoder gcd = new Geocoder(getBaseContext(),Locale.getDefault());
//        try{
//            List<Address> addresses = gcd.getFromLocation(latitude,longitude,1);
//            for (Address adr :addresses){
//                if(adr != null){
//                    String city= adr.getLocality();
//                    if (city!=null && !city.equals("")){
//                        cityname=city;
//                    }else {
//                        Log.d("TAG","City Not Found");
//                        Toast.makeText(this, "User city not found", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        return cityname;
    }

    private void getWeatherInfo(double latitude, double longitude,String city) {
        String url = "http://api.openweathermap.org/data/3.0/onecall?lat=" + latitude + "&lon=" + longitude + "&units=metric&appid=9bb9485e113f7f8e07520d9f65878e19";
        loading.setVisibility(View.GONE);
        home.setVisibility(View.VISIBLE);

//        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                loading.setVisibility(View.GONE);
//                home.setVisibility(View.VISIBLE);
//                weatherRvModalArrayList.clear();
//
//                String temp = null;
//                try {
//                    temp = response.getJSONObject("current").getString("temp");
//                    temperature.setText(temp + "°C");
//                    String desc = response.getJSONObject("weather").getString("description");
//                    String icn = response.getJSONArray("weather").getJSONObject(0).getString("icon");
//                    Picasso.get().load("https://openweathermap.org/img/wn/" + icn + ".png").into(icon);
//                    condition.setText(desc);
//
//                    JSONArray hourly = response.getJSONArray("0");
//                    for (int i = 0; i < hourly.length(); i++) {
//                        JSONObject hourObj = hourly.getJSONObject(i);
//                        String temper = hourObj.getString("temp");
//                        String img = hourObj.getJSONObject("weather").getJSONObject("0").getString("icon");
//                        String wind = hourObj.getString("wind_speed");
//                        weatherRvModalArrayList.add(new WeatherRvModal(temper, img, wind));
//                    }
//                    weatherAdapter.notifyDataSetChanged();
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(MainActivity.this, "Enter a valid city", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        requestQueue.add(jsonObjectRequest);

        OpenWeatherMapHelper helper = new OpenWeatherMapHelper(getString(R.string.OPEN_WEATHER_MAP_API_KEY));
        helper.setUnits(Units.METRIC);

        helper.getCurrentWeatherByCityName(""+city, new CurrentWeatherCallback() {
            @Override
            public void onSuccess(CurrentWeather currentWeather) {
                Log.v(TAG, "Coordinates: " + currentWeather.getCoord().getLat() + ", "+currentWeather.getCoord().getLon() +"\n"
                        +"Weather Description: " + currentWeather.getWeather().get(0).getDescription() + "\n"
                        +"Temperature: " + currentWeather.getMain().getTempMax()+"\n"
                        +"Wind Speed: " + currentWeather.getWind().getSpeed() + "\n"
                        +"City, Country: " + currentWeather.getName() + ", " + currentWeather.getSys().getCountry()
                );

                double temp = currentWeather.getMain().getTempMax();
                temperature.setText(temp+" °C");
                String desc = currentWeather.getWeather().get(0).getDescription();
                condition.setText(desc);
                String name = currentWeather.getName();
                cityName.setText(name);
                Picasso.get().load("https://openweathermap.org/img/wn/02n.png").into(icon);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.v(TAG, throwable.getMessage());
            }
        });

        helper.getThreeHourForecastByGeoCoordinates(latitude,longitude, new ThreeHourForecastCallback() {
            @Override
            public void onSuccess(ThreeHourForecast threeHourForecast) {
                Log.v(TAG, "City/Country: "+ threeHourForecast.getCity().getName() + "/" + threeHourForecast.getCity().getCountry() +"\n"
                        +"Forecast Array Count: " + threeHourForecast.getCnt() +"\n"
                        //For this example, we are logging details of only the first forecast object in the forecasts array
                        +"First Forecast Date Timestamp: " + threeHourForecast.getList().get(0).getDt() +"\n"
                        +"First Forecast Weather Description: " + threeHourForecast.getList().get(0).getWeather().get(0).getDescription()+ "\n"
                        +"First Forecast Max Temperature: " + threeHourForecast.getList().get(0).getMain().getTempMax()+"\n"
                        +"First Forecast Wind Speed: " + threeHourForecast.getList().get(0).getWind().getSpeed() + "\n"
                );

                for (int i = 0 ; i<4; i++){
                    int temp = (int) threeHourForecast.getList().get(i).getMain().getTempMax();
                    Long img = threeHourForecast.getList().get(i).getDt();
                    int wind = (int) threeHourForecast.getList().get(i).getWind().getSpeed();
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.v(TAG, throwable.getMessage());
            }
        });
    }
}