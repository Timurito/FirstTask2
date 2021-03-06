package entities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static main.Constants.SCALE;

public class Department {
    private String name;
    private List<Employee> staff;

    public Department(String name, Employee... newStaff) {
        this.name = name.toUpperCase();
        getStaff().addAll(Arrays.asList(newStaff));
    }

    public List<Employee> getStaff() {
        return staff == null ? staff = new ArrayList<>() : staff;
    }

    public String getName() {
        return name;
    }

    public void addEmployee(Employee newEmployee) {
        getStaff().add(newEmployee);

    }

    public BigDecimal getAvgSalary() {
        return getSumSalary().divide(new BigDecimal(getStaff().size()), SCALE, RoundingMode.HALF_EVEN);
    }

    public BigDecimal getSumSalary() {
        BigDecimal sumSalary = new BigDecimal(0);
        for (Employee e : getStaff()) {
            sumSalary = sumSalary.add(e.getSalary());
        }
        return sumSalary;
    }

    public int getStaffSize() {
        return getStaff().size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Department: ").append(name).append("\n");
        sb.append("Average salary of department: ").append(getAvgSalary()).append("\n");
        for (Employee e : getStaff()) {
            sb.append(e).append("\n");
        }
        return sb.append("\n").toString();
    }
}
