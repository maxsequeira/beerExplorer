package com.senacrs.beerExplorer.service;

import com.senacrs.beerExplorer.controller.StartController;
import com.senacrs.beerExplorer.model.Beer;
import com.senacrs.beerExplorer.model.Rating;
import com.senacrs.beerExplorer.model.User;
import com.senacrs.beerExplorer.model.UserInfo;
import com.senacrs.beerExplorer.repository.BeerRepository;
import com.senacrs.beerExplorer.repository.RatingRepository;
import com.senacrs.beerExplorer.repository.UserRepository;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

import java.util.Objects;

import org.apache.spark.ml.evaluation.RegressionEvaluator;
import org.apache.spark.ml.recommendation.ALS;
import org.apache.spark.ml.recommendation.ALSModel;
import org.springframework.web.context.WebApplicationContext;

@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RecommendationService {

    Logger logger = LoggerFactory.getLogger(RecommendationService.class);

    private boolean isTraning;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RatingRepository ratingRepository;

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    JavaSparkContext sc;

    MatrixFactorizationModel matrixFactorizationModel;


    SparkSession spark;
    @Value("spark.sql.warehouse.dir")
    String warehouse;

    @Value("${model.folder}")
    String modelPath;

    int rank = 20;
    int numIterations = 15;
    double lambda = 0.10;
    double alpha = 1.00;
    long seed = 12345L;
    boolean implicitPrefs = false;


    public SparkSession getSpark() {
        if (Objects.isNull(spark)) {
            spark = SparkSession
                    .builder()
                    .appName("BeerExplorer")
                    .master("local[*]")
                    .config("spark.sql.warehouse.dir", warehouse).config("fetchsize",10000)
                    .getOrCreate();
        }
        return spark;
    }


    public void trainModel() {

        if (isTraning) {
            return;
        }
        isTraning = true;
        logger.info("Training started!");
        List<Rating> ratingList = new ArrayList<>();
        ratingList = ratingRepository.findAll();
        JavaRDD<Rating> ratingsRDD = sc.parallelize(ratingList);
        Dataset<Row> ratings = getSpark().createDataFrame(ratingsRDD, Rating.class);
        Dataset<Row>[] splits = ratings.randomSplit(new double[]{0.8, 0.2});
        Dataset<Row> training = splits[0];
        Dataset<Row> test = splits[1];
        logger.info("Data loaded");

        ALS als = new ALS()
                .setAlpha(alpha)
                .setRank(rank).setSeed(seed)
                .setImplicitPrefs(implicitPrefs)
                .setMaxIter(numIterations)
                .setRegParam(0.01)
                .setUserCol("userId")
                .setItemCol("beerId")
                .setRatingCol("rating");
        ALSModel model = als.fit(ratings);
        model.setColdStartStrategy("drop");
        logger.info("Model generated");
        try {
            model.save(modelPath);
            logger.info("Model saved!");

            matrixFactorizationModel = null;

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        isTraning = true;
    }


    public String addNewUser(String username) {
        addUser(username);
        return "User:" + username + " has been added successfully";
    }

    public User addUser(String username) {
        User user = new User();
        user.setName(username);
        if (!userRepository.findByName(username).isEmpty()) {
            throw new RuntimeException("Username already exists!");
        }
        user = userRepository.save(user);
        return user;
    }

    public Rating rateBeer(Long beerId, Long userId, Float rate) {
        Rating rating = new Rating();
        rating.setBeerId(beerId);
        rating.setUserId(userId);
        rating.setRating(rate);
        rating = ratingRepository.save(rating);
        return rating;
    }

    public Beer addBeer(String name, String brewery, String style, String abv) {
        Beer beer = new Beer();
        beer.setName(name);
        beer.setBrewery(brewery);
        beer.setStyle(style);
        beer.setAbv(abv);
        beer = beerRepository.save(beer);
        return beer;
    }

    public UserInfo getUserInfo(String userName) {
        User user = userRepository.findFirstByName(userName);
        Long ratings = ratingRepository.countByUserId(user.getId());
        return new UserInfo(user, ratings);
    }

    public UserInfo getUserInfo(Long userId) {
        User user = userRepository.findById(userId).get();
        Long ratings = ratingRepository.countByUserId(user.getId());
        return new UserInfo(user, ratings);
    }

    public List<Beer> getAllBeer() {
        return beerRepository.findAll();
    }

    public List<Beer> getRecommendation(Long userId, Integer numOfRecommendations) {
        UserInfo userInfo = getUserInfo(userId);
        if (userInfo.getNumberOfRatings() < 5) {
            String msg = "The user " + userInfo.getUser().getName() + " has less of 5 ratings, plese rate at lest 5 beers";
            logger.error(msg);
            throw new RuntimeException(msg);
        }
        MatrixFactorizationModel matrixFactorizationModel = MatrixFactorizationModel.load(getSpark().sparkContext(), modelPath);
        org.apache.spark.mllib.recommendation.Rating[] ratings;
        ratings = matrixFactorizationModel.recommendProducts(userId.intValue(), numOfRecommendations);
        List<Beer> beers = new ArrayList<>();
        for (int count = 0; count < ratings.length; count++) {
            int beerId = ratings[count].product();
            beers.add(beerRepository.findById(Long.valueOf(beerId)).get());
        }
        return beers;
    }

    private MatrixFactorizationModel getModel() {
        if (matrixFactorizationModel == null) {
            matrixFactorizationModel = MatrixFactorizationModel.load(getSpark().sparkContext(), modelPath);
        }
        return matrixFactorizationModel;
    }


//    public String addRatiing(String username, String beerId, Integer rate) {
//        Rating rating = new Rating();
//        rating.setBeerId();
//        userRepository.save(user);
//        return "User:" + username + " has been added successfully";
//    }
//    def tune_ALS(train_data, validation_data, maxIter, regParams, ranks):
//            """
//    grid search function to select the best model based on RMSE of
//    validation data
//    Parameters
//    ----------
//    train_data: spark DF with columns ['userId', 'movieId', 'rating']
//
//    validation_data: spark DF with columns ['userId', 'movieId', 'rating']
//
//    maxIter: int, max number of learning iterations
//
//    regParams: list of float, one dimension of hyper-param tuning grid
//
//    ranks: list of float, one dimension of hyper-param tuning grid
//
//            Return
//    ------
//    The best fitted ALS model with lowest RMSE score on validation data
//    """
//            # initial
//    min_error = float('inf')
//    best_rank = -1
//    best_regularization = 0
//    best_model = None
//    for rank in ranks:
//            for reg in regParams:
//            # get ALS model
//            als = ALS().setMaxIter(maxIter).setRank(rank).setRegParam(reg)
//            # train ALS model
//            model = als.fit(train_data)
//            # evaluate the model by computing the RMSE on the validation data
//            predictions = model.transform(validation_data)
//    evaluator = RegressionEvaluator(metricName="rmse",
//                                    labelCol="rating",
//                                    predictionCol="prediction")
//    rmse = evaluator.evaluate(predictions)
//    print('{} latent factors and regularization = {}: '
//                  'validation RMSE is {}'.format(rank, reg, rmse))
//            if rmse < min_error:
//    min_error = rmse
//            best_rank = rank
//    best_regularization = reg
//            best_model = model
//    print('\nThe best model has {} latent factors and '
//                  'regularization = {}'.format(best_rank, best_regularization))
//            return best_model

}
