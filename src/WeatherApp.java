import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class WeatherApp {
    //берем инфу о погоде для выбранной локи
    public static JSONObject getWeatherData(String locationName) {
        //получаем координаты используя апи
        JSONArray locationData = getLocationData(locationName);
        //долгота и широта
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");
        //апи запрос с координатами
        String urlString ="https://api.open-meteo.com/v1/forecast?" + "latitude="+ latitude+ "&longitude="+ longitude + "&hourly=temperature_2m,relativehumidity_2m,weathercode,windspeed_10m&timezone=America%2FLos_Angeles";
        try {
            //вызов апи и получение ответа
            HttpURLConnection conn = fetchApiResponse(urlString);
            //проверка статуса
            if (conn.getResponseCode() != 200){
                System.out.println("error");
                return null;
            }
            //сохраняем результ
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while(scanner.hasNext()){
                resultJson.append(scanner.nextLine());
            }
            scanner.close();
            conn.disconnect();
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);
            JSONArray weathercode = (JSONArray) hourly.get("weathercode");
            String weatherCondition = convertWeatherCode((long) weathercode.get(index));
            JSONArray relativeHumidity = (JSONArray) hourly.get("relativehumidity_2m");
            long humidity = (long) relativeHumidity.get(index);
            JSONArray windspeedData = (JSONArray) hourly.get("windspeed_10m");
            double windspeed = (double) windspeedData.get(index);
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed",windspeed);
            return weatherData;



        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }
    public static JSONArray getLocationData(String locationName){
        //меняем плюс на пробел для запроса в ссылке
        locationName = locationName.replaceAll(" ", "+");
        //апи юрл с параметром локи
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";
        try {
            //вызов апи и получение ответа
            HttpURLConnection conn = fetchApiResponse(urlString);
            //проверка соединения
            if(conn.getResponseCode() != 200){
                System.out.println("error");
                return null;
            }else {
                //сохраеяем апи результат
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());
                //читает и сохраняет результат в стринг билдер
                while (scanner.hasNext()){
                    resultJson.append(scanner.nextLine());
                }
                //завершение
                scanner.close();
                conn.disconnect();
                //парсим джсон строку в джсон обжект
                JSONParser parser = new JSONParser();
                JSONObject resultJsonObj = (JSONObject)  parser.parse(String.valueOf(resultJson));
                //получаем результаты локи из апи из имени локи
                JSONArray locationData= (JSONArray) resultJsonObj.get("results");
                return locationData;

            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    private static HttpURLConnection fetchApiResponse(String urlString){
        try {
            //попытка создать соединение
            URL url = new URL(urlString);
            HttpURLConnection conn =  (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            //коннект к юрл
            conn.connect();
            return conn;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
    private static int findIndexOfCurrentTime(JSONArray timeList){
        String currentTime = getCurrentTime();
        for(int i = 0; i < timeList.size(); i++){
            String time = (String) timeList.get(i);
            if(time.equalsIgnoreCase(currentTime)){
                return i;
            }
        }
        return 0;
    }
    private static String getCurrentTime(){
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");
        String formattedDateTime  = currentDateTime.format(formatter);
        return formattedDateTime;
    }
    private static String convertWeatherCode(long weathercode){
        String weatherCondition = "";
        if(weathercode == 0L){
            weatherCondition = "Clear";
        }else if (weathercode > 0L && weathercode <= 3L){
            weatherCondition = "Cloudy";
        } else if ((weathercode >= 51L && weathercode <= 67L ) || (weathercode >= 80L && weathercode <= 99L)) {
            weatherCondition = "Rain";
        } else if (weathercode >= 71L && weathercode <= 77L) {
            weatherCondition = "Snow";
        }
        return weatherCondition;
    }

}
