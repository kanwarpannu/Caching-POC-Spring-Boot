package com.poc.cache.cachedemo.services;

import com.poc.cache.cachedemo.models.Employee;
import com.poc.cache.cachedemo.repositories.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    //Return type and key of cacheput and cacheable is exactly the same
    @CachePut(value = "EMPLOYEE", key = "'EMPNUM:' + #employee.id")
    public Optional<Employee> saveEmployee(Employee employee) {
        return Optional.of(employeeRepository.save(employee));
    }

    @Cacheable(value = "EMPLOYEE", key = "'EMPNUM:' + #id")
    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    public List<Employee> getAllEmployee() {
        return employeeRepository.findAll();
    }

    public void deleteEmployeeById(Long id) {
        employeeRepository.deleteById(id);
        this.evictCacheByEmployee(id);
    }

    @CacheEvict(value = "EMPLOYEE", key = "'EMPNUM:' + #id")
    public void evictCacheByEmployee(Long id) {
    }


}
