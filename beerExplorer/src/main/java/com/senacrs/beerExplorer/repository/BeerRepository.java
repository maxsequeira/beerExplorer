package com.senacrs.beerExplorer.repository;

import com.senacrs.beerExplorer.model.Beer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BeerRepository extends JpaRepository<Beer, Long> {

    List<Beer> findBeersByNameAndBrewery(String beerName, String brewery);
}