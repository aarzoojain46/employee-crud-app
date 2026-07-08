package com.employee.management.service;

import com.employee.management.dto.EmployeeRequest;
import com.employee.management.dto.EmployeeResponse;
import com.employee.management.entity.Employee;
import com.employee.management.exception.EmployeeNotFoundException;
import com.employee.management.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Service-layer unit tests. The repository is mocked so we test only
 * EmployeeService's own logic (mapping, orchestration, exception throwing) -
 * no real database is involved.
 */
@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    // ---------- CREATE ----------

    @Test
    void createEmployee_validRequest_savesAndReturnsResponse() {
        EmployeeRequest request = new EmployeeRequest("Aarzoo", "aarzoo@example.com", 50000.0);

        // The repository doesn't know the id until save() is called - simulate that
        Employee savedEmployee = new Employee(1L, "Aarzoo", "aarzoo@example.com", 50000.0);

        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);

        EmployeeResponse response = employeeService.createEmployee(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Aarzoo", response.name());
        assertEquals("aarzoo@example.com", response.email());
        assertEquals(50000.0, response.salary());

        // Capture what was actually passed to save() to confirm mapping was correct
        ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository, times(1)).save(captor.capture());

        Employee captured = captor.getValue();
        assertEquals("Aarzoo", captured.getName());
        assertEquals("aarzoo@example.com", captured.getEmail());
        assertEquals(50000.0, captured.getSalary());
    }

    // ---------- GET BY ID ----------

    @Test
    void getEmployeeById_existingId_returnsResponse() {
        Employee employee = new Employee(1L, "Aarzoo", "aarzoo@example.com", 50000.0);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        EmployeeResponse response = employeeService.getEmployeeById(1L);

        assertEquals(1L, response.id());
        assertEquals("Aarzoo", response.name());
        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    void getEmployeeById_nonExistingId_throwsEmployeeNotFoundException() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        EmployeeNotFoundException ex = assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.getEmployeeById(99L)
        );

        assertEquals("Employee not found with id: 99", ex.getMessage());
        verify(employeeRepository, times(1)).findById(99L);
    }

    // ---------- GET ALL ----------

    @Test
    void getAllEmployees_returnsMappedList() {
        List<Employee> employees = List.of(
                new Employee(1L, "Aarzoo", "aarzoo@example.com", 50000.0),
                new Employee(2L, "Rohan", "rohan@example.com", 60000.0)
        );

        when(employeeRepository.findAll()).thenReturn(employees);

        List<EmployeeResponse> responses = employeeService.getAllEmployees();

        assertEquals(2, responses.size());
        assertEquals("Aarzoo", responses.get(0).name());
        assertEquals("Rohan", responses.get(1).name());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void getAllEmployees_noEmployees_returnsEmptyList() {
        when(employeeRepository.findAll()).thenReturn(List.of());

        List<EmployeeResponse> responses = employeeService.getAllEmployees();

        assertTrue(responses.isEmpty());
    }

    // ---------- UPDATE ----------

    @Test
    void updateEmployee_existingId_updatesAndReturnsResponse() {
        Employee existingEmployee = new Employee(1L, "Aarzoo", "aarzoo@example.com", 50000.0);
        Employee updatedEmployee = new Employee(1L, "Aarzoo Updated", "aarzoo@example.com", 55000.0);

        EmployeeRequest request = new EmployeeRequest("Aarzoo Updated", "aarzoo@example.com", 55000.0);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);

        EmployeeResponse response = employeeService.updateEmployee(1L, request);

        assertEquals("Aarzoo Updated", response.name());
        assertEquals(55000.0, response.salary());

        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).save(existingEmployee);
    }

    @Test
    void updateEmployee_nonExistingId_throwsEmployeeNotFoundException() {
        EmployeeRequest request = new EmployeeRequest("Ghost", "ghost@example.com", 40000.0);

        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.updateEmployee(99L, request)
        );

        // Save should never be reached if the employee wasn't found
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    // ---------- DELETE ----------

    @Test
    void deleteEmployee_existingId_deletesSuccessfully() {
        Employee employee = new Employee(1L, "Aarzoo", "aarzoo@example.com", 50000.0);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        doNothing().when(employeeRepository).delete(employee);

        employeeService.deleteEmployee(1L);

        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).delete(employee);
    }

    @Test
    void deleteEmployee_nonExistingId_throwsEmployeeNotFoundExceptionAndNeverDeletes() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.deleteEmployee(99L)
        );

        verify(employeeRepository, never()).delete(any(Employee.class));
    }
}