package com.poc.cache.cachedemo.controllers;

import com.poc.cache.cachedemo.models.Employee;
import com.poc.cache.cachedemo.services.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

@RestController
@RequestMapping(path = "/api/v1")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping(value = "/employee")
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        Employee savedEmployee = employeeService.saveEmployee(employee);
        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }

    @GetMapping(value = "/employee/{id}")
    public ResponseEntity<?> getEmployee(@PathVariable("id") String id) {
        Employee savedEmployee = employeeService.getEmployeeById(Long.parseLong(id));
        if (savedEmployee != null) {
            return new ResponseEntity<>(savedEmployee, HttpStatus.OK);
        }
        return new ResponseEntity<>("Employee not found", HttpStatus.OK);
    }

    @GetMapping(value = "/employee")
    public ResponseEntity<List<Employee>> getAllEmployee() {
        List<Employee> employeeList = employeeService.getAllEmployee();
        return ResponseEntity.ok(employeeList);
    }

    @PutMapping(value = "/employee/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable("id") String id, @RequestBody Employee employee) {
        Employee savedEmployee = employeeService.getEmployeeById(Long.parseLong(id));
        if (savedEmployee != null) {
            savedEmployee.setRole(employee.getRole());
            savedEmployee.setName(employee.getName());
            savedEmployee.setPhoneNumber(employee.getPhoneNumber());

            Employee returnedEmployee = employeeService.saveEmployee(savedEmployee);
            return new ResponseEntity<>(returnedEmployee, HttpStatus.OK);
        }
        return new ResponseEntity<>("Employee not found", HttpStatus.OK);
    }

    @DeleteMapping(value = "/employee/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable("id") String id) {
        Employee savedEmployee = employeeService.getEmployeeById(Long.parseLong(id));
        if (savedEmployee != null) {
            employeeService.deleteEmployeeById(Long.parseLong(id));
            return new ResponseEntity<>("Employee deleted", HttpStatus.OK);
        }
        return new ResponseEntity<>("Employee not found", HttpStatus.OK);
    }

    @DeleteMapping(value = "/employee")
    public ResponseEntity<String> deleteAllEmployee() {
        employeeService.deleteAllEmployee();
        return new ResponseEntity<>("All Employees deleted", HttpStatus.OK);
    }
}
