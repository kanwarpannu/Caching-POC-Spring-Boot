package com.poc.cache.cachedemo.services;

import com.poc.cache.cachedemo.models.Employee;
import com.poc.cache.cachedemo.repositories.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @CachePut(value = "EMPLOYEE", key = "'EMPNUM:' + #employee.id")
    public Employee saveEmployee(Employee employee) {
        log.info("Saving employee into DB");
        return employeeRepository.save(employee);
    }

    @Cacheable(value = "EMPLOYEE", key = "'EMPNUM:' + #id")
    public Employee getEmployeeById(Long id) {
        log.info("Looking into DB for employee {}", id);
        return employeeRepository.findById(id).orElse(null);
    }

    public List<Employee> getAllEmployee() {
        return employeeRepository.findAll();
    }

    @CacheEvict(value = "EMPLOYEE", key = "'EMPNUM:' + #id")
    public void deleteEmployeeById(Long id) {
        employeeRepository.deleteById(id);
        log.info("Employee number {} evicted from cache", id);
    }

    @CacheEvict(value = "EMPLOYEE", allEntries = true)
    public void deleteAllEmployee() {
        employeeRepository.deleteAll();
        log.info("Entire cache is cleared");
    }
}
