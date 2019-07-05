package com.senacrs.beerExplorer.repository;

import com.senacrs.beerExplorer.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    Long countByUserId(Long userId);

    Rating findRatingByBeerIdAndAndUserIdAndRating(Long beerId, Long userId, Float rating);
}