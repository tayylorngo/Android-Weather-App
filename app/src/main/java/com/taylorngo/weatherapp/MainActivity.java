package com.taylorngo.weatherapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity {

    private RequestQueue mQueue;
    private double temperature;
    private int sunrise;
    private int sunset;
    private String weatherDesc;
    private String cityName;
    private String picURL;
    private ImageView weatherImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button getWeatherButton = findViewById(R.id.getWeatherBtn);
        mQueue = Volley.newRequestQueue(this);

        getWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWeather();
            }
        });

        weatherImage = findViewById(R.id.weatherIcon);
    }

    public void getWeather(){
        EditText cityText = findViewById(R.id.cityInput);
        String city = cityText.getText().toString();
        String API_KEY = getString(R.string.open_weather_app_api_key);
        String urlString = "https://api.openweathermap.org/data/2.5/weather?q="
                + city + "&appid=" + API_KEY + "&units=imperial";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlString, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            cityName = response.getString("name");
                            JSONArray weatherArr = response.getJSONArray("weather");
                            for(int i = 0; i < weatherArr.length(); i++){
                                JSONObject weather = weatherArr.getJSONObject(i);
                                weatherDesc = weather.getString("description");
                                picURL = weather.getString("icon");
                            }
                            JSONObject main = response.getJSONObject("main");
                            temperature = main.getDouble("temp");
                            JSONObject sys = response.getJSONObject("sys");
                            sunrise = sys.getInt("sunrise");
                            sunset = sys.getInt("sunset");
                            updateInfo();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }

    public void updateInfo(){
        TextView city = findViewById(R.id.cityNameText);
        TextView temperatureText = findViewById(R.id.temperatureText);
        TextView weatherDescription = findViewById(R.id.weatherConditionText);
        TextView sunriseText = findViewById(R.id.sunriseText);
        TextView sunsetText = findViewById(R.id.sunsetText);
        city.setText(cityName);
        temperatureText.setText(temperature + "°F");
        weatherDescription.setText(weatherDesc);
        java.util.Date sunriseTime = new java.util.Date((long) sunrise * 1000);
        java.util.Date sunsetTime = new java.util.Date((long) sunset * 1000);

        String sunriseTimeString = "";
        if(sunriseTime.getHours() < 10){
            sunriseTimeString += "0" + sunriseTime.getHours() + ":";
        }
        else{
            sunriseTimeString += sunriseTime.getHours() + ":";
        }
        if(sunriseTime.getMinutes() < 10){
            sunriseTimeString += "0" + sunriseTime.getMinutes();
        }
        else{
            sunriseTimeString += sunriseTime.getMinutes();
        }

        String sunsetTimeString = "";
        if(sunsetTime.getHours() < 10){
            sunsetTimeString += "0" + sunsetTime.getHours() + ":";
        }
        else{
            sunsetTimeString += sunsetTime.getHours() + ":";
        }
        if(sunsetTime.getMinutes() < 10){
            sunsetTimeString += "0" + sunsetTime.getMinutes();
        }
        else{
            sunsetTimeString += sunsetTime.getMinutes();
        }
        sunriseText.setText("Sunrise: " + sunriseTimeString);
        sunsetText.setText("Sunset: " + sunsetTimeString);

        String imgURL = "https://openweathermap.org/img/wn/" + picURL + "@2x.png";
        Picasso.get().load(imgURL).into(weatherImage);
    }

}