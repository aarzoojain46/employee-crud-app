package com.employee.management.service;

import com.employee.management.dto.EmployeeRequest;
import com.employee.management.dto.EmployeeResponse;
import com.employee.management.entity.Employee;
import com.employee.management.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import com.employee.management.exception.EmployeeNotFoundException;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // Create Employee
    public EmployeeResponse createEmployee(EmployeeRequest request) {

        Employee employee = new Employee();

        employee.setName(request.name());
        employee.setEmail(request.email());
        employee.setSalary(request.salary());

        Employee savedEmployee = employeeRepository.save(employee);

        return mapToResponse(savedEmployee);
    }

    // Get Employee By Id
    public EmployeeResponse getEmployeeById(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        return mapToResponse(employee);
    }

    // Get All Employees
    public List<EmployeeResponse> getAllEmployees() {

        return employeeRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Update Employee
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        employee.setName(request.name());
        employee.setEmail(request.email());
        employee.setSalary(request.salary());

        Employee updatedEmployee = employeeRepository.save(employee);

        return mapToResponse(updatedEmployee);
    }

    // Delete Employee
    public void deleteEmployee(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        employeeRepository.delete(employee);
    }

    // Helper Method
    private EmployeeResponse mapToResponse(Employee employee) {

        return new EmployeeResponse(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getSalary()
        );
    }
}