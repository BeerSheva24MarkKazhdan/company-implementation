package telran.employees;

import java.util.*;

public class CompanyImpl implements Company {
    private TreeMap<Long, Employee> employees = new TreeMap<>();
    private HashMap<String, List<Employee>> employeesDepartment = new HashMap<>();
    private TreeMap<Float, List<Manager>> managersFactor = new TreeMap<>();

    @Override
    public Iterator<Employee> iterator() {
        return new Iterator<Employee>() {
            private final Iterator<Map.Entry<Long, Employee>> it = employees.entrySet().iterator();
            private Employee current = null;

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Employee next() {
                Map.Entry<Long, Employee> entry = it.next();
                current = entry.getValue();
                return current;
            }

            @Override
            public void remove() {
                if (current == null) {
                    throw new IllegalStateException("");
                }
                it.remove();

                
                String department = current.getDepartment();
                List<Employee> employeeList = employeesDepartment.get(department);
                if (employeeList != null) {
                    employeeList.remove(current);
                    if (employeeList.isEmpty()) {
                        employeesDepartment.remove(department);
                    }
                }

                if (current instanceof Manager) {
                    Manager manager = (Manager) current;
                    float factor = manager.getFactor();
                    List<Manager> managerList = managersFactor.get(factor);
                    if (managerList != null) {
                        managerList.remove(manager);
                        if (managerList.isEmpty()) {
                            managersFactor.remove(factor);
                        }
                    }
                }

                current = null; 
            }
        };
    }

    @Override
    public void addEmployee(Employee empl) {
        if (!employees.containsKey(empl.getId())) {
            employees.put(empl.getId(), empl);
            String department = empl.getDepartment();
            List<Employee> employeeList = employeesDepartment.get(department);

            if (employeeList != null) {
                employeeList.add(empl);
            } else {
                employeeList = new ArrayList<>();
                employeeList.add(empl);
                employeesDepartment.put(department, employeeList);
            }

            if (empl instanceof Manager) {
                Manager manager = (Manager) empl;
                float factor = manager.getFactor();

                List<Manager> managerList = managersFactor.get(factor);
                if (managerList != null) {
                    managerList.add(manager);
                } else {
                    managerList = new ArrayList<>();
                    managerList.add(manager);
                    managersFactor.put(factor, managerList);
                }
            }
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public Employee getEmployee(long id) {
        Employee result = null;
        if (employees.containsKey(id)) {
            result = employees.get(id);
        }
        return result;
    }

    @Override
    public Employee removeEmployee(long id) {
        Employee removedEmployee = employees.remove(id);

        if (removedEmployee != null) {
            String department = removedEmployee.getDepartment();
            List<Employee> employeeList = employeesDepartment.get(department);

            if (employeeList != null) {
                employeeList.remove(removedEmployee);
                if (employeeList.isEmpty()) {
                    employeesDepartment.remove(department);
                }
            }

            if (removedEmployee instanceof Manager) {
                Manager manager = (Manager) removedEmployee;
                float factor = manager.getFactor();
                List<Manager> managerList = managersFactor.get(factor);
                if (managerList != null) {
                    managerList.remove(manager);
                    if (managerList.isEmpty()) {
                        managersFactor.remove(factor);
                    }
                }
            }
            return removedEmployee;
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public int getDepartmentBudget(String department) {
        List<Employee> emplInDep = employeesDepartment.get(department);
        int totalBudget = 0;
        if (emplInDep != null && !emplInDep.isEmpty()) {
            for (Employee empl : emplInDep) {
                totalBudget = totalBudget + empl.computeSalary();
            }
        }
        return totalBudget;
    }

    @Override
    public String[] getDepartments() {
        String[] result = employeesDepartment.keySet().toArray(new String[0]);
        Arrays.sort(result);
        return result;
    }

    @Override
    public Manager[] getManagersWithMostFactor() {
        Manager[] resultArray = new Manager[0];
        if (!managersFactor.isEmpty()) {
            Float maxFactor = managersFactor.lastKey();
            List<Manager> managersWithMaxFactor = managersFactor.get(maxFactor);
            resultArray = managersWithMaxFactor.toArray(new Manager[0]);
        }
        return resultArray;
    }

}
