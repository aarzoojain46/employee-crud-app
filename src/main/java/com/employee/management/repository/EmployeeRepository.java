package com.employee.management.repository;

import com.employee.management.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Spring Data JPA generates the query from the method name automatically
    boolean existsByEmail(String email);

    Optional<Employee> findByEmail(String email);
}

// package com.employee.management.repository;

// import com.employee.management.entity.Employee;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;

// @Repository
// public interface EmployeeRepository extends JpaRepository<Employee, Long> {

// }