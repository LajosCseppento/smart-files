package dev.lajoscseppento.smartfiles.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class IndexController {

    @GetMapping("/")
    public Mono<String> index() {
        return Mono.just("SmartFiles");
    }

    @GetMapping("/model")
    public Mono<String> model() {
        return Mono.just("SmartFiles");
    }

}
