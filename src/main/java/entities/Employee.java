package entities;

import java.math.BigDecimal;

public class Employee {
    private String fullName;
    private BigDecimal salary;

    public Employee(String fullName, BigDecimal salary) {
        this.fullName = fullName;
        this.salary = salary;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    @Override
    public String toString() {
        return "Employee: " + fullName + ", " + salary;
    }
}
