package com.senacrs.beerExplorer.controller;

import com.opencsv.CSVReader;
import com.senacrs.beerExplorer.model.Beer;
import com.senacrs.beerExplorer.model.Rating;
import com.senacrs.beerExplorer.model.User;
import com.senacrs.beerExplorer.repository.BeerRepository;
import com.senacrs.beerExplorer.repository.RatingRepository;
import com.senacrs.beerExplorer.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@RestController
public class StartController {

    Logger logger = LoggerFactory.getLogger(StartController.class);

    @Autowired
    private BeerRepository beerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RatingRepository ratingRepository;

    private List<User> usersToSave = new ArrayList<>();
    private List<Beer> beersToSave = new ArrayList<>();
    private List<Rating> ratingToSave = new ArrayList<>();



    @RequestMapping(method = RequestMethod.POST, path = "/initialize")
    public void initiaize() {
        logger.info("Initializing...");
        try {
            logger.info("Loading Beers...");
            loadBeers();
            logger.info("Loading Users...");
            loadUsers();
            logger.info("Loading Ratings...");
            loadRatings();
            logger.info("Initialization completed!");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
        }
    }

    @Transactional
    public void loadBeers() throws Exception {
        ClassPathResource classPathResource = new ClassPathResource("initial_data/beers.csv");
        File file = new File(classPathResource.getURI());
        FileReader fileReader = new FileReader(file);
        CSVReader csvReader = new CSVReader(fileReader);
        String[] nextRecord;
        while ((nextRecord = csvReader.readNext()) != null) {
            Beer beer = new Beer();
            beer.setId(Long.valueOf(nextRecord[0]));
            beer.setName(nextRecord[1]);
            beer.setBrewery(nextRecord[4]);
            beer.setStyle(nextRecord[2]);
            beer.setAbv(nextRecord[3]);
            beersToSave.add(beer);
        }
        beersToSave = beerRepository.saveAll(beersToSave);
    }

    @Transactional
    public void loadUsers() throws Exception {
        ClassPathResource classPathResource = new ClassPathResource("initial_data/users.csv");
        File file = new File(classPathResource.getURI());
        FileReader fileReader = new FileReader(file);
        CSVReader csvReader = new CSVReader(fileReader);
        String[] nextRecord;
        while ((nextRecord = csvReader.readNext()) != null) {
            User user = new User();
            user.setId(Long.parseLong(nextRecord[0]));
            user.setName(nextRecord[1]);
            usersToSave.add(user);
        }
        usersToSave = userRepository.saveAll(usersToSave);
    }

    @Transactional
    public void loadRatings() throws Exception {
        ClassPathResource classPathResource = new ClassPathResource("initial_data/ratings.csv");
        File file = new File(classPathResource.getURI());
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String nextRecord;
        List<Rating> ratingList = new ArrayList<>();
        int count=0;
        while ((nextRecord = bufferedReader.readLine()) != null) {
            count++;
            Rating rating = Rating.parseRating(nextRecord);
            ratingList.add(rating);
            if(count>100000){
                logger.info("SAVING 100K");
                ratingRepository.saveAll(ratingList);
                ratingList.clear();
                count = 0;
            }
        }
    }


}
