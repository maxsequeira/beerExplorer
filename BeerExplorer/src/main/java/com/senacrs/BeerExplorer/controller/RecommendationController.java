package com.senacrs.BeerExplorer.controller;

import com.senacrs.BeerExplorer.model.RecommendedBeer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class RecommendationController {


    @GetMapping("/recommendOne")
    @ResponseBody
    public RecommendedBeer recommendOne(){
        RecommendedBeer recommendedBeer = new RecommendedBeer();
        recommendedBeer.setAbv("6.8");
        recommendedBeer.setPais("United States");
        recommendedBeer.setAvaliacaoMedia("3.7");
        recommendedBeer.setIbu("65");
        recommendedBeer.setFabricante("Sierra Nevada Brewing Co.");
        recommendedBeer.setNome("Celebration Ale.");
        recommendedBeer.setDescricao("The long, cold nights of winter are a little brighter with Celebration Ale. Wonderfully robust and rich, Celebration Ale is dry-hopped for a lively, intense aroma. Brewed especially for the holidays, it is perfect for a festive gathering or for a quiet evening at home.");
        recommendedBeer.setEstilo("American IPA");
        return recommendedBeer;
    }

    @GetMapping("/recommend5")
    @ResponseBody
    public List<RecommendedBeer> recommend5(){
        List<RecommendedBeer> recommendedBeers = new ArrayList<>();
        for(int x = 0; x < 5; x++) {
            RecommendedBeer recommendedBeer = new RecommendedBeer();
            recommendedBeer.setAbv("6.8");
            recommendedBeer.setPais("United States");
            recommendedBeer.setAvaliacaoMedia("3.7");
            recommendedBeer.setIbu("65");
            recommendedBeer.setFabricante("Sierra Nevada Brewing Co.");
            recommendedBeer.setNome("Celebration Ale.");
            recommendedBeer.setDescricao("The long, cold nights of winter are a little brighter with Celebration Ale. Wonderfully robust and rich, Celebration Ale is dry-hopped for a lively, intense aroma. Brewed especially for the holidays, it is perfect for a festive gathering or for a quiet evening at home.");
            recommendedBeer.setEstilo("American IPA");
            recommendedBeers.add(recommendedBeer);
        }
        return recommendedBeers;
    }
}
