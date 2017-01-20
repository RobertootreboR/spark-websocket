import lombok.Getter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Duration;

import java.time.Instant;
import java.util.Optional;

/**
 * Created by robert on 12.01.17.
 */
@Getter

public class WeatherForecast {
    public static final String errorMessage = "Problems occured while downloading weather. Try again in about 10 minutes.";
    String weather;
    Optional<Instant> lastRequest= Optional.empty();
    static String getJSON(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = new OkHttpClient().newCall(request).execute();
        return response.body().string();
    }
    public String update(){
        if(Duration.between(lastRequest.orElseGet(() ->Instant.now().minusSeconds(3700)),Instant.now()).toMinutes()>=60
                || weather.equals(errorMessage)){
            weather = getWeather();
        }
        lastRequest=Optional.of(Instant.now());
        return weather;
    }

    public String getWeather() {
        try {
            JSONObject krkWeather = new JSONObject(getJSON("http://api.openweathermap.org/data/2.5/forecast/city?id=3094802&APPID=6fdd34768f67187a44e6fe59a0f72e4a"));
            return getBasicInfo(krkWeather) + " " + getClouds(krkWeather);
        } catch (IOException | JSONException ex) {
            return errorMessage;
        }
    }

    private String getClouds(JSONObject krkWeather) throws JSONException {
        return "clouds: " + krkWeather.getJSONArray("list").getJSONObject(0).getJSONObject("clouds").get("all") +"% of sky";
    }

    private String getBasicInfo(JSONObject krkWeather) throws JSONException {
        JSONObject basics = krkWeather.getJSONArray("list").getJSONObject(0).getJSONObject("main");
        return "temperature: "+ basics.get("temp") + "K , humidity: " + basics.get("humidity") + "%, pressure: "+ basics.get("pressure") + "hPa,";

    }

}
