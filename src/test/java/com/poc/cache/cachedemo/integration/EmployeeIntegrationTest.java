package com.poc.cache.cachedemo.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.cache.cachedemo.models.Employee;
import com.poc.cache.cachedemo.repositories.EmployeeRepository;
import com.poc.cache.cachedemo.services.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@DisplayName("GIVEN employee controller is called")
@SpringBootTest
@ActiveProfiles("test")
@EnableCaching
class EmployeeIntegrationTest {

    String id = "1";
    Employee employee = Employee.builder()
            .id(Long.valueOf(id))
            .name("John Doe")
            .role("Programmer")
            .phoneNumber("1234567890")
            .build();
    Employee employee2 = Employee.builder()
            .id(2L)
            .name("First Last")
            .role("programmer")
            .phoneNumber("111111111")
            .build();
    List<Employee> employeeList = Arrays.asList(employee, employee2);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    public void setup() {
        employeeRepository.deleteAll();
        cacheManager.getCache("EMPLOYEE").clear();
    }

    @Test
    @DisplayName("WHEN [POST] endpoint is hit THEN create a new employee in database AND persist the same in Cache")
    public void createEmployee() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/employee")
                .content(objectMapper.writeValueAsString(employee))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(201))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Employee actualResponse = objectMapper.readValue(response, new TypeReference<Employee>() {
        });

        assertAll(
                () -> assertEquals(employee.getId(), actualResponse.getId()),
                () -> assertEquals(employee.getName(), actualResponse.getName()),
                () -> assertEquals(employee.getPhoneNumber(), actualResponse.getPhoneNumber()),
                () -> assertEquals(employee.getRole(), actualResponse.getRole())
        );
        assertAll(
                () -> assertEquals(employee.getId(), employeeRepository.findById(actualResponse.getId()).orElse(null).getId()),
                () -> assertEquals(employee.getName(), employeeRepository.findById(actualResponse.getId()).orElse(null).getName()),
                () -> assertEquals(employee.getPhoneNumber(), employeeRepository.findById(actualResponse.getId()).orElse(null).getPhoneNumber()),
                () -> assertEquals(employee.getRole(), employeeRepository.findById(actualResponse.getId()).orElse(null).getRole())
        );
        Employee cachedEmployee = (Employee) cacheManager.getCache("EMPLOYEE")
                .get("EMPNUM:" + actualResponse.getId()).get();
        assertEquals(actualResponse, cachedEmployee);

        cacheManager.getCache("EMPLOYEE").evict("EMPNUM:" + actualResponse.getId());
        employeeRepository.delete(actualResponse);
    }

    @Test
    @DisplayName("WHEN [GET] endpoint is hit with a valid employee id as path variable THEN get the employee from cache")
    public void getEmployee_whenEmployeeFound() throws Exception {
        cacheManager.getCache("EMPLOYEE").put("EMPNUM:" + id, employee);

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/employee/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Employee actualResponse = objectMapper.readValue(response, new TypeReference<Employee>() {
        });

        assertAll(
                () -> assertEquals(employee.getId(), actualResponse.getId()),
                () -> assertEquals(employee.getName(), actualResponse.getName()),
                () -> assertEquals(employee.getPhoneNumber(), actualResponse.getPhoneNumber()),
                () -> assertEquals(employee.getRole(), actualResponse.getRole())
        );
        assertNull(employeeRepository.findById(Long.parseLong(id)).orElse(null));
        cacheManager.getCache("EMPLOYEE").evict("EMPNUM:" + id);
    }

    @Test
    @DisplayName("WHEN [GET] endpoint is hit with a invalid employee id as path variable THEN send employee not found message")
    public void getEmployee_whenEmployeeNotFound() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/employee/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        assertEquals(response, "Employee not found");
    }

    @Test
    @DisplayName("WHEN [GET] ALL endpoint is hit THEN send all employee data")
    public void getAllEmployee() throws Exception {
        employeeRepository.saveAll(employeeList);

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        List<Employee> actualResponse = objectMapper.readValue(response, new TypeReference<List<Employee>>() {
        });

        assertAll(
                () -> assertEquals(employeeList.get(0).getId(), actualResponse.get(0).getId()),
                () -> assertEquals(employeeList.get(0).getName(), actualResponse.get(0).getName()),
                () -> assertEquals(employeeList.get(0).getPhoneNumber(), actualResponse.get(0).getPhoneNumber()),
                () -> assertEquals(employeeList.get(0).getRole(), actualResponse.get(0).getRole()),
                () -> assertEquals(employeeList.get(1).getId(), actualResponse.get(1).getId()),
                () -> assertEquals(employeeList.get(1).getName(), actualResponse.get(1).getName()),
                () -> assertEquals(employeeList.get(1).getPhoneNumber(), actualResponse.get(1).getPhoneNumber()),
                () -> assertEquals(employeeList.get(1).getRole(), actualResponse.get(1).getRole())
        );
    }

    @Test
    @DisplayName("WHEN [GET] Read Only endpoint is hit with a valid employee id as path variable THEN get the employee from cache, if not exist then go to DB but do not persist the data in Cache")
    public void getEmployeeReadonly_whenEmployeeFound() throws Exception {
        employeeRepository.save(employee);

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/employee/{id}/readonly", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Employee actualResponse = objectMapper.readValue(response, new TypeReference<Employee>() {
        });

        assertAll(
                () -> assertEquals(employee.getId(), actualResponse.getId()),
                () -> assertEquals(employee.getName(), actualResponse.getName()),
                () -> assertEquals(employee.getPhoneNumber(), actualResponse.getPhoneNumber()),
                () -> assertEquals(employee.getRole(), actualResponse.getRole())
        );
        assertThrows(NullPointerException.class, () -> cacheManager.getCache("EMPLOYEE")
                .get("EMPNUM:" + employee.getId()).get());
    }

    @Test
    @DisplayName("WHEN [GET] Read Only endpoint is hit with a invalid employee id as path variable THEN get the employee from cache, if not exist then go to DB AND finally return employee not found")
    public void getEmployeeReadonly_whenEmployeeNotFound() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/employee/{id}/readonly", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        assertEquals(response, "Employee not found");

        assertThrows(NullPointerException.class, () -> cacheManager.getCache("EMPLOYEE")
                .get("EMPNUM:" + employee.getId()).get());
    }

    @Test
    @DisplayName("WHEN [UPDATE] endpoint is hit with a valid employee id as path variable THEN update the employee in database AND cache")
    public void updateEmployee_GivenCorrectEmployee() throws Exception {
        employeeRepository.save(employee);
        Employee updatedEmployee = Employee.builder()
                .id(Long.valueOf(id))
                .name("John Wick")
                .role("Classified")
                .phoneNumber("666")
                .build();

        MvcResult mvcResult = mockMvc.perform(put("/api/v1/employee/{id}", id)
                .content(objectMapper.writeValueAsString(updatedEmployee))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Employee actualResponse = objectMapper.readValue(response, new TypeReference<Employee>() {
        });

        assertAll(
                () -> assertEquals(updatedEmployee.getId(), actualResponse.getId()),
                () -> assertEquals(updatedEmployee.getName(), actualResponse.getName()),
                () -> assertEquals(updatedEmployee.getPhoneNumber(), actualResponse.getPhoneNumber()),
                () -> assertEquals(updatedEmployee.getRole(), actualResponse.getRole())
        );
        Employee cachedEmployee = (Employee) cacheManager.getCache("EMPLOYEE")
                .get("EMPNUM:" + actualResponse.getId()).get();
        assertEquals(updatedEmployee, cachedEmployee);

        cacheManager.getCache("EMPLOYEE").evict("EMPNUM:" + actualResponse.getId());
        employeeRepository.delete(actualResponse);
    }

    @Test
    @DisplayName("WHEN [UPDATE] endpoint is hit with a invalid employee id as path variable THEN return employee not found message")
    public void updateEmployee_GivenIncorrectEmployee() throws Exception {
        MvcResult mvcResult = mockMvc.perform(put("/api/v1/employee/{id}", id)
                .content(objectMapper.writeValueAsString(employee2))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        assertEquals(response, "Employee not found");
    }

    @Test
    @DisplayName("WHEN [DELETE] endpoint is hit with a valid employee id as path variable THEN delete that employee from database AND cache")
    public void deleteEmployee_GivenValidEmployee() throws Exception {
        employeeRepository.save(employee);
        cacheManager.getCache("EMPLOYEE").put("EMPNUM:" + id, employee);

        MvcResult mvcResult = mockMvc.perform(delete("/api/v1/employee/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        assertEquals(response, "Employee deleted");
        assertThrows(NullPointerException.class, () -> cacheManager.getCache("EMPLOYEE")
                .get("EMPNUM:" + employee.getId()).get());
    }

    @Test
    @DisplayName("WHEN [DELETE] endpoint is hit with a invalid employee id as path variable THEN return employee not found message")
    public void deleteEmployee_GivenInvalidEmployee() throws Exception {
        MvcResult mvcResult = mockMvc.perform(delete("/api/v1/employee/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        assertEquals(response, "Employee not found");
    }

    @Test
    @DisplayName("WHEN [DELETE] ALL endpoint is hit THEN delete all employees in database AND cache")
    public void deleteAllEmployee() throws Exception {
        employeeRepository.saveAll(employeeList);
        cacheManager.getCache("EMPLOYEE").put("EMPNUM:" + employeeList.get(0).getId(), employeeList.get(0));
        cacheManager.getCache("EMPLOYEE").put("EMPNUM:" + employeeList.get(1).getId(), employeeList.get(1));

        MvcResult mvcResult = mockMvc.perform(delete("/api/v1/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        assertEquals(response, "All Employees deleted");
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> cacheManager.getCache("EMPLOYEE")
                        .get("EMPNUM:" + employeeList.get(0).getId()).get()),
                ()->assertThrows(NullPointerException.class, () -> cacheManager.getCache("EMPLOYEE")
                        .get("EMPNUM:" + employeeList.get(1).getId()).get())
        );
        assertAll(
                () -> assertNull(employeeRepository.findById(employeeList.get(0).getId()).orElse(null)),
                () -> assertNull(employeeRepository.findById(employeeList.get(1).getId()).orElse(null))
        );
    }
}