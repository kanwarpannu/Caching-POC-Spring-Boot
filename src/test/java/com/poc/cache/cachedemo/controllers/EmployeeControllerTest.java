package com.poc.cache.cachedemo.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.cache.cachedemo.models.Employee;
import com.poc.cache.cachedemo.repositories.EmployeeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//TODO: missing cacheable test case, make sure to use temp cache and not redis during test cases

@WebMvcTest(controllers = EmployeeController.class)
@DisplayName("GIVEN employee controller is called")
class EmployeeControllerTest {

    @MockBean
    private EmployeeRepository employeeRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    String id = "1";

    Employee employee = Employee.builder()
            .id(Long.valueOf(id))
            .name("John Doe")
            .role("Programmer")
            .phoneNumber("1234567890")
            .build();

    Employee updatedEmployee = Employee.builder()
            .id(2L)
            .name("First Last")
            .role("programmer")
            .phoneNumber("111111111")
            .build();

    List<Employee> employeeList = Arrays.asList(employee, updatedEmployee);

    @Test
    @DisplayName("WHEN POST endpoint is hit THEN create a new employee in database")
    void createEmployee() throws Exception {
        when(employeeRepository.save(employee)).thenReturn(employee);

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/employee")
                .content(objectMapper.writeValueAsString(employee))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(201))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        Employee actualResponse = objectMapper.readValue(response, new TypeReference<Employee>() {
        });

        verify(employeeRepository).save(employee);

        assertAll(
                () -> assertEquals(employee.getId(), actualResponse.getId()),
                () -> assertEquals(employee.getName(), actualResponse.getName()),
                () -> assertEquals(employee.getPhoneNumber(), actualResponse.getPhoneNumber()),
                () -> assertEquals(employee.getRole(), actualResponse.getRole())
        );
    }

    @Test
    @DisplayName("WHEN GET endpoint is hit with a valid employee id as path variable THEN get the employee in database")
    void getEmployee_whenEmployeeFound() throws Exception {
        when(employeeRepository.findById(Long.valueOf(id))).thenReturn(Optional.of(employee));

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/employee/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        Employee actualResponse = objectMapper.readValue(response, new TypeReference<Employee>() {
        });

        verify(employeeRepository).findById(Long.valueOf(id));

        assertAll(
                () -> assertEquals(employee.getId(), actualResponse.getId()),
                () -> assertEquals(employee.getName(), actualResponse.getName()),
                () -> assertEquals(employee.getPhoneNumber(), actualResponse.getPhoneNumber()),
                () -> assertEquals(employee.getRole(), actualResponse.getRole())
        );
    }

    @Test
    @DisplayName("WHEN GET endpoint is hit with a invalid employee id as path variable THEN send no employee data")
    void getEmployee_whenEmployeeNotFound() throws Exception {
        when(employeeRepository.findById(Long.valueOf(id))).thenReturn(Optional.empty());

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/employee/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        assertEquals(response, "Employee not found");
        verify(employeeRepository).findById(Long.valueOf(id));
    }

    @Test
    @DisplayName("WHEN GET ALL endpoint is hit THEN send all employee data")
    void getAllEmployee() throws Exception {
        when(employeeRepository.findAll()).thenReturn(employeeList);

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
                () -> assertEquals(employeeList.get(0).getRole(), actualResponse.get(0).getRole())
        );
    }

    @Test
    @DisplayName("WHEN UPDATE endpoint is hit with a valid employee id as path variable THEN update the employee in database")
    void updateEmployee_GivenCorrectEmployee() throws Exception {
        Employee savedEmployee = Employee.builder()
                .id(Long.valueOf(id))
                .name("First Last")
                .role("programmer")
                .phoneNumber("111111111")
                .build();

        when(employeeRepository.findById(Long.valueOf(id))).thenReturn(Optional.of(employee));
        when(employeeRepository.save(savedEmployee)).thenReturn(savedEmployee);

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
                () -> assertEquals(savedEmployee.getId(), actualResponse.getId()),
                () -> assertEquals(savedEmployee.getName(), actualResponse.getName()),
                () -> assertEquals(savedEmployee.getPhoneNumber(), actualResponse.getPhoneNumber()),
                () -> assertEquals(savedEmployee.getRole(), actualResponse.getRole())
        );
    }

    @Test
    @DisplayName("WHEN UPDATE endpoint is hit with a invalid employee id as path variable THEN return employee not found message")
    void updateEmployee_GivenIncorrectEmployee() throws Exception {
        when(employeeRepository.findById(Long.valueOf(id))).thenReturn(Optional.empty());

        MvcResult mvcResult = mockMvc.perform(put("/api/v1/employee/{id}", id)
                .content(objectMapper.writeValueAsString(updatedEmployee))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        assertEquals(response, "Employee not found");
    }

    @Test
    @DisplayName("WHEN DELETE endpoint is hit with a valid employee id as path variable THEN delete that employee")
    void deleteEmployee_GivenValidEmployee() throws Exception {
        when(employeeRepository.findById(Long.valueOf(id))).thenReturn(Optional.of(employee));

        doNothing().when(employeeRepository).deleteById(Long.parseLong(id));

        MvcResult mvcResult = mockMvc.perform(delete("/api/v1/employee/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        assertEquals(response, "Employee deleted");
    }

    @Test
    @DisplayName("WHEN DELETE endpoint is hit with a invalid employee id as path variable THEN return employee not found message")
    void deleteEmployee_GivenInvalidEmployee() throws Exception {
        when(employeeRepository.findById(Long.valueOf(id))).thenReturn(Optional.empty());

        MvcResult mvcResult = mockMvc.perform(delete("/api/v1/employee/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        assertEquals(response, "Employee not found");
    }
}