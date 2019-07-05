package com.senacrs.beerExplorer.controller;

import com.senacrs.beerExplorer.model.Beer;
import com.senacrs.beerExplorer.model.Rating;
import com.senacrs.beerExplorer.model.User;
import com.senacrs.beerExplorer.model.UserInfo;
import com.senacrs.beerExplorer.service.ImportService;
import com.senacrs.beerExplorer.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RecommendationController {

    @Autowired
    RecommendationService service;

    @Autowired
    ImportService importService;

    @RequestMapping(method = RequestMethod.POST, path = "/addUser")
    public User addUser(@RequestParam String userName) {
        return service.addUser(userName);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/addNewUser")
    public String addNewUser(@RequestParam String userName) {
        return service.addNewUser(userName);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/getAvaliableImportServices")
    public List<String> getAvaliableImportServices() {
        List<String> services = new ArrayList<>();
        services.add("untappd");
        return services;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/importData")
    public String importData(String importServiceName, Long userId, String sourceUserName, String sourcePassword) {
        if (importServiceName.equals("untappd")) {
            importService.importData(userId, sourceUserName, sourcePassword);
        } else {
            throw new RuntimeException("Service: " + importServiceName + " not found!");
        }
        return "Data successfully imported!";
    }

    @RequestMapping(method = RequestMethod.POST, path = "/rateBeer")
    public Rating rateBeer(@RequestParam Long beerId,
                           @RequestParam Long userId,
                           @RequestParam Float rating) {
        return service.rateBeer(beerId, userId, rating);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/addBeer")
    public Beer addBeer(@RequestParam String beerName,
                        @RequestParam String brewery,
                        @RequestParam String style,
                        String abv) {
        return service.addBeer(beerName, brewery, style, abv);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/getUserInfo")
    public UserInfo getUserInfo(@RequestParam String userName) {
        return service.getUserInfo(userName);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/getAllBeers")
    public List<Beer> getAllBeers() {
        return service.getAllBeer();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/getRecommendation")
    public List<Beer> getRecommendation(@RequestParam Long userId,
                                        @RequestParam(defaultValue = "5") Integer numOfRecommendations) {
    return service.getRecommendation(userId, numOfRecommendations);
    }


}