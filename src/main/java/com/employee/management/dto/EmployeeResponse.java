package com.employee.management.dto;

public record EmployeeResponse(
        Long id,
        String name,
        String email,
        Double salary
) {
}