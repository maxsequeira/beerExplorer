package com.senacrs.beerExplorer.service;

import com.senacrs.beerExplorer.model.Beer;
import com.senacrs.beerExplorer.model.Rating;
import com.senacrs.beerExplorer.model.User;
import com.senacrs.beerExplorer.model.UserInfo;
import com.senacrs.beerExplorer.repository.BeerRepository;
import com.senacrs.beerExplorer.repository.RatingRepository;
import com.senacrs.beerExplorer.repository.UserRepository;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.evaluation.RegressionEvaluator;
import org.apache.spark.ml.recommendation.ALS;
import org.apache.spark.ml.recommendation.ALSModel;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class ImportService {

    Logger logger = LoggerFactory.getLogger(ImportService.class);

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    RatingRepository ratingRepository;

    @Autowired
    RecommendationService recommendationService;

    public void importData(Long userId, String username, String pass) {

        System.setProperty("webdriver.chrome.driver", "src/main/resources/selenium/chromedriver");
        ChromeOptions options = new ChromeOptions();

        options.setHeadless(true);

        //Instantiate Web Driver and open web page
        ChromeDriver driver = new ChromeDriver(options);
        driver.get("https://untappd.com/login");
        WebElement login = driver.findElementById("username");
        login.sendKeys(username);
        WebElement password = driver.findElementById("password");
        password.sendKeys(pass);
        password.submit();
        driver.get("https://untappd.com/user/" + username);
        List<WebElement> rates = driver.findElementsByClassName("checkin");
        List<BeerRead> dataRead = new ArrayList<>();
        for (WebElement rate : rates) {
            List<WebElement> beerElement = rate.findElement(By.className("text")).findElements(By.xpath("a[@href]"));
            String href = beerElement.get(1).getAttribute("href");
            String beerName = beerElement.get(1).getText();
            String brewery = beerElement.get(2).getText();
            String ratingString = rate.findElement(By.className("rating")).getAttribute("class").split(" ")[1].split("-")[1];
            Float rating = Float.parseFloat(ratingString);
            rating *= 0.01f;
            BeerRead beerRead = new BeerRead();
            beerRead.href = href;
            beerRead.beerName = beerName;
            beerRead.brewery = brewery;
            beerRead.rating = rating.toString();
            dataRead.add(beerRead);
            logger.info("Reading beer: " + beerName + " from: " + brewery + " with rating:" + rating);
        }

        dataRead.stream().forEach(data -> {
            driver.get(data.href);
            data.style = driver.findElement(By.className("style")).getText();
            data.abv = driver.findElement(By.className("abv")).getText().substring(0, 3);
            saveData(data, userId);
            logger.info(data.toString());
        });
        driver.close();
    }

    private void saveData(BeerRead beerRead, Long userId) {
        List<Beer> beerFromDb = beerRepository.findBeersByNameAndBrewery(beerRead.beerName, beerRead.brewery);
        Beer beer = new Beer();
        if (beerFromDb.isEmpty()) {
            beer.setName(beerRead.beerName);
            beer.setBrewery(beerRead.brewery);
            beer.setAbv(beerRead.abv);
            beer.setStyle(beerRead.style);
            beer = beerRepository.save(beer);
        } else {
            beer = beerFromDb.get(0);
        }

        Rating rating = new Rating();
        rating.setRating(Float.valueOf(beerRead.rating));
        rating.setUserId(userId);
        rating.setBeerId(beer.getId());


        if (!ratingRepository.findOne(Example.of(rating)).isPresent()) {
            rating.setTimestamp(new Date().getTime());
            ratingRepository.save(rating);
        } else {
            logger.info("Skipping already saved rate...");
        }

//        if(ratingRepository.countByUserId(userId) >= 5){
//            Thread thread = new Thread(){
//                @Override
//                public void run() {
//                    recommendationService.trainModel();
//                    logger.info("NEW MODEL!");
//                }
//            };
//            thread.run();
//        }


    }


    private class BeerRead {
        String href;
        String beerName;
        String brewery;
        String rating;
        String style;
        String abv;

        @Override
        public String toString() {
            return "BeerRead{" +
                    "href='" + href + '\'' +
                    ", beerName='" + beerName + '\'' +
                    ", brewery='" + brewery + '\'' +
                    ", rating='" + rating + '\'' +
                    ", style='" + style + '\'' +
                    ", abv='" + abv + '\'' +
                    '}';
        }
    }
}
