package com.poc.cache.cachedemo.controllers;

import com.poc.cache.cachedemo.models.Employee;
import com.poc.cache.cachedemo.services.EmployeeService;
import lombok.RequiredArgsConstructor;
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

//TODO: use and put commands from this Reference to readme file https://markheath.net/post/exploring-redis-with-docker
//TODO: Test the code
//TODO: Add swagger UI
//TODO: format and analyse entire code

@RestController
@RequestMapping(path = "/api/v1")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping(value = "/employee", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        Employee savedEmployee = employeeService.saveEmployee(employee).orElse(null);
        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }

    @GetMapping(value = "/employee/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getEmployee(@PathVariable("id") String id) {
        Optional<Employee> savedEmployee = employeeService.getEmployeeById(Long.parseLong(id));
        if (savedEmployee.isPresent()) {
            return new ResponseEntity<>(savedEmployee, HttpStatus.OK);
        }
        return new ResponseEntity<>("Employee not found", HttpStatus.OK);
    }

    @GetMapping(value = "/employee", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Employee>> getAllEmployee() {
        List<Employee> employeeList = employeeService.getAllEmployee();
        return ResponseEntity.ok(employeeList);
    }

    @PutMapping(value = "/employee/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateEmployee(@PathVariable("id") String id, @RequestBody Employee employee) {
        Optional<Employee> savedEmployee = employeeService.getEmployeeById(Long.parseLong(id));
        if (savedEmployee.isPresent()) {
            savedEmployee.get().setName(employee.getName());
            savedEmployee.get().setRole(employee.getRole());
            savedEmployee.get().setPhoneNumber(employee.getPhoneNumber());

            Employee returnedEmployee = employeeService.saveEmployee(savedEmployee.get()).orElse(null);
            return new ResponseEntity<>(returnedEmployee, HttpStatus.OK);
        }
        return new ResponseEntity<>("Employee not found", HttpStatus.OK);
    }

    @DeleteMapping(value = "/employee/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteEmployee(@PathVariable("id") String id) {
        Optional<Employee> savedEmployee = employeeService.getEmployeeById(Long.parseLong(id));
        if (savedEmployee.isPresent()) {
            employeeService.deleteEmployeeById(Long.parseLong(id));
            return new ResponseEntity<>("Employee deleted", HttpStatus.OK);
        }
        return new ResponseEntity<>("Employee not found", HttpStatus.OK);
    }
}
