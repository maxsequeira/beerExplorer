package com.senacrs.BeerExplorer.controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class HomeController {

        @RequestMapping("/")
        public String index() {
            return "Bem vindo ao Beer Explorer!";

    }
}
