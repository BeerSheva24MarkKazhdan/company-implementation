package telran.employees;

import java.util.*;

import org.json.JSONObject;

import java.io.*;
import telran.io.Persistable;

public class CompanyImpl implements Company, Persistable {
    private TreeMap<Long, Employee> employees = new TreeMap<>();
    private HashMap<String, List<Employee>> employeesDepartment = new HashMap<>();
    private TreeMap<Float, List<Manager>> managersFactor = new TreeMap<>();

    private class CompanyIterator implements Iterator<Employee> {
        Iterator<Employee> iterator = employees.values().iterator();
        Employee lastIterated;

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Employee next() {
            lastIterated = iterator.next();
            return lastIterated;
        }

        @Override
        public void remove() {
            iterator.remove();
            removeFromIndexMaps(lastIterated);
        }
    }

    @Override
    public Iterator<Employee> iterator() {
        return new CompanyIterator();
    }

    @Override
    public void addEmployee(Employee empl) {
        long id = empl.getId();
        if (employees.putIfAbsent(id, empl) != null) {
            throw new IllegalStateException("Already exists employee " + id);
        }
        addIndexMaps(empl);
    }

    private void addIndexMaps(Employee empl) {
        employeesDepartment.computeIfAbsent(empl.getDepartment(), k -> new ArrayList<>()).add(empl);
        if (empl instanceof Manager manager) {
            managersFactor.computeIfAbsent(manager.getFactor(), k -> new ArrayList<>()).add(manager);
        }
    }

    @Override
    public Employee getEmployee(long id) {
        return employees.get(id);
    }

    @Override
    public Employee removeEmployee(long id) {
        Employee empl = employees.remove(id);
        if (empl == null) {
            throw new NoSuchElementException("Not found employee " + id);
        }
        removeFromIndexMaps(empl);
        return empl;
    }

    private void removeFromIndexMaps(Employee empl) {
        removeIndexMap(empl.getDepartment(), employeesDepartment, empl);
        if (empl instanceof Manager manager) {
            removeIndexMap(manager.getFactor(), managersFactor, manager);
        }
    }

    private <K, V extends Employee> void removeIndexMap(K key, Map<K, List<V>> map, V empl) {
        List<V> list = map.get(key);
        list.remove(empl);
        if (list.isEmpty()) {
            map.remove(key);
        }
    }

    @Override
    public int getDepartmentBudget(String department) {
        return employeesDepartment.getOrDefault(department, Collections.emptyList())
                .stream().mapToInt(Employee::computeSalary).sum();
    }

    @Override
    public String[] getDepartments() {
        return employeesDepartment.keySet().stream().sorted().toArray(String[]::new);
    }

    @Override
    public Manager[] getManagersWithMostFactor() {
        Manager[] res = new Manager[0];
        if (!managersFactor.isEmpty()) {
            res = managersFactor.lastEntry().getValue().toArray(res);
        }
        return res;
    }

    @Override
    public void saveToFile(String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(fileName))) {
            for (Employee employee : employees.values()) {
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("className", employee.getClass().getName());
                jsonObj.put("id", employee.getId());
                jsonObj.put("department", employee.getDepartment());
                jsonObj.put("basicSalary", employee.computeSalary());
                if (employee instanceof Manager manager) {
                    jsonObj.put("factor", manager.getFactor());
                } else if (employee instanceof SalesPerson salesPerson) {
                    jsonObj.put("wage", salesPerson.getWage());
                    jsonObj.put("hours", salesPerson.getHours());
                    jsonObj.put("percent", salesPerson.getPercent());
                    jsonObj.put("sales", salesPerson.getSales());
                } else if (employee instanceof WageEmployee wageEmployee) {
                    jsonObj.put("wage", wageEmployee.getWage());
                    jsonObj.put("hours", wageEmployee.getHours());
                }
                writer.println(jsonObj.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save data to file: " + fileName, e);
        }
    }

    @Override
    public void restoreFromFile(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                JSONObject jsonObj = new JSONObject(line); 
                Employee employee = textToEmployee(jsonObj); 
                addEmployee(employee);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to restore data from file: " + fileName, e);
        }
    }
    
    private Employee textToEmployee(JSONObject jsonObj) {
        Employee employee;
        String className = jsonObj.getString("className");
        long id = jsonObj.getLong("id");
        String department = jsonObj.getString("department");
        int basicSalary = jsonObj.getInt("basicSalary");
        if ("telran.employees.Manager".equals(className)) {
            float factor = jsonObj.getFloat("factor");
            employee = new Manager(id, basicSalary, department, factor); 
        } else if ("telran.employees.SalesPerson".equals(className)) {
            int wage = jsonObj.getInt("wage");
            int hours = jsonObj.getInt("hours");
            float percent = jsonObj.getFloat("percent");
            long sales = jsonObj.getLong("sales");
            employee = new SalesPerson(id, basicSalary, department, wage, hours, percent, sales); 
        } else if ("telran.employees.WageEmployee".equals(className)) {
            int wage = jsonObj.getInt("wage");
            int hours = jsonObj.getInt("hours");
            employee = new WageEmployee(id, basicSalary, department, wage, hours); 
        } else {
            employee = new Employee(id, basicSalary, department); 
        }
        return employee;
    }
}