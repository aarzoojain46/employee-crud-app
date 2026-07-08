package com.employee.management.controller;

import com.employee.management.dto.EmployeeRequest;
import com.employee.management.dto.EmployeeResponse;
import com.employee.management.exception.EmployeeNotFoundException;
import com.employee.management.exception.GlobalExceptionHandler;
import com.employee.management.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller-layer unit tests using Mockito to mock the service layer.
 * MockMvc is built in standalone mode so the GlobalExceptionHandler is
 * exercised the same way it would be in a full Spring context.
 */
@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ---------- CREATE ----------

    @Test
    void createEmployee_validRequest_returns201AndBody() throws Exception {
        EmployeeRequest request = new EmployeeRequest("Aarzoo", "aarzoo@example.com", 50000.0);
        EmployeeResponse response = new EmployeeResponse(1L, "Aarzoo", "aarzoo@example.com", 50000.0);

        when(employeeService.createEmployee(any(EmployeeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Aarzoo"))
                .andExpect(jsonPath("$.email").value("aarzoo@example.com"))
                .andExpect(jsonPath("$.salary").value(50000.0));

        verify(employeeService, times(1)).createEmployee(any(EmployeeRequest.class));
    }

    @Test
    void createEmployee_blankName_returns400WithValidationError() throws Exception {
        // name is blank, which should fail @NotBlank before hitting the service
        EmployeeRequest invalidRequest = new EmployeeRequest("", "aarzoo@example.com", 50000.0);

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.path").value("/employees"))
                .andExpect(jsonPath("$.errors.name").exists());

        verify(employeeService, never()).createEmployee(any(EmployeeRequest.class));
    }

    @Test
    void createEmployee_invalidEmail_returns400WithValidationError() throws Exception {
        EmployeeRequest invalidRequest = new EmployeeRequest("Aarzoo", "not-an-email", 50000.0);

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").exists());

        verify(employeeService, never()).createEmployee(any(EmployeeRequest.class));
    }

    // ---------- GET BY ID ----------

    @Test
    void getEmployeeById_existingId_returns200AndBody() throws Exception {
        EmployeeResponse response = new EmployeeResponse(1L, "Aarzoo", "aarzoo@example.com", 50000.0);

        when(employeeService.getEmployeeById(1L)).thenReturn(response);

        mockMvc.perform(get("/employees/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Aarzoo"));

        verify(employeeService, times(1)).getEmployeeById(1L);
    }

    @Test
    void getEmployeeById_nonExistingId_returns404WithStandardBody() throws Exception {
        when(employeeService.getEmployeeById(99L))
                .thenThrow(new EmployeeNotFoundException(99L));

        mockMvc.perform(get("/employees/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Employee not found with id: 99"))
                .andExpect(jsonPath("$.path").value("/employees/99"));

        verify(employeeService, times(1)).getEmployeeById(99L);
    }

    // ---------- GET ALL ----------

    @Test
    void getAllEmployees_returnsListOf200() throws Exception {
        List<EmployeeResponse> employees = List.of(
                new EmployeeResponse(1L, "Aarzoo", "aarzoo@example.com", 50000.0),
                new EmployeeResponse(2L, "Rohan", "rohan@example.com", 60000.0)
        );

        when(employeeService.getAllEmployees()).thenReturn(employees);

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Aarzoo"))
                .andExpect(jsonPath("$[1].name").value("Rohan"));

        verify(employeeService, times(1)).getAllEmployees();
    }

    // ---------- UPDATE ----------

    @Test
    void updateEmployee_existingId_returns200AndUpdatedBody() throws Exception {
        EmployeeRequest request = new EmployeeRequest("Aarzoo Updated", "aarzoo@example.com", 55000.0);
        EmployeeResponse response = new EmployeeResponse(1L, "Aarzoo Updated", "aarzoo@example.com", 55000.0);

        when(employeeService.updateEmployee(eq(1L), any(EmployeeRequest.class))).thenReturn(response);

        mockMvc.perform(put("/employees/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Aarzoo Updated"))
                .andExpect(jsonPath("$.salary").value(55000.0));

        verify(employeeService, times(1)).updateEmployee(eq(1L), any(EmployeeRequest.class));
    }

    @Test
    void updateEmployee_nonExistingId_returns404() throws Exception {
        EmployeeRequest request = new EmployeeRequest("Ghost", "ghost@example.com", 40000.0);

        when(employeeService.updateEmployee(eq(99L), any(EmployeeRequest.class)))
                .thenThrow(new EmployeeNotFoundException(99L));

        mockMvc.perform(put("/employees/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Employee not found with id: 99"));
    }

    // ---------- DELETE ----------

    @Test
    void deleteEmployee_existingId_returns200WithMessage() throws Exception {
        doNothing().when(employeeService).deleteEmployee(1L);

        mockMvc.perform(delete("/employees/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Employee deleted successfully"));

        verify(employeeService, times(1)).deleteEmployee(1L);
    }

    @Test
    void deleteEmployee_nonExistingId_returns404() throws Exception {
        doThrow(new EmployeeNotFoundException(99L)).when(employeeService).deleteEmployee(99L);

        mockMvc.perform(delete("/employees/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Employee not found with id: 99"));

        verify(employeeService, times(1)).deleteEmployee(99L);
    }
}