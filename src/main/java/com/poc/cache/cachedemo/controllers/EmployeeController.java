package com.poc.cache.cachedemo.controllers;

import com.poc.cache.cachedemo.models.Employee;
import com.poc.cache.cachedemo.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @PostMapping("/employee")
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        Employee savedEmployee = employeeRepository.save(employee);
        return new ResponseEntity(savedEmployee, HttpStatus.CREATED);
    }


    //get employee from cache if non existent call h2 database and cache the same

    //get all employee

    @PutMapping("/employee/{id}")
    public ResponseEntity<Employee> updateEmployee(@RequestParam("id") String id, @RequestBody Employee employee) {
        Employee savedEmployee = employeeRepository.findById(Long.parseLong(id)).orElse(null);
//TODO: missing null case
        savedEmployee.setName(employee.getName());
        savedEmployee.setRole(employee.getRole());
        savedEmployee.setPhoneNumber(employee.getPhoneNumber());

        Employee returnedEmployee = employeeRepository.save(savedEmployee);

        return new ResponseEntity(returnedEmployee, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/employee/{id}")
    public ResponseEntity<String> deleteEmployee(@RequestParam("id") String id) {
        employeeRepository.deleteById(Long.parseLong(id));
        return new ResponseEntity("employee deleted", HttpStatus.OK);
        //TODO: what if id not present
    }
}
