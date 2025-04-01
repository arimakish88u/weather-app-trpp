import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeatherAppGui extends JFrame {
    private JSONObject weatherData;

    public WeatherAppGui(){
        //заголовок приложухи
        super("Weather App");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(450, 650);
        setLocationRelativeTo(null);//гуи в центр экрана
        setLayout(null); //вывод в центр гуи
        setResizable(false);//запрет на изменения размера окна

        addGuiComponents();
    }
    private void addGuiComponents() {
        //строка поиска
        JTextField searchTextField = new JTextField();
        searchTextField.setBounds(15, 15, 351, 45);
        //меняем шрифт и размер
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));
        add(searchTextField);

        //иконка погоды
        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);
        //текст температуры
        JLabel temperatureText = new JLabel("Weather App");
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));
        //центрируем текст
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);
        //описание в целом
        JLabel weatherConditionDesc = new JLabel("Enter the name of the city");
        weatherConditionDesc.setBounds(0, 405, 450, 36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);
        //иконка влажности
        JLabel humidityImage = new JLabel(loadImage("src/assets/humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);
        //текст влажности
        JLabel humidityText = new JLabel("<html><b>Humidity</b>     %</html>");
        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);
        //иконка скорость ветра
        JLabel windspeedImage = new JLabel(loadImage("src/assets/windspeed.png"));
        windspeedImage.setBounds(220, 500, 74, 66);
        add(windspeedImage);
        //текст скорости ветра
        JLabel windspeedText = new JLabel("<html><b>Windspeed</b>   km/h<html>");
        windspeedText.setBounds(310, 500, 85, 55);
        windspeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windspeedText);
        //кнопка поиска
        JButton searchButton = new JButton(loadImage("src/assets/search.png"));

        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13, 47, 45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userInput = searchTextField.getText();
                if (userInput.replaceAll("\\s", "").length() <= 0) {
                    return;
                }
                weatherData = WeatherApp.getWeatherData(userInput);
                String weatherCondition = (String) weatherData.get("weather_condition");
                switch(weatherCondition){
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/assets/snow.pngImage"));
                        break;
                }
                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + " C");
                weatherConditionDesc.setText(weatherCondition);
                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");
                double windspeed = (double) weatherData.get("windspeed");
                windspeedText.setText("<html><b>Windspeed</b> " + windspeed + "km/h</html>");
            }
        });
        add(searchButton);
    }

    private ImageIcon loadImage (String resourcePath){
            //для изображений
            try {
                BufferedImage image = ImageIO.read(new File(resourcePath));
                return new ImageIcon(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("error");
            return null;
        }
    }
