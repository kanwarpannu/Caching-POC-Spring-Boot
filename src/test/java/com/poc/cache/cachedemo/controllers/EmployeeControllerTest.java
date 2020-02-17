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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EmployeeController.class)
@DisplayName("GIVEN employee controller is called")
class EmployeeControllerTest {

    //TODO: pending test cases

    @MockBean
    private EmployeeRepository employeeRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    Employee employee = Employee.builder()
            .id((long) 1)
            .name("John Doe")
            .role("Programmer")
            .phoneNumber("1234567890")
            .build();

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
                () -> assertEquals(actualResponse.getId(), employee.getId()),
                () -> assertEquals(actualResponse.getName(), employee.getName()),
                () -> assertEquals(actualResponse.getPhoneNumber(), employee.getPhoneNumber()),
                () -> assertEquals(actualResponse.getRole(), employee.getRole())
        );

    }

    @Test
    void getEmployee() {
    }

    @Test
    void getAllEmployee() {
    }

    @Test
    void updateEmployee() {
    }

    @Test
    void deleteEmployee() {
    }
}