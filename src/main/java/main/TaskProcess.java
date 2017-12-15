package main;

import entities.Combination;
import entities.Department;
import entities.Employee;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaskProcess {
    private BufferedWriter bw;
    private int totalFoundCombs;

    public TaskProcess(String[] args) {
        String inputFilename = args[0];
        String outputFilename = args[1];
        boolean appendToOutputFile = args[2].equalsIgnoreCase("true");
        try {
            bw = new BufferedWriter(new FileWriter(outputFilename, appendToOutputFile));
            bw.write("Reading file...\n");
            List<Department> list = readFile(inputFilename);
            bw.write("Successfully read info:\n");
            for (Department d : list) {
                bw.write(d.toString());
            }
            bw.write("________________________________________________________\n");
            processDepartments(list);
        } catch (IOException e) {
            // log here?
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    // log here?
                }
            }
        }
    }

    private List<Department> readFile(String filename) {
        List<Department> res = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(new FileReader((filename)))) {
            String temp;
            while ((temp = r.readLine()) != null) {
                String[] stringParts = temp.split(";");
                Employee newEmployee;
                if (stringParts.length != 3 || (newEmployee = parseEmployee(stringParts)) == null) {
                    bw.write("[FAIL] failed to parse line: " + temp + "\n");
                } else {
                    addNewEmployeeToDepartment(res, newEmployee, stringParts[0]);
                }
            }
        } catch (IOException e) {
            // log here?
        }
        return res;
    }

    private void addNewEmployeeToDepartment(List<Department> res, Employee newEmployee, String departmentName) {
        for (Department currDepartment : res) {
            if (currDepartment.getName().equalsIgnoreCase(departmentName)) {
                currDepartment.addEmployee(newEmployee);
                return;
            }
        }
        res.add(new Department(departmentName, newEmployee));
    }

    private Employee parseEmployee(String[] stringParts) {
        BigDecimal salary = parseSalary(stringParts[2]);
        return salary != null ? new Employee(stringParts[1], salary) : null;
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
            e.printStackTrace();
            return null;
        }
        return salary.signum() >= 0 ? salary : null;
    }

    private void processDepartments(List<Department> departments) {
        for (int i = 0; i < departments.size(); i++) {
            for (int j = i + 1; j < departments.size(); j++) {
                compareDepartments(departments.get(i), departments.get(j));
            }
        }
    }

    private void compareDepartments(Department department1, Department department2) {
        // loop by number of elements for combinations (i-combinations from department1)
        for (int i = 0; i < department1.getStaff().size(); i++) {
            List<int[]> dep1Combinations = getAllCombinations(department1.getStaff().size(), i);
            // loop by combinations of department1
            for (int[] q : dep1Combinations) {
                // loop by number of elements for combinations (i-combinations from department2)
                for (int j = 0; j < department2.getStaff().size(); j++) {
                    List<int[]> dep2Combinations = getAllCombinations(department2.getStaff().size(), j);
                    // loop by combinations of department2
                    for (int[] k : dep2Combinations) {
                        compareCombinations(department1, department2, q, k);
                    }
                }
            }
        }
    }

    private List<int[]> getAllCombinations(int n, int k) {
        List<int[]> combinations = new ArrayList<>();
        int[] inputArr = new int[n];
        for (int i = 0; i < n; i++) {
            inputArr[i] = i;
        }
        getCombinations(combinations, inputArr, inputArr.length, k, 0, new int[k], 0);
        return combinations;
    }

    // this method is unconscionably stolen from the Internet
    private void getCombinations(List<int[]> resultList, int arr[], int n, int k, int index, int data[], int i) {
        // Current combination is ready to be saved
        if (index == k) {
            resultList.add(Arrays.copyOf(data, data.length));
            return;
        }
        // When no more elements are there to put in data[]
        if (i >= n) {
            return;
        }
        // current is included, put next at next location
        data[index] = arr[i];
        getCombinations(resultList, arr, n, k, index + 1, data, i + 1);
        // current is excluded, replace it with next (Note that
        // i+1 is passed, but index is not changed)
        getCombinations(resultList, arr, n, k, index, data, i + 1);
    }

    private void compareCombinations(Department department1, Department department2, int[] dep1Comb, int[] dep2Comb) {
        BigDecimal dep1OrigAvgSalary = department1.getAvgSalary();
        BigDecimal dep2OrigAvgSalary = department2.getAvgSalary();
        BigDecimal dep1CombAvgSalary = calcCombAvgSalary(department1, department2, dep1Comb, dep2Comb);
        BigDecimal dep2CombAvgSalary = calcCombAvgSalary(department2, department1, dep2Comb, dep1Comb);

        if (dep1OrigAvgSalary.compareTo(dep1CombAvgSalary) == -1 && dep2OrigAvgSalary.compareTo(dep2CombAvgSalary) == -1) {
            totalFoundCombs++;
            writeToFile("Found combination " + totalFoundCombs + "\n", new Combination(department1, department2,
                    getEmployeesByIndexes(department1, dep1Comb), getEmployeesByIndexes(department2, dep2Comb),
                    dep1CombAvgSalary, dep2CombAvgSalary).toString());
        }
    }

    private BigDecimal calcCombAvgSalary(Department department1, Department department2, int[] dep1Comb, int[] dep2Comb) {
        BigDecimal combAvgSalary = new BigDecimal(0);
        List<Employee> newDeptComb = new ArrayList<>();
        newDeptComb.addAll(getEmployeesByIndexesExceptOf(department1, dep1Comb));
        newDeptComb.addAll(getEmployeesByIndexes(department2, dep2Comb));
        for (Employee e : newDeptComb) {
            combAvgSalary = combAvgSalary.add(e.getSalary());
        }
        return combAvgSalary.divide(new BigDecimal(department1.getStaff().size() - dep1Comb.length + dep2Comb.length), RoundingMode.HALF_EVEN);
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

    private List<Employee> getEmployeesByIndexesExceptOf(Department department, int[] indexes) {
        List<Employee> resList = new ArrayList<>();
        outer:
        for (int i = 0; i < department.getStaff().size(); i++) {
            for (int j : indexes) {
                if (i == j) {
                    continue outer;
                }
            }
            resList.add(department.getStaff().get(i));
        }
        return resList;
    }

    private void writeToFile(String optionalMsg, String msg) {
        try {
            bw.write(optionalMsg + msg);
        } catch (IOException e) {
            // log here?
        }
    }
}