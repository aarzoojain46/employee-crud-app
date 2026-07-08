package com.employee.management.controller;

import com.employee.management.dto.EmployeeRequest;
import com.employee.management.dto.EmployeeResponse;
import com.employee.management.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // Create Employee
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponse createEmployee(
            @Valid @RequestBody EmployeeRequest request) {

        return employeeService.createEmployee(request);
    }

    // Get Employee By Id
    @GetMapping("/{id}")
    public EmployeeResponse getEmployeeById(@PathVariable Long id) {

        return employeeService.getEmployeeById(id);
    }

    // Get All Employees
    @GetMapping
    public List<EmployeeResponse> getAllEmployees() {

        return employeeService.getAllEmployees();
    }

    // Update Employee
    @PutMapping("/{id}")
    public EmployeeResponse updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request) {

        return employeeService.updateEmployee(id, request);
    }

    // Delete Employee
    @DeleteMapping("/{id}")
    public String deleteEmployee(@PathVariable Long id) {

        employeeService.deleteEmployee(id);

        return "Employee deleted successfully";
    }
}