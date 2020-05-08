package com.example.rabbit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@SuppressWarnings("unused")
@RestController
public class SampleController {


    @Autowired
    SampleService service;

    @RequestMapping(value = "/v1/test/rabbit", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<String> responseEntity() throws Exception {

        var response = service.test();

        return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.OK);
    }
}
