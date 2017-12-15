package entities;

import java.math.BigDecimal;
import java.util.List;

public class Combination {
    private Department dep1;
    private Department dep2;
    private List<Employee> staffFromDep1;
    private List<Employee> staffFromDep2;
    private BigDecimal dep1NewAvgSalary;
    private BigDecimal dep2NewAvgSalary;

    public Combination(Department dep1, Department dep2, List<Employee> staffFromDep1, List<Employee> staffFromDep2,
                       BigDecimal dep1NewAvgSalary, BigDecimal dep2NewAvgSalary) {
        this.dep1 = dep1;
        this.dep2 = dep2;
        this.staffFromDep1 = staffFromDep1;
        this.staffFromDep2 = staffFromDep2;
        this.dep1NewAvgSalary = dep1NewAvgSalary;
        this.dep2NewAvgSalary = dep2NewAvgSalary;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Combination of ").append(dep1.getName()).append(" and ").append(dep2.getName()).append("\n")
                .append("Staff from ").append(dep1.getName()).append(":\n");
        for (Employee e : staffFromDep1) {
            sb.append(e).append("\n");
        }
        sb.append("Staff from ").append(dep2.getName()).append(":\n");
        for (Employee e : staffFromDep2) {
            sb.append(e).append("\n");
        }
        sb.append(dep1.getName()).append(" <original average salary> - <new average salary>: ")
                .append(dep1.getAvgSalary()).append(" - ").append(dep1NewAvgSalary).append("\n");
        sb.append(dep2.getName()).append(" <original average salary> - <new average salary>: ")
                .append(dep2.getAvgSalary()).append(" - ").append(dep2NewAvgSalary).append("\n\n");
        return sb.toString();
    }
}
