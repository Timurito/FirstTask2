package main;

import entities.Department;
import entities.Employee;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static main.Constants.*;

public class TaskProcess {
    private int totalFoundCombs;
    private BufferedWriterWrap bw;
    private List<Department> departments = new ArrayList<>();
    private Map<String, Department> departmentNames = new HashMap<>();

    public TaskProcess(String inputFilename, String outputFilename) {
        bw = new BufferedWriterWrap(outputFilename);
        if (bw == null) {
            return;
        }
        bw.write("Reading file...\n");
        if (!readFile(inputFilename)) {
            return;
        }
        bw.write("Successfully read info:\n");
        for (Department d : departments) {
            bw.write(d.toString());
        }
        bw.write("________________________________________________________\n");
        processDepartments();
        bw.close();
    }

    private boolean readFile(String filename) {
        try (BufferedReader r = new BufferedReader(new FileReader((filename)))) {
            String temp;
            while ((temp = r.readLine()) != null) {
                String[] stringParts = temp.split(";");
                Employee newEmployee;
                if (stringParts.length != STRING_PARTS_NUM ||
                        (newEmployee = parseEmployee(stringParts)) == null) {
                    bw.write("[FAIL] failed to parse line: " + temp + "\n");
                } else {
                    addNewEmployeeToDepartment(newEmployee, stringParts[DEPARTMENT_NAME]);
                }
            }
            return true;
        } catch (IOException e) {
            System.out.println("[ERROR] IO error with input file operation.");
            return false;
        }
    }

    private void addNewEmployeeToDepartment(Employee newEmployee, String departmentName) {
        if (departmentNames.containsKey(departmentName)) {
            departmentNames.get(departmentName).addEmployee(newEmployee);
        } else {
            Department newDepartment = new Department(departmentName, newEmployee);
            departments.add(newDepartment);
            departmentNames.put(departmentName, newDepartment);
        }
    }

    private Employee parseEmployee(String[] stringParts) {
        BigDecimal salary = parseSalary(stringParts[EMPLOYEE_SALARY]);
        return salary != null ? new Employee(stringParts[EMPLOYEE_FULL_NAME], salary) : null;
    }

    private BigDecimal parseSalary(String str) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');
//        String pattern = "#,##0.0#";
        DecimalFormat decimalFormat = new DecimalFormat("", symbols);
        decimalFormat.setParseBigDecimal(true);
        BigDecimal salary;
        try {
            salary = (BigDecimal) decimalFormat.parse(str);
        } catch (ParseException e) {
            System.err.println("[ERROR] Failed to parse employee's salary");
            return null;
        }
        return salary.signum() >= 0 ? salary : null;
    }

    private void processDepartments() {
        for (int i = 0; i < departments.size(); i++) {
            for (int j = 0; j < departments.size(); j++) {
                if (i != j) {
                    new CalcCombination(departments.get(i), departments.get(j));
                }
            }
        }
    }

    private class CalcCombination {
        private Department department1;
        private Department department2;

        public CalcCombination(Department department1, Department department2) {
            this.department1 = department1;
            this.department2 = department2;
            int[] data = new int[department1.getStaff().size()];
            for (int i = 0; i < data.length; i++) {
                data[i] = i;
            }

            for (int i = 1; i < department1.getStaff().size(); i++) {
                int[] temp = new int[i];
                calculateCombination(data, temp, data.length, temp.length, 0, 0);
            }
        }

        private void calculateCombination(int[] data, int[] temp, int n, int k, int tempIndex, int dataIndex) {
            if (k == tempIndex) {
                compareDepartments(temp);
                return;
            }
            if (dataIndex >= n) {
                return;
            }
            temp[tempIndex] = data[dataIndex];
            calculateCombination(data, temp, n, k, tempIndex + 1, dataIndex + 1);
            calculateCombination(data, temp, n, k, tempIndex, dataIndex + 1);
        }

        private void compareDepartments(int[] dep1Comb) {
            List<Employee> dep1StaffByIndexes = getEmployeesByIndexes(department1, dep1Comb);
            BigDecimal dep1NewAvgSalary = department1.getSumSalary();
            for (Employee e : dep1StaffByIndexes) {
                dep1NewAvgSalary = dep1NewAvgSalary.subtract(e.getSalary());
            }
            dep1NewAvgSalary = dep1NewAvgSalary.divide(
                    new BigDecimal(department1.getStaffSize() - dep1StaffByIndexes.size()),
                    SCALE, RoundingMode.HALF_EVEN);
            BigDecimal dep2NewAvgSalary = department2.getSumSalary();
            for (Employee e : dep1StaffByIndexes) {
                dep2NewAvgSalary = dep2NewAvgSalary.add(e.getSalary());
            }
            dep2NewAvgSalary = dep2NewAvgSalary.divide(
                    new BigDecimal(department2.getStaffSize() + dep1StaffByIndexes.size()),
                    SCALE, RoundingMode.HALF_EVEN);
            BigDecimal dep1OrigAvgSalary = department1.getAvgSalary();
            BigDecimal dep2OrigAvgSalary = department2.getAvgSalary();
            if (dep1OrigAvgSalary.compareTo(dep1NewAvgSalary) == -1 && dep2OrigAvgSalary.compareTo(dep2NewAvgSalary) == -1) {
                totalFoundCombs++;
                StringBuilder sb = new StringBuilder("Found combination ").append(totalFoundCombs).append("\n")
                        .append("Combination of ").append(department1.getName()).append(" and ")
                        .append(department2.getName()).append("\n")
                        .append("Staff from ").append(department1.getName()).append(":\n");
                for (Employee e : dep1StaffByIndexes) {
                    sb.append(e).append("\n");
                }
                sb.append(department1.getName()).append(" <original average salary> - <new average salary>: ")
                        .append(dep1OrigAvgSalary).append(" - ").append(dep1NewAvgSalary).append("\n");
                sb.append(department2.getName()).append(" <original average salary> - <new average salary>: ")
                        .append(dep2OrigAvgSalary).append(" - ").append(dep2NewAvgSalary).append("\n\n");
                bw.write(sb.toString());
            }
        }

        private List<Employee> getEmployeesByIndexes(Department department, int[] indexes) {
            List<Employee> resList = new ArrayList<>();
            for (int i = 0; i < department.getStaff().size(); i++) {
                for (int j : indexes) {
                    if (i == j) {
                        resList.add(department.getStaff().get(i));
                        break;
                    }
                }
            }
            return resList;
        }
    }

    class BufferedWriterWrap {
        private BufferedWriter bw;

        public BufferedWriterWrap(String filename) {
            try {
                bw = new BufferedWriter(new FileWriter(filename));
            } catch (IOException e) {
                System.err.println("[ERROR] Failed to open/create output file");
            }
        }

        public void write(String msg) {
            try {
                bw.write(msg);
            } catch (IOException e) {
                System.err.println("[ERROR] Failed writing to output file");
            }
        }

        public void close() {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    System.err.println("[ERROR] Failed closing output file");
                }
            }
        }
    }
}