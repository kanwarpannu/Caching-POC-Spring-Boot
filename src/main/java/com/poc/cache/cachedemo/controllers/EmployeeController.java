package com.poc.cache.cachedemo.controllers;

import com.poc.cache.cachedemo.models.Employee;
import com.poc.cache.cachedemo.repositories.EmployeeRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/v1")
@AllArgsConstructor
public class EmployeeController {

    private EmployeeRepository employeeRepository;

    @PostMapping(value = "/employee", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        Employee savedEmployee = employeeRepository.save(employee);
        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }

    //TODO: get employee from cache if non existent call h2 database and cache the same

    @GetMapping(value = "/employee/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getEmployee(@PathVariable("id") String id) {
        Optional<Employee> savedEmployee = employeeRepository.findById(Long.parseLong(id));
        if (!savedEmployee.isPresent()) {
            return new ResponseEntity<>("Employee not found", HttpStatus.OK);
        }
        return new ResponseEntity<>(savedEmployee, HttpStatus.OK);
    }

    @GetMapping(value = "/employee", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Employee>> getAllEmployee() {
        List<Employee> employeeList = employeeRepository.findAll();
        return ResponseEntity.ok(employeeList);
    }

    @PutMapping(value = "/employee/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateEmployee(@PathVariable("id") String id, @RequestBody Employee employee) {
        Optional<Employee> savedEmployee = employeeRepository.findById(Long.parseLong(id));
        if (!savedEmployee.isPresent()) {
            return new ResponseEntity<>("Employee not found", HttpStatus.OK);
        }
        savedEmployee.get().setName(employee.getName());
        savedEmployee.get().setRole(employee.getRole());
        savedEmployee.get().setPhoneNumber(employee.getPhoneNumber());

        Employee returnedEmployee = employeeRepository.save(savedEmployee.get());

        return new ResponseEntity<>(returnedEmployee, HttpStatus.OK);
    }

    @DeleteMapping(value = "/employee/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteEmployee(@PathVariable("id") String id) {
        Optional<Employee> savedEmployee = employeeRepository.findById(Long.parseLong(id));
        if (!savedEmployee.isPresent()) {
            return new ResponseEntity<>("Employee not found", HttpStatus.OK);
        }

        employeeRepository.deleteById(Long.parseLong(id));
        return new ResponseEntity<>("Employee deleted", HttpStatus.OK);
    }
}
