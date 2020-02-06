package com.poc.cache.cachedemo.controllers;

import com.poc.cache.cachedemo.models.Employee;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1")
public class EmployeeController {

    //create employee stores in h2 database
    @PostMapping("/employee")
    public ResponseEntity<String> createEmployee(@RequestBody Employee employee){
        return null;
    }


    //get employee from cache if non existent call h2 database and cache the same

    //get all employee

    //update employee

    //delete employee
}
