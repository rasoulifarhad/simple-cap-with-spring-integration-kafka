package com.farhad.example.cap.simple.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.farhad.example.cap.gateway.CapitalizeService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class CapitalizeResource {
    

    @Autowired
    CapitalizeService capService;

    @PostMapping(path = "/api/fn/blocking/cap",produces = MediaType.APPLICATION_JSON_VALUE,consumes =  MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> blockingCapitalize(@RequestBody String capitalize) { 

        String capitalized = capService.capitalize(capitalize);

        log.info("{} capitalized to {}",capitalize,capitalized);

        return ResponseEntity.ok(capitalized);
    }

    @PostMapping(path = "/api/cap",produces = MediaType.APPLICATION_JSON_VALUE,consumes =  MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> capitalize(@RequestBody String capitalize) { 

        String capitalized = capService.capitalize(capitalize);

        log.info("{} capitalized to {}",capitalize,capitalized);

        return ResponseEntity.ok(capitalized);
    }

}
