package com.poc.cache.cachedemo.repositories;

import com.poc.cache.cachedemo.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {
}
